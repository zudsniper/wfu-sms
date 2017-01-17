package cc.holstr.wfu.google;

import cc.holstr.wfu.model.Item;
import cc.holstr.wfu.properties.Unpacker;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * Created by jason on 1/6/17.
 */
public class Stockist extends TreeMap<String, Item> {

	private Drive drive;
	private SheetsIO sheetsIO;

	private String spreadsheetID;
	private Credential credential;

	public Stockist() {
		build();
	}

	public void build() {
		credential = authorise();
		drive = initialiseDrive();
		spreadsheetID = Unpacker.SPREADSHEET_ID;
		sheetsIO = new SheetsIO(credential,spreadsheetID);
		fromStockument();
	}

	public void add(Item item) {
		super.put(item.getName(), item);
	}

	private Credential authorise() {
		Credential credential = null;

		Set<String> scopes = new HashSet<String>();
		scopes.add("https://www.googleapis.com/auth/drive");
		scopes.add("https://www.googleapis.com/auth/spreadsheets");

		try {
			credential = GoogleCredential.fromStream(this.getClass().getClassLoader().getResourceAsStream("credentials/WFU-stockist-credentials.json"))
					.createScoped(scopes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return credential;

	}

	private Drive initialiseDrive() {
		try {
			HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
			Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
					"wfu-stockist").build();
			return drive;
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String listStock() {
		String stock = "Item name   Price \n";
		for(Item item : this.values()) {
			stock+=item.getName() + "     $" + item.getPrice() + "\n";
		}
		return stock;
	}

	public void fromStockument() {
		clear();
		String[][] contents = sheetsIO.readRange("stock",SheetsIO.getRangeFromDimension(sheetsIO.sheets.get("stock").getDimensions()));
		for(int r = 1; r<contents.length; r++) {
			add(new Item(contents[r][0],Double.parseDouble(contents[r][1]), Integer.parseInt(contents[r][2])));
		}
	}

	public void toStockument() {
		String[][] contents = sheetsIO.readRange("stock",SheetsIO.getRangeFromDimension(sheetsIO.sheets.get("stock").getDimensions()));
		for(int r = 1; r<contents.length; r++) {
			Item item = get(contents[r][0]);
			if(item!=null) {
				contents[r][2]=""+item.getQuantity();
			}
		}
		sheetsIO.writeRange("stock",
				SheetsIO.getRangeFromDimension(sheetsIO.sheets.get("stock").getDimensions()),
				"USER_ENTERED",
				contents);
	}
	public void showDocuments() {
		try {
			List<File> files = drive.files().list().execute().getItems();
			System.out.println("DRIVE DOCUMENTS: ");
			for(File file : files) {
				System.out.println(file.getTitle() +" : "+file.getId()+"   "+file.getCreatedDate());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Deprecated
	public void deleteDocuments() {
		try {
			List<File> files = drive.files().list().execute().getItems();
			for(File file : files) {
				drive.files().delete(file.getId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public void newStockDocument() {
		JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
			@Override
			public void onFailure(GoogleJsonError e,
								  HttpHeaders responseHeaders)
					throws IOException {
				// Handle error
				System.err.println(e.getMessage());
			}

			@Override
			public void onSuccess(Permission permission,
								  HttpHeaders responseHeaders)
					throws IOException {
				System.out.println("Permission ID: " + permission.getId());
			}
		};

		try {
			File body = new File();
			body.setTitle("STOCK-DOC");
			body.setDescription("STOCKIST DOCUMENT");
			body.setMimeType("application/vnd.google-apps.spreadsheet");

			File file = drive.files().insert(body).execute();

			spreadsheetID = file.getId();

			BatchRequest batch = drive.batch();

			for(String email : Unpacker.STATIC_INFO.getStockUsers()) {
				Permission perm = new Permission()
						.setType("user")
						.setRole("writer")
						.setEmailAddress(email)
						.setValue(email);
				System.out.println(email);
				drive.permissions().insert(spreadsheetID, perm)
						.setFields("id")
						.queue(batch, callback);
			}

			batch.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

package cc.holstr.wfu.google.stocking;

import cc.holstr.wfu.google.GoogleSheetsManager;
import cc.holstr.wfu.google.SheetsIO;
import cc.holstr.wfu.model.Item;
import cc.holstr.wfu.properties.Unpacker;
import com.google.api.client.auth.oauth2.Credential;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * Created by jason on 1/6/17.
 */
public class Stockist extends GoogleSheetsManager {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private TreeMap<String, Item> stocks;

	public Stockist() {
		build("wfu-stockist","credentials/WFU-stockist-credentials.json");
	}

	public void build(String appName, String credentialPath) {
		super.build(appName, credentialPath, Unpacker.STOCK_SPREADSHEET_ID);
		stocks = new TreeMap<>();
		fromStockument();
	}

	public void add(Item item) {stocks.put(item.getName(), item);
	}

	public String listStock() {
		String stock = "Item name   Price \n";
		for(Item item : stocks.values()) {
			stock+=item.getName() + "     $" + item.getPrice() + "\n";
		}
		return stock;
	}

	public void fromStockument() {
		stocks.clear();
		String[][] contents = sheetsIO.readRange("stock",SheetsIO.getRangeFromDimension(sheetsIO.sheets.get("stock").getDimensions()));
		for(int r = 1; r<contents.length; r++) {
			add(new Item(contents[r][0],Double.parseDouble(contents[r][1]), Integer.parseInt(contents[r][2])));
		}
	}

	public void toStockument() {
		String[][] contents = sheetsIO.readRange("stock",SheetsIO.getRangeFromDimension(sheetsIO.sheets.get("stock").getDimensions()));
		for(int r = 1; r<contents.length; r++) {
			Item item = stocks.get(contents[r][0]);
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

	public TreeMap<String, Item> getStocks() {
		return stocks;
	}
}

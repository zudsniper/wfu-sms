package cc.holstr.wfu.google.statistics;

import cc.holstr.util.RegExp;
import cc.holstr.wfu.google.GoogleSheetsManager;
import cc.holstr.wfu.google.SheetsIO;
import cc.holstr.wfu.model.Purchase;
import cc.holstr.wfu.properties.Unpacker;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by jason on 2/2/17.
 */
public class StatsManager extends GoogleSheetsManager{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public StatsManager() {
		this.build("wfu-statistics","credentials/WFU-statistics-credentials.json");
	}

	public void build(String appName, String credentialPath) {
		super.build(appName, credentialPath, Unpacker.STATS_SPREADSHEET_ID);
	}

	public String addPurchase(Purchase purchase) {
		String[][] contents = sheetsIO.getContents("stats");
		if(contents!=null) {
			logger.debug("Recieved contents");
		} else {
			logger.error("Didn't recieve contents");
		}
		/*int writeRow = -1;
		clearRowLoop:
		for(int r = 0; r<contents.length; r++) {
			boolean clear = true;
			for(int c = 0; c<contents[r].length;c++) {
				if(!RegExp.contains(contents[r][c],"( )+")) {
					clear = false;
				}
			}
			if(clear) {
				writeRow = r;
				break clearRowLoop;
			}
		}
		logger.debug("found clear row at: " + writeRow);*/

		String[][] newContents = Arrays.copyOf(contents,contents.length+1);
		newContents[contents.length]=new String[contents[contents.length-1].length];
		//1 TimeStamp, 2 Number, 3 Cart, 4 Cart Total, 5 Merchant, 6 Time, 7 Place, 8 Payment Method, 9 Time Spend On Bot,
		double timeSpent = purchase.millisSinceStart() / (double)60000;
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy E");
		newContents[contents.length][0] = df.format(new Date());
		newContents[contents.length][1] = purchase.getNumber();
		newContents[contents.length][2] = purchase.getCart().contents();
		newContents[contents.length][3] = "" + purchase.getCart().getTotal();
		newContents[contents.length][4] = purchase.getMerchant().getName();
		newContents[contents.length][5] = purchase.getTimeAndPlace().getTime();
		newContents[contents.length][6] = purchase.getTimeAndPlace().getPlace();
		newContents[contents.length][7] = purchase.getPaymentType().getDisplayName();
		newContents[contents.length][8] = "" + timeSpent;
		logger.debug("Writing to contents...");
		sheetsIO.setContents("stats", newContents);
		return ""+timeSpent;
	}

	@Deprecated
	public void newStatsDocument() {
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
			body.setTitle("STATS-DOC");
			body.setDescription("STATISTICS DOCUMENT");
			body.setMimeType("application/vnd.google-apps.spreadsheet");

			File file = drive.files().insert(body).execute();

			spreadsheetID = file.getId();

			logger.warn("STATS_SPREADSHEET_ID:"+spreadsheetID);

			BatchRequest batch = drive.batch();

			for(String email : Unpacker.STATIC_INFO.getStatsUsers()) {
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

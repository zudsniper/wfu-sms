package cc.holstr.wfu.google;

import cc.holstr.wfu.google.http.HandledGoogleHttpHelper;
import cc.holstr.wfu.google.model.OutputSheet;
import com.google.api.client.auth.oauth2.Credential;

import javax.json.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jason on 1/6/17.
 */
public class SheetsIO {

	private final GoogleJsonGenerator jsonGenerator = new GoogleJsonGenerator();
	private String spreadsheetID;
	private HandledGoogleHttpHelper httpHelper;

	public HashMap<String, OutputSheet> sheets;

	public SheetsIO(Credential credential, String spreadsheetID) {
		build(credential,spreadsheetID);
	}

	public void build(Credential credential, String spreadsheetID) {
		this.spreadsheetID = spreadsheetID;
		httpHelper = new HandledGoogleHttpHelper(credential);
		sheets = getUpdatedSheets();
	}


	private HashMap<String, OutputSheet> getUpdatedSheets() {
		// get sheet info from currentSpreadsheet
		long getTime;
		HashMap<String, OutputSheet> sheets = new HashMap<>();
		//ArrayList<OutputSheet> sheets = new ArrayList<OutputSheet>();
		InputStream response = httpHelper.get(
				"https://sheets.googleapis.com/v4/spreadsheets/" + spreadsheetID + "?&fields=sheets.properties");
		getTime = System.currentTimeMillis();
		JsonReader reader = Json.createReader(response);
		JsonObject obj = reader.readObject();
		JsonArray sheetsArray = obj.getJsonArray("sheets");
		for (JsonObject sheet : sheetsArray.getValuesAs(JsonObject.class)) {
			JsonObject prop = sheet.getJsonObject("properties");
			OutputSheet temp = new OutputSheet(prop.getJsonNumber("sheetId").longValue(), prop.getString("title"),
					prop.getInt("index"));
			JsonObject gridProp = prop.getJsonObject("gridProperties");
			temp.setDimensions(gridProp.getInt("rowCount"), gridProp.getInt("columnCount"));
			temp.setUpdateTime(getTime);
			sheets.put(temp.getTitle(),temp);
		}
		return sheets;
	}

	public String makeSheet(String title, int rows, int cols) {
		// create a new sheet in currentSpreadsheet, and return OutputSheet
		// object with its info
		JsonObject newSheet = jsonGenerator.getNewSheetObject(title, rows, cols);
		InputStream response = httpHelper.post(
				"https://sheets.googleapis.com/v4/spreadsheets/" + spreadsheetID + ":batchUpdate",
				newSheet.toString());
		if (response != null) {
			//success!
		}
		return response.toString();
	}

	public void deleteSheet(long sheetID) {
		// delete a sheet from currentSpreadsheet, and return OutputSheet object
		// with its info
		JsonObject deleteSheet = jsonGenerator.getDeleteSheetObject(sheetID);
		InputStream response = httpHelper.post(
				"https://sheets.googleapis.com/v4/spreadsheets/" + spreadsheetID + ":batchUpdate",
				deleteSheet.toString());
		if (response != null) {
			//success!
		}
	}

	public void clearSheet(long sheetID) {
		String url = "https://sheets.googleapis.com/v4/spreadsheets/" + spreadsheetID + ":batchUpdate";
		JsonObject clearSheet = jsonGenerator.getClearSheetObject(sheetID);
		InputStream response = httpHelper.post(url, clearSheet.toString());
		if (response != null) {
			//success!
		}
	}

	public void writeRange(String sheetTitle, String range, String valueInputOption, String[][] writeVals) {
		// set a specified range from currentSpreadsheet to provided string
		// matrix.
		/*
		 * example usage: writeRange("Sheet1","A1:D4","USER_ENTERED",{
		 * {"hello","world"}, {"meme","temp"} });
		 */
		int terms = 0;
		String url = "https://sheets.googleapis.com/v4/spreadsheets/" + spreadsheetID + "/values/" + sheetTitle
				+ "!" + range + "?valueInputOption=" + valueInputOption;
		JsonArrayBuilder values = Json.createArrayBuilder();
		for (int r = 0; r < writeVals.length; r++) {
			JsonArrayBuilder temp = Json.createArrayBuilder();
			for (int c = 0; c < writeVals[r].length; c++) {
				temp.add(writeVals[r][c]);
				if (!writeVals[r][c].equals("")) {
					terms++;
				}
			}
			values.add(temp);
		}
		JsonObjectBuilder write = Json.createObjectBuilder().add("range", sheetTitle + "!" + range)
				.add("majorDimension", "ROWS").add("values", values);
		InputStream response = httpHelper.put(url, write.build().toString());
		if (response != null) {
			//success!
		}
	}

	public String[][] readRange(String sheetTitle, String range) {
		// retrieve a specified range from currentSpreadsheet, return string
		// matrix with its data.
		// example usage: readRange("Sheet1","A1:D4");
		boolean success = true;
		List<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		String[][] vals;
		int terms = 0, nulls = 0;
		String url = "https://sheets.googleapis.com/v4/spreadsheets/" + spreadsheetID + "/values/" + sheetTitle
				+ "!" + range;
		InputStream response = httpHelper.get(url);
		if (response == null) {
			success = false;
		}
		// TODO: validation maybe?
		JsonReader reader = Json.createReader(response);
		JsonObject obj = reader.readObject();
		if (obj.toString().contains("\"values\":")) {
			JsonArray sheetsArray = obj.getJsonArray("values");
			for (JsonArray rows : sheetsArray.getValuesAs(JsonArray.class)) {
				ArrayList<String> temp = new ArrayList<String>();
				for (JsonValue value : rows) {
					temp.add(parse(value.toString()));
					terms++;
				}
				data.add(temp);
			}
			// get longest row
			int biggest = 0;
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).size() > biggest) {
					biggest = data.get(i).size();
				}
			}
			vals = new String[data.size()][biggest];
			for (int r = 0; r < data.size(); r++) {
				for (int c = 0; c < data.get(r).size(); c++) {
					vals[r][c] = data.get(r).get(c);
				}
			}
			// make safe for outside use
			for (int r = 0; r < vals.length; r++) {
				for (int c = 0; c < vals[r].length; c++) {
					if (vals[r][c] == null) {
						vals[r][c] = new String();
					}
				}
			}
		} else {
			vals = new String[1][1];
		}
		return vals;
	}

	private static String parse(String value) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			if (!(value.charAt(i) == '"' || value.charAt(i) == '\\')) {
				str.append(value.charAt(i));
			} else if (value.charAt(i) == '"') {
				if (i - 1 >= 0) {
					if (value.charAt(i - 1) == '\\') {
						str.append(value.charAt(i));
					}
				}
			}
		}
		return str.toString();
	}

	public static String getAlphabetValue(int num) {
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder b = new StringBuilder();
		while(num>26) {
			b.append(alphabet.charAt((num%26)-1));
			num = num/26;
		}
		b.append(alphabet.charAt((num)-1));
		return b.reverse().toString();
	}

	public static String getRangeFromDimension(Dimension dim) {
		return getRangeFromDimensions((int)dim.getWidth(), (int)dim.getHeight());
	}

	public static String getRangeFromDimensions(int row, int col) {
		String range = "A1:";
		range = range + getAlphabetValue(col) + row;
		return range;
	}

	private class GoogleJsonGenerator {
		public JsonObject getNewSheetObject(String title, int row, int col) {
			//create jsonObject to send to sheets api
			JsonObject newSheet = Json.createObjectBuilder()
					.add("requests",Json.createArrayBuilder()
							.add(Json.createObjectBuilder()
									.add("addSheet", Json.createObjectBuilder()
											.add("properties", Json.createObjectBuilder()
													.add("title", ""+title)
													.add("gridProperties", Json.createObjectBuilder()
															.add("rowCount", row) //100 is a guess, TBD
															.add("columnCount",col) //number of weeks in a year + 1 label column
													)
											)
									)
							)
					).build();
			return newSheet;
		}

		public JsonObject getDeleteSheetObject(long sheetID) {
			JsonObject deleteSheet = Json.createObjectBuilder()
					.add("requests",Json.createArrayBuilder()
							.add(Json.createObjectBuilder()
									.add("deleteSheet", Json.createObjectBuilder()
											.add("sheetId", sheetID)
									)
							)
					).build();
			return deleteSheet;
		}


		public JsonObject getClearSheetObject(long sheetID) {
			JsonObject clearSheet = Json.createObjectBuilder()
					.add("requests", Json.createArrayBuilder()
							.add(Json.createObjectBuilder()
									.add("updateCells", Json.createObjectBuilder()
											.add("range", Json.createObjectBuilder()
													.add("sheetId", sheetID)
											)
											.add("fields", "userEnteredValue")
									)
							)
					).build();

			return clearSheet;
		}

	}

}

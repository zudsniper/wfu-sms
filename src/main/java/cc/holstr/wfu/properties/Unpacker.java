package cc.holstr.wfu.properties;

import cc.holstr.wfu.model.StaticInfo;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by jason on 1/6/17.
 */
public class Unpacker {

	private static final Gson gson = new Gson();

	public static String STOCK_SPREADSHEET_ID;
	public static String STATS_SPREADSHEET_ID;
	public static StaticInfo STATIC_INFO;

	public static void unpack() {
		STATIC_INFO = gson.fromJson(new InputStreamReader(Unpacker.class.getClassLoader().getResourceAsStream("defaults/data.json")),StaticInfo.class);
		String stock_spreadsheet_id = System.getenv("STOCK_SPREADSHEET_ID");
		String stats_spreadsheet_id = System.getenv("STATS_SPREADSHEET_ID");
		if(stock_spreadsheet_id!=null) {
			STOCK_SPREADSHEET_ID = stock_spreadsheet_id;
		} else {
			STOCK_SPREADSHEET_ID = STATIC_INFO.getStockSpreadsheetId();
		}
		if(stats_spreadsheet_id!=null) {
			STATS_SPREADSHEET_ID = stats_spreadsheet_id;
		} else {
			STATS_SPREADSHEET_ID = STATIC_INFO.getStatsSpreadsheetId();
		}



	}
}

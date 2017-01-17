package cc.holstr.wfu.properties;

import cc.holstr.wfu.model.StaticInfo;
import com.google.gson.Gson;

import java.io.InputStreamReader;

/**
 * Created by jason on 1/6/17.
 */
public class Unpacker {

	private static final Gson gson = new Gson();

	public static String SPREADSHEET_ID;
	public static StaticInfo STATIC_INFO;

	public static void unpack() {
		STATIC_INFO = gson.fromJson(new InputStreamReader(Unpacker.class.getClassLoader().getResourceAsStream("defaults/data.json")),StaticInfo.class);
		String id = System.getenv("SPREADSHEET_ID");
		if(id!=null) {
			SPREADSHEET_ID = id;
		} else {
			SPREADSHEET_ID = STATIC_INFO.getSpreadsheetId();
		}



	}
}

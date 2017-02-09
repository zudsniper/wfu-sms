package cc.holstr.wfu.google;

import cc.holstr.wfu.properties.Unpacker;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by jason on 2/2/17.
 */
public abstract class GoogleSheetsManager {


	protected Drive drive;
	public SheetsIO sheetsIO;

	protected String spreadsheetID;
	protected Credential credential;

	public String appName;
	public String credentialPath;

	public GoogleSheetsManager() {
	}

	public GoogleSheetsManager(String appName, String credentialPath, String spreadsheetID) {
		this.build(appName, credentialPath, spreadsheetID);
	}

	public void build(String appName, String credentialPath, String spreadsheetID) {
		this.appName = appName;
		this.credentialPath = credentialPath;
		this.spreadsheetID = spreadsheetID;
		credential = authorise();
		drive = initialiseDrive();
		sheetsIO = new SheetsIO(credential,spreadsheetID);
	}

	public Credential authorise() {
		Credential credential = null;

		Set<String> scopes = new HashSet<String>();
		scopes.add("https://www.googleapis.com/auth/drive");
		scopes.add("https://www.googleapis.com/auth/spreadsheets");

		try {
			credential = GoogleCredential.fromStream(this.getClass().getClassLoader().getResourceAsStream(credentialPath))
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
					appName).build();
			return drive;
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SheetsIO getSheetsIO() {
		return sheetsIO;
	}
}

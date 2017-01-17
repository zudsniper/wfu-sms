package cc.holstr.wfu.google.http;

import com.google.api.client.auth.oauth2.Credential;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GoogleHttpHelper {
	
	private Credential credential; 
	
	public GoogleHttpHelper(Credential credential) {
		this.credential = credential;
	}
	
	public InputStream post(String url, String param) throws Exception{
			return post(url,param,credential);
	}
	
	public InputStream get(String url) throws Exception{
		return get(url,credential);
	}
	
	public InputStream put(String url, String jsonString) throws Exception{
		return put(url, jsonString,credential);
	}
	
	public static InputStream get(String url, Credential cred) throws Exception{
		//http GET
		boolean failed = false;
		  String charset = "UTF-8"; 
		//if access token is expired, refresh and try again.
		  if (cred.getAccessToken() == null || cred.getExpiresInSeconds() != null && cred.getExpiresInSeconds() <= 60) {
		      //cred.refreshToken();
			  cred.refreshToken();
		      get(url, cred);
		  }
		  URLConnection connection = new URL(url).openConnection();
		  connection.setRequestProperty("accept-charset", charset);
		  connection.setRequestProperty("content-length", "0");
		  connection.setRequestProperty("authorization","Bearer " + cred.getAccessToken());

		  InputStream response = connection.getInputStream();
		  return response;
	}
	
	public static InputStream post(String url, String jsonString, Credential cred) throws Exception{
		//http POST
		  boolean failed = false;
		  String charset = "UTF-8"; 
		//if access token is expired, refresh and try again.
		  if (cred.getAccessToken() == null || cred.getExpiresInSeconds() != null && cred.getExpiresInSeconds() <= 60) {
		      cred.refreshToken();
		      post(url, jsonString, cred);
		  }
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(true); // Triggers POST.
		  connection.setRequestProperty("accept-charset", charset);
		  connection.setRequestProperty("content-type", "application/json;charset=" + charset);
		  connection.setRequestProperty("content-length", ""+jsonString.length());
		  connection.setRequestProperty("authorization","Bearer " + cred.getAccessToken());

		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(jsonString.getBytes(charset));
		  } catch(Exception e) {
			  failed = true;
			  e.printStackTrace();
		  }
		  
		  InputStream response = connection.getInputStream();
		  return response;
		}
	
	public static InputStream put(String url, String jsonString, Credential cred) throws Exception{
		//http PUT
		  boolean failed = false;
		  String charset = "UTF-8"; 
		  //if access token is expired, refresh and try again.
		  if (cred.getAccessToken() == null || cred.getExpiresInSeconds() != null && cred.getExpiresInSeconds() <= 60) {
		      cred.refreshToken();
		      put(url, jsonString, cred);
		  }
		  HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
		  connection.setDoOutput(true);
		  connection.setRequestMethod("PUT");
		  connection.setRequestProperty("accept-charset", charset);
		  connection.setRequestProperty("content-type", "application/json;charset=" + charset);
		  connection.setRequestProperty("content-length", ""+jsonString.length());
		  connection.setRequestProperty("authorization","Bearer " + cred.getAccessToken());

		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(jsonString.getBytes(charset));
		  } catch(Exception e) {
			  failed = true;
			  e.printStackTrace();
		  }

		  InputStream response = connection.getInputStream();
		  return response;
		}
	
	public Credential getCredential() {
		return credential;
	}
}

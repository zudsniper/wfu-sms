package cc.holstr.wfu.google.http;

import com.google.api.client.auth.oauth2.Credential;

import java.io.InputStream;

public class RetryingGoogleHttpHelper extends GoogleHttpHelper {

	private String retryOn;
	private long retryMS;
	
	
	public RetryingGoogleHttpHelper(Credential credential, long retryMS, String retryOn) {
		super(credential);
		setWait(retryMS);
		setRetryOn(retryOn);
	}
	
	public InputStream post(String url, String jsonString) {
		InputStream response = null;
				try {
					response = super.post(url, jsonString);
				} catch (Exception e) {
					if(retryOn!=null) {
						if(e.getMessage().contains(retryOn)) {
							retryWait();
							return post(url, jsonString);
						}
					} else {
					retryWait();
					return post(url, jsonString);
					}
				}
		return response;
	}
	
	public InputStream get(String url) {
		InputStream response = null; 
				try {
					response = super.get(url);
				} catch (Exception e) {
					if(retryOn!=null) {
						if(e.getMessage().contains(retryOn)) {
							retryWait();
							return get(url);
						}
					} else {
					retryWait();
					return get(url);
					}
				}
		return response;
	}
	
	public InputStream put(String url, String jsonString) {
		InputStream response = null;
			try {
				response = super.put(url, jsonString);
			} catch(Exception e) {
				if(retryOn!=null) {
					if(e.getMessage().contains(retryOn)) {
						retryWait();
						return put(url, jsonString);
					}
				} else {
				retryWait();
				return put(url, jsonString);
				}
			}
			return response;
	}
	
	private void retryWait() {
		if(getWait()>=0) {
			try {
				Thread.sleep(getWait());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getRetryOn() {
		return retryOn;
	}

	public void setRetryOn(String retryOn) {
		this.retryOn = retryOn;
	}

	public long getWait() {
		return retryMS;
	}

	public void setWait(long retryMS) {
		this.retryMS = retryMS;
	}	
	
}

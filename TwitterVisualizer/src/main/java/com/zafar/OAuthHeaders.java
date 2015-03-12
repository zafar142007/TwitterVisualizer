package com.zafar;

public class OAuthHeaders {

	private String consumerKey="XXXXXXX", consumerSecret="XXXXXXX", accessTokenString="XXXXXXX", accessTokenSecret="XXXXX";
	
	public OAuthHeaders() {
		// TODO Auto-generated constructor stub
	}

	protected String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	protected String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	protected String getAccessTokenString() {
		return accessTokenString;
	}

	public void setAccessTokenString(String accessTokenString) {
		this.accessTokenString = accessTokenString;
	}

	protected String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

}

package com.zafar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class TwitterThreadProvider extends Thread {
	private Logger log=Logger.getLogger(TwitterThreadProvider.class);
	private String consumerKey="", consumerSecret="", accessTokenString="", accessTokenSecret="";
	public String getConsumerKey() {
		return consumerKey;
	}
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	public String getConsumerSecret() {
		return consumerSecret;
	}
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	public String getAccessTokenString() {
		return accessTokenString;
	}
	public void setAccessTokenString(String accessTokenString) {
		this.accessTokenString = accessTokenString;
	}
	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}
	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}
	private String longitude, latitude;
	private final double RADIUS=1.0;//search within 1 mile
	private final double EARTH_RADIUS=3959.0;//earth radius in miles
    private static final String STREAM_URI = "https://stream.twitter.com/1.1/statuses/filter.json";
    public ArrayList<String> tweets;
    
    public TwitterThreadProvider() {
	}
    public TwitterThreadProvider(String lat, String longi, ArrayList<String> tweets){
    	latitude=lat;
    	longitude=longi;
    	this.tweets=tweets;
    }
	public void setLatitude(String lat) {
		latitude=lat;
	}

	public void setLongitude(String longi){
		longitude=longi;
	}
	public static double convertToRadians(double degree){
		return degree*Math.PI/180.0;
	}
	public static double convertToDegrees(double radians){
		return radians*180.0/Math.PI;
	}
	public String calculateBoundingBox(){
		StringBuilder result=new StringBuilder();
		try{
			double lat=Double.parseDouble(latitude);
			double longi=Double.parseDouble(longitude);
	
			result.append(convertToDegrees(convertToRadians(longi)-(RADIUS/EARTH_RADIUS)));
			result.append(",");
			result.append(convertToDegrees(convertToRadians(lat)-(RADIUS/EARTH_RADIUS)));
			
			result.append(",");
			
			result.append(convertToDegrees(convertToRadians(longi)+(RADIUS/EARTH_RADIUS)));
			result.append(",");
			result.append(convertToDegrees(convertToRadians(lat)+(RADIUS/EARTH_RADIUS)));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result.toString();
	}
	public void run(){
		try{
			OAuthService service = new ServiceBuilder()
	        .provider(TwitterApi.class)
	        .apiKey(consumerKey)
	        .apiSecret(consumerSecret)
	        .build();
			String searchBoundingBox=calculateBoundingBox();
			Token accessToken = new Token(accessTokenString, accessTokenSecret);

            // Let's generate the request
            log.debug("Connecting");
            OAuthRequest request = new OAuthRequest(Verb.POST, STREAM_URI);
            request.addHeader("version", "HTTP/1.1");
            request.addHeader("host", "stream.twitter.com");
            request.setConnectionKeepAlive(true);
            request.addHeader("user-agent", "Twitter Stream Reader");
            request.addBodyParameter("locations",searchBoundingBox); // Set keywords you'd like to track here
            service.signRequest(accessToken, request);
            log.debug(request);
            Response response = request.send();

            // Create a reader to read Twitter's stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getStream()));

            String line;
            while ((line = reader.readLine()) != null) {
            	synchronized(tweets){
            		if(line!="")
            			tweets.add(line);
            	}
            	log.info(line);
            }
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}

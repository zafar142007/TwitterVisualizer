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
	volatile boolean finished = false;
	private String consumerKey="", consumerSecret="", accessTokenString="", accessTokenSecret="";
	private String longitude, latitude;
	private double RADIUS=1.0;//search within 1 mile
	private final double EARTH_RADIUS=3959.0;//earth radius in miles
	private int MAX_NUMBER_OF_TWEETS=100;
    private static final String STREAM_URI = "https://stream.twitter.com/1.1/statuses/filter.json";
    public ArrayList<String> tweets;
    public void setHeaders(OAuthHeaders h)
    {
    	setConsumerKey(h.getConsumerKey());
    	setConsumerSecret(h.getConsumerSecret());
    	setAccessTokenSecret(h.getAccessTokenSecret());
    	setAccessTokenString(h.getAccessTokenString());
    }
	private void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	private void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	private void setAccessTokenString(String accessTokenString) {
		this.accessTokenString = accessTokenString;
	}
	private void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}
    public void setTweets(ArrayList<String> t){
    	 tweets=t;
    }
    public void setRadius(double d){
    	RADIUS=d;
    }
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
            while ((line = reader.readLine()) != null && finished==false) {
            	synchronized(tweets){
            		if(line!="")
            		{	
            			tweets.add(line);
            			if(tweets.size()==MAX_NUMBER_OF_TWEETS)
            			{
            				tweets.remove(0);//flush out from the front of the queue
            			}
            		}
            	}
            	log.info(line);
            }
            reader.close();
            log.debug("Closing this thread: latitude "+latitude+"  longitude "+longitude);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public void stopMe()
	{
	    finished = true;
	}
	public boolean isFinished(){
		return isAlive();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TwitterThreadProvider other = (TwitterThreadProvider) obj;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}
}

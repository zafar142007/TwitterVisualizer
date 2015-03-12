package com.zafar;

import java.util.ArrayList;

import org.apache.log4j.Logger;


public class Client {

	private static Logger log=Logger.getLogger(Client.class);
	private TwitterThreadProvider provider=new TwitterThreadProvider();
	private ArrayList<String> tweets=new ArrayList<String>();
	
	public Client() {
		if(provider!=null)
			provider.setTweets(tweets);
		else
			log.error("Provider is null");
	}
	public Client(String lat, String longi){
		if(provider!=null){
			provider.setLatitude(lat);
			provider.setLongitude(longi);
			provider.setTweets(tweets);
		}else
			log.error("Provider is null");
	}
	public Client(ArrayList<String> str, TwitterThreadProvider pr){
		tweets=str;
		provider=pr;
		if(provider!=null)
		{
			
			provider.setTweets(tweets);
		}
		else
			log.error("Provider is null");
	}
	
	
	public TwitterThreadProvider getProvider() {
		return provider;
	}

	public void setProvider(TwitterThreadProvider provider) {
		this.provider = provider;
	}

	public ArrayList<String> getTweets() {
		return tweets;
	}

	public void setTweets(ArrayList<String> tweets) {
		this.tweets = tweets;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((provider == null) ? 0 : provider.hashCode());
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
		Client other = (Client) obj;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		return true;
	}

}

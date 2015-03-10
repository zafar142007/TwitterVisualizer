package com.zafar;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class TwitterReader {

	private TwitterThreadProvider provider;
	public ArrayList<String> tweets;
	
	@RequestMapping(value="/start",method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void startStreamingFromTwitter(@RequestParam(value = "lat", required = true) String lat, @RequestParam(value = "longi", required = true) String longi){
		tweets=new ArrayList<String>();
		provider=new TwitterThreadProvider(lat, longi, tweets);	
		provider.setConsumerKey("XXXXXX");//Enter your keys in the following four fields
		provider.setConsumerSecret("XXXXXX");
		provider.setAccessTokenString("XXXXXXXXX");
		provider.setAccessTokenSecret("XXXXXXXXXX");
		provider.start();
	}
/*	@RequestMapping(value="/start",method = RequestMethod.GET)
	public void startStreamingFromTwitter(@RequestBody UserLocation latLong){
		tweets=new ArrayList<String>();
		provider=new TwitterThreadProvider(latLong.getLat(), latLong.getLongi(), tweets);	
		provider.start();
	}
*/	
	@RequestMapping(value="/tweet",method=RequestMethod.GET)
	public @ResponseBody Tweet[] getTweet(@RequestParam(value="index", required=true) String indexx){
		Tweet newTweets[]=null;
		int index=Integer.parseInt(indexx);
		if(tweets!=null)
		{	synchronized(tweets){
				if(tweets.size()-index >0)
				{	newTweets=new Tweet[tweets.size()-index];
				
					for(int i=index, j=0; i<tweets.size(); i++, j++)
						newTweets[j]=new Tweet(tweets.get(i));
				}
			}
		}
		
		return newTweets;
		
	}
}

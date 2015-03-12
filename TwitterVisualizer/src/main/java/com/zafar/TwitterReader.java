package com.zafar;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class TwitterReader {

	private static Logger log=Logger.getLogger(TwitterReader.class);
	private HashMap<Position, Client> clients;
	private OAuthHeaders headers=new OAuthHeaders();
	public TwitterReader(){
		clients=new HashMap<Position, Client>();
	}
	@RequestMapping(value="/start",method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void startStreamingFromTwitter(@RequestParam(value = "lat", required = true) String lat, @RequestParam(value = "longi", required = true) String longi){
		Client client=new Client(lat, longi);
		Position pos=new Position(lat, longi);
		if(clients.get(pos)==null)
		{	
			clients.put(pos, client);
			TwitterThreadProvider provider=client.getProvider();
			provider.setHeaders(headers);
			provider.start();
		}
	}
	@RequestMapping(value="/end",method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void stopStreamingFromTwitter(@RequestParam(value = "lat", required = true) String lat, @RequestParam(value = "longi", required = true) String longi){
		Position pos=new Position(lat, longi);
		Client c=clients.remove(pos);
		c.getProvider().stopMe();
		log.info("Ended feed filtering for "+lat+" and "+longi);
	}
	@RequestMapping(value="/tweet",method=RequestMethod.GET)
	public @ResponseBody Tweet[] getTweet(@RequestParam(value="index", required=true) String indexx, @RequestParam(value = "lat", required = true) String lat, @RequestParam(value = "longi", required = true) String longi){
		Client c=clients.get(new Position(lat, longi));
		if(c!=null)
		{
			ArrayList<String> tweets=c.getTweets();
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
		else return null;
	}
}

package com.zafar;

public class Tweet {
	public Tweet(String string) {
		tweet=string;
	}

	private String tweet;

	public String getTweet() {
		return tweet;
	}

	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
}

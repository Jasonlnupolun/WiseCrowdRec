/**
 * @author feiyu
 */
package com.feiyu.springmvc.service;

import java.util.List;

import com.feiyu.util.GlobalVariables;

import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class SearchTweetsImpl implements SearchTweets {
	private static StatusListener _listener;
	private static TwitterStream _twitterStream;
	private static String _keywordPhrases;
	public ConfigurationBuilder _twitterConfBuilder_back;
	public ConfigurationBuilder _twitterConfBuilder_dyna;
	
	public SearchTweetsImpl(StatusListener listener, TwitterStream twitterStream, String keywordPhrases) {
		_listener = listener;
		_twitterStream = twitterStream;
		_keywordPhrases = keywordPhrases;
	}
	
	public void twitterInitBack() {
        // Set Twitter app oauth infor
		_twitterConfBuilder_back = new ConfigurationBuilder();
		//twitterConf.setIncludeEntitiesEnabled(true);
		_twitterConfBuilder_back.setDebugEnabled(Boolean.valueOf(GlobalVariables.WCR_PROPS.getProperty("debug")))
			.setOAuthConsumerKey(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerKey1"))
			.setOAuthConsumerSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerSecret1"))
			.setOAuthAccessToken(GlobalVariables.WCR_PROPS.getProperty("oauth.accessToken1"))
			.setOAuthAccessTokenSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.accessTokenSecret1"));
	}
	
	public void twitterInitDyna() {
        // Set Twitter app oauth infor
		_twitterConfBuilder_dyna= new ConfigurationBuilder();
		//twitterConf.setIncludeEntitiesEnabled(true);
		_twitterConfBuilder_dyna.setDebugEnabled(Boolean.valueOf(GlobalVariables.WCR_PROPS.getProperty("debug")))
			.setOAuthConsumerKey(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerKey2"))
			.setOAuthConsumerSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerSecret2"))
			.setOAuthAccessToken(GlobalVariables.WCR_PROPS.getProperty("oauth.accessToken2"))
			.setOAuthAccessTokenSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.accessTokenSecret2"));
	}
	
	private void openListener(boolean isDynamicSearch) {
		if (!isDynamicSearch) {
			twitterInitBack();
			_twitterStream = new TwitterStreamFactory(_twitterConfBuilder_back.build()).getInstance();
		} else {
			twitterInitDyna();
			_twitterStream = new TwitterStreamFactory(_twitterConfBuilder_dyna.build()).getInstance();
		}
		_twitterStream.addListener(_listener);
	}
	/**
	 * Starts listening and getting tweets, which contain the keyword phrase, from current time
	 * @param phrases: keyword phrases which searched by the client, for example: I rated #IMDb, movie, this is great
	 */
	@Override
	public void searchTweetsFromNowOn(boolean isDynamicSearch) {
		this.openListener(isDynamicSearch);
		FilterQuery fq = new FilterQuery();
		String keywords[] = {_keywordPhrases};// movie
		fq.track(keywords);
		_twitterStream.filter(fq);
	}
	
	/**
	 * Starts listening on random sample, about 1% of firehouse tweets, of all public statuses.
	 * Not "Gardenhose" access level -> about 10% of firehouse tweets, need to email api-research@twitter.com later
	 * @param phrases
	 */
	@Override
	public void searchTweetsRandomSample(boolean isDynamicSearch) {
		this.openListener(isDynamicSearch);
		_twitterStream.sample();
	}
	
	public void searchDailyTweets() {
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			Query query = new Query(_keywordPhrases);
			QueryResult result;
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					System.out.println("=>"+ tweet.getCreatedAt()+"@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
				}
			} while ((query = result.nextQuery()) != null);
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			System.exit(-1);
		}
	}
}

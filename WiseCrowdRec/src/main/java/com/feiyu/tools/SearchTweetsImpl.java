/**
 * @author feiyu
 */
package com.feiyu.tools;

import java.util.List;
import java.util.Properties;

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
	private static Properties _wcrProps;
	private static ConfigurationBuilder _twitterConf;
	private static StatusListener _listener;
	private static TwitterStream _twitterStream;
	
	public SearchTweetsImpl(ConfigurationBuilder twitterConf, Properties wcrProps, 
			StatusListener listener, TwitterStream twitterStream) {
		_wcrProps = wcrProps;
		_twitterConf = twitterConf;
		_listener = listener;
		_twitterStream = twitterStream;
	}
	
	private void openListener() {
		_twitterStream = new TwitterStreamFactory(_twitterConf.build()).getInstance();
		_twitterStream.addListener(_listener);
	}
	/**
	 * Starts listening and getting tweets, which contain the keyword phrase, from current time
	 * @param phrases: keyword phrases which searched by the client, for example: I rated #IMDb, movie, this is great
	 */
	@Override
	public void searchTweetsFromNowOn() {
		this.openListener();
		FilterQuery fq = new FilterQuery();
		String keywords[] = {_wcrProps.getProperty("SEARCH_PHRASES")};// movie
		fq.track(keywords);
		_twitterStream.filter(fq);
	}
	
	/**
	 * Starts listening on random sample, about 1% of firehouse tweets, of all public statuses.
	 * Not "Gardenhose" access level -> about 10% of firehouse tweets, need to email api-research@twitter.com later
	 * @param phrases
	 */
	@Override
	public void searchTweetsRandomSample() {
		this.openListener();
		_twitterStream.sample();
	}
	
	public void searchDailyTweets() {
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			Query query = new Query(_wcrProps.getProperty("SEARCH_PHRASES"));
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

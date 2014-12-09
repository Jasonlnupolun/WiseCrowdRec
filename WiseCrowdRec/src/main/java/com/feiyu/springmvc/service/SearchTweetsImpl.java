/**
 * @author feiyu
 */
package com.feiyu.springmvc.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.feiyu.utils.GlobalVariables;

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

public class SearchTweetsImpl implements SearchTweets {
  private static Logger LOG = Logger.getLogger(SearchTweetsImpl.class.getName());
  private static StatusListener _listener;
  private static TwitterStream _twitterStream;
  private static String _keywordPhrases;

  public SearchTweetsImpl() {
  }

  public SearchTweetsImpl(StatusListener listener, TwitterStream twitterStream, String keywordPhrases) {
    _listener = listener;
    _twitterStream = twitterStream;
    _keywordPhrases = keywordPhrases;
  }

  private void openListener(boolean isDynamicSearch) {
    if (!isDynamicSearch) {
      _twitterStream = new TwitterStreamFactory(GlobalVariables.TWT_CONF_BUILDER_BACK.build()).getInstance();
    } else {
      _twitterStream = new TwitterStreamFactory(GlobalVariables.TWT_CONF_BUILDER_DYNA.build()).getInstance();
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
          LOG.info("=>"+ tweet.getCreatedAt()+"@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
        }
      } while ((query = result.nextQuery()) != null);
      System.exit(0);
    } catch (TwitterException te) {
      te.printStackTrace();
      LOG.info("Failed to search tweets: " + te.getMessage());
      System.exit(-1);
    }
  }
}

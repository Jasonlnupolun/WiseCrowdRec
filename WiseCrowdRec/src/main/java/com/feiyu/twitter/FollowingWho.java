package com.feiyu.twitter;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import com.feiyu.utils.GlobalVariables;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class FollowingWho {
	ConfigurationBuilder confBuilder = new ConfigurationBuilder();

	public void getOauth(String userID) throws ConnectionException, NumberFormatException, TwitterException {
		String[] oauthAry =  GlobalVariables.AST_CASSANDRA_UL.queryWithUserID(userID);
		
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(
				GlobalVariables.WCR_PROPS.getProperty("oauth.consumerKey3"), 
				GlobalVariables.WCR_PROPS.getProperty("oauth.consumerSecret3"));
		AccessToken oathAccessToken = new AccessToken(
				oauthAry[1], oauthAry[2]);
		twitter.setOAuthAccessToken(oathAccessToken);

		System.out.println("Listing friends's ids of"+twitter.getId()+":");
		System.out.println(twitter.getId()+" is following:");
		IDs ids = twitter.getFriendsIDs(twitter.getId(), -1);
		for (long id : ids.getIDs()) {
			System.out.println("id->"+id);
		}
	}
}

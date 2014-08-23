package com.feiyu.twitter;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.jackson.map.ObjectMapper;

import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import com.feiyu.utils.GlobalVariables;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class FollowingWhom {
	public String getFollowingWhomList(String userID) throws ConnectionException, NumberFormatException, TwitterException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String,String> jsonMap = new TreeMap<String,String>();
		
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

		System.out.println(twitter.getId()+" is following:");
		PagableResponseList<User> friendList = twitter.getFriendsList(twitter.getId(), -1);
		for (User user : friendList) {
			System.out.println(
					"user->"+user.getName()
					+" "+user.getScreenName()
					+" "+user.getId()
					);
			jsonMap.put(Long.toString(user.getId()),user.getName());
		}
		
		String json = mapper.writeValueAsString(jsonMap);
	    System.out.println("JSON: " + json);
	    return json;
	}
}

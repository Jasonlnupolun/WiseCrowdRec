/**
 * @author feiyu
 */
package com.feiyu.storm.streamingdatacollection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.feiyu.storm.streamingdatacollection.bolt.GetMetadataBolt;
import com.feiyu.storm.streamingdatacollection.spout.TwitterQuaryStreamSpout;

import twitter4j.conf.ConfigurationBuilder;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

public class Topology {

    private static final String TOPOLOGY_NAME = "Streaming-Data-Collection";
    private static final int MESSAGE_TIMEOUT_SECS = 120;
	private static final int TWITTER_SPOUT_PARALLELISM_HINT = 2;
	private static final int GMD_BOLT_PARALLELISM_HINT = 5;
	private static Properties _wcrProps;
	private static ConfigurationBuilder _twitterConf;
	
	public static void getWiseCrowdRecConfigInfo () throws IOException {
		_wcrProps = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        _wcrProps.load(in);
        
        // Set Twitter app oauth infor
		_twitterConf = new ConfigurationBuilder();
		//twitterConf.setIncludeEntitiesEnabled(true);
		_twitterConf.setDebugEnabled(Boolean.valueOf(_wcrProps.getProperty("debug")))
			.setOAuthConsumerKey(_wcrProps.getProperty("oauth.consumerKey"))
			.setOAuthConsumerSecret(_wcrProps.getProperty("oauth.consumerSecret"))
			.setOAuthAccessToken(_wcrProps.getProperty("oauth.accessToken"))
			.setOAuthAccessTokenSecret(_wcrProps.getProperty("oauth.accessTokenSecret"));
	}

	public static void main(String[] args) throws IOException {
		// Get WiseCrowdRec configuration information
		getWiseCrowdRecConfigInfo ();
		
		// Get storm topology configuration information
		Config config = new Config();
//		config.setDebug(true);
		config.setMessageTimeoutSecs(MESSAGE_TIMEOUT_SECS);
		/*
		 * http://nathanmarz.github.io/storm/doc/backtype/storm/Config.html#TOPOLOGY_MESSAGE_TIMEOUT_SECS
		 * "The maximum amount of time given to the topology to fully process a message emitted by a spout. 
		 * If the message is not acked within this time frame, Storm will fail the message on the spout. 
		 * Some spouts implementations will then replay the message at a later time."
		 * Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS default is 30 seconds
		 */

		TopologyBuilder b = new TopologyBuilder();
		b.setSpout("TwitterQuaryStreamSpout", new TwitterQuaryStreamSpout(_twitterConf, _wcrProps), TWITTER_SPOUT_PARALLELISM_HINT);
        b.setBolt("GetMetadataBolt", new GetMetadataBolt() , GMD_BOLT_PARALLELISM_HINT).shuffleGrouping("TwitterQuaryStreamSpout");

		final LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY_NAME, config, b.createTopology());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				cluster.killTopology(TOPOLOGY_NAME);
				cluster.shutdown();
			}
		});

	}

}

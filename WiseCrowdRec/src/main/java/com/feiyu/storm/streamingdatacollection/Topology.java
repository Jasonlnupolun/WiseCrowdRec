/**
 * @author feiyu
 */
package com.feiyu.storm.streamingdatacollection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import com.feiyu.database.AstyanaxCassandraManipulator;
import com.feiyu.database.PelopsCassandraManipulator;
import com.feiyu.storm.streamingdatacollection.bolt.EntityCountBolt;
import com.feiyu.storm.streamingdatacollection.bolt.GetMetadataBolt;
import com.feiyu.storm.streamingdatacollection.bolt.InfoFilterBolt;
import com.feiyu.storm.streamingdatacollection.spout.TwitterQuaryStreamSpout;

import twitter4j.conf.ConfigurationBuilder;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class Topology {

    private final String TOPOLOGY_NAME = "Streaming-Data-Collection";
    private final int MESSAGE_TIMEOUT_SECS = 120;
	private final int TWITTER_SPOUT_PARALLELISM_HINT = 2;
	private final int GMD_BOLT_PARALLELISM_HINT = 5;
	private final int IF_BOLT_PARALLELISM_HINT = 5;
	private final int EC_BOLT_PARALLELISM_HINT = 5;
	private Properties _wcrProps;
	private ConfigurationBuilder _twitterConf;
	private PelopsCassandraManipulator _cm;
	
	public void getWiseCrowdRecConfigInfo () throws IOException {
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
	
	public void cassandraInitial() {
		_cm = new PelopsCassandraManipulator("pool","wcrkeyspace","tweets","localhost",9160);
		_cm.addToPool();
	}

	public void startTopology(String searchPhrases) throws IOException {
		// Get WiseCrowdRec configuration information
		getWiseCrowdRecConfigInfo();
		
		// Initialize Cassandra database
//		cassandraInitial();
		
		_wcrProps.setProperty("SEARCH_PHRASES", searchPhrases);
		
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
        b.setBolt("InfoFilterBolt", new InfoFilterBolt() , IF_BOLT_PARALLELISM_HINT).fieldsGrouping("GetMetadataBolt", new Fields("tweetMetadata"));
        b.setBolt("EntityCountBolt", new EntityCountBolt() , EC_BOLT_PARALLELISM_HINT).fieldsGrouping("InfoFilterBolt", new Fields("entity", "category"));

		final LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY_NAME, config, b.createTopology());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				cluster.killTopology(TOPOLOGY_NAME);
				cluster.shutdown();
//				_cm.shutdownPool(); // shutdown cassandra pool
				System.exit(0);
			}
		});
	}
	
	public static void main(String[] argv) throws IOException {
//		PropertyConfigurator.configure(AstyanaxCassandraManipulator.class.getClassLoader().getResource("log4j.properties"));
		Topology t = new Topology();
		t.startTopology("movie");
	}
}

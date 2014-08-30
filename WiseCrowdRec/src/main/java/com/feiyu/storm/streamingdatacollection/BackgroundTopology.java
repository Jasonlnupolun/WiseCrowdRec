/**
 * @author feiyu
 */
package com.feiyu.storm.streamingdatacollection;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.TException;

import com.feiyu.storm.streamingdatacollection.bolt.ForTestGetMovieDataBolt;
import com.feiyu.storm.streamingdatacollection.bolt.ForTestMovieCountBolt;
import com.feiyu.storm.streamingdatacollection.bolt.GetMoviedataBolt;
import com.feiyu.storm.streamingdatacollection.bolt.MovieCount2CassandraBackBolt;
import com.feiyu.storm.streamingdatacollection.bolt.StormJmsBolt;
import com.feiyu.storm.streamingdatacollection.spout.ForTestFakeSpout;
import com.feiyu.storm.streamingdatacollection.spout.TwitterQueryStreamBackSpout;
import com.feiyu.utils.InitializeWCR;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class BackgroundTopology {
	private String TOPOLOGY_NAME;
	private final int MESSAGE_TIMEOUT_SECS = 120;
	private final int TWITTER_SPOUT_PARALLELISM_HINT = 1;
	private final int GMD_BOLT_PARALLELISM_HINT = 1;
	private final int EC_BOLT_PARALLELISM_HINT = 1;

	public void startTopology(boolean isFakeTopologyForTest, String tpName, String keywordPhrases) throws IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, URISyntaxException, TException {
		PropertyConfigurator.configure(BackgroundTopology.class.getClassLoader().getResource("log4j.properties"));
		TOPOLOGY_NAME = tpName;

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

		StormJmsBolt stormJmsBolt = new StormJmsBolt(); // Storm java message services

		TopologyBuilder b = new TopologyBuilder();

		if (isFakeTopologyForTest) {
			b.setSpout("ForTestFakeSpout", new ForTestFakeSpout(), TWITTER_SPOUT_PARALLELISM_HINT);
			b.setBolt("ForTestGetMovieDataBolt", new ForTestGetMovieDataBolt() , GMD_BOLT_PARALLELISM_HINT).shuffleGrouping("ForTestFakeSpout");
			b.setBolt("ForTestMovieCountBolt", new ForTestMovieCountBolt() , EC_BOLT_PARALLELISM_HINT).fieldsGrouping("ForTestGetMovieDataBolt", new Fields("movie"));
			b.setBolt("StormJmsBolt", stormJmsBolt.jmsBolt()).fieldsGrouping("ForTestMovieCountBolt",new Fields("movieWithCount"));
		} else {
			b.setSpout("TwitterQueryStreamBackSpout", new TwitterQueryStreamBackSpout(keywordPhrases), TWITTER_SPOUT_PARALLELISM_HINT);
			b.setBolt("GetMoviedataBolt", new GetMoviedataBolt() , GMD_BOLT_PARALLELISM_HINT).shuffleGrouping("TwitterQueryStreamBackSpout");
			b.setBolt("MovieCount2CassandraBackBolt", new MovieCount2CassandraBackBolt() , EC_BOLT_PARALLELISM_HINT).fieldsGrouping("GetMoviedataBolt", new Fields("movie"));
			b.setBolt("StormJmsBolt", stormJmsBolt.jmsBolt()).fieldsGrouping("MovieCount2CassandraBackBolt",new Fields("movieWithCount"));
		}

		// @@@ modify this later, Submit topology locally or to the cluster

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

	public static void main(String[] argv) throws Exception {
		InitializeWCR initWcr = new InitializeWCR();
		initWcr.getWiseCrowdRecConfigInfo();
		initWcr.twitterInitBack();
		initWcr.cassandraInitial();
		initWcr.coreNLPInitial();
		initWcr.themoviedbOrgInitial();
		initWcr.rabbitmqInit();

		BackgroundTopology t = new BackgroundTopology();

//		boolean isFakeTopologyForTest = true;
		boolean isFakeTopologyForTest = false;
		t.startTopology(isFakeTopologyForTest, "wcr_topology_back", "I rated #IMDb");
	}
}

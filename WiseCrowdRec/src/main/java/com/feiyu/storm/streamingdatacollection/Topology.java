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

import com.feiyu.storm.streamingdatacollection.bolt.EntityCount2CassandraBackBolt;
import com.feiyu.storm.streamingdatacollection.bolt.EntityCount2ElasticsearchBolt;
import com.feiyu.storm.streamingdatacollection.bolt.GetMetadataBolt;
import com.feiyu.storm.streamingdatacollection.bolt.InfoFilterBolt;
import com.feiyu.storm.streamingdatacollection.spout.TwitterQuaryStreamBackSpout;
import com.feiyu.storm.streamingdatacollection.spout.TwitterQuaryStreamDynaSpout;
import com.feiyu.util.InitializeWCR;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class Topology {

    private String TOPOLOGY_NAME;
    private final int MESSAGE_TIMEOUT_SECS = 120;
	private final int TWITTER_SPOUT_PARALLELISM_HINT = 1;
	private final int GMD_BOLT_PARALLELISM_HINT = 1;
	private final int IF_BOLT_PARALLELISM_HINT = 1;
	private final int EC_BOLT_PARALLELISM_HINT = 1;

	public void startTopology(boolean isDynamicSearch, String tpName, String keywordPhrases) throws IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, URISyntaxException, TException {
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

		TopologyBuilder b = new TopologyBuilder();
        
		if (!isDynamicSearch) {
			b.setSpout("TwitterQuaryStreamBackSpout", new TwitterQuaryStreamBackSpout(keywordPhrases), TWITTER_SPOUT_PARALLELISM_HINT);
			b.setBolt("GetMetadataBolt", new GetMetadataBolt() , GMD_BOLT_PARALLELISM_HINT).shuffleGrouping("TwitterQuaryStreamBackSpout");
			b.setBolt("InfoFilterBolt", new InfoFilterBolt() , IF_BOLT_PARALLELISM_HINT).fieldsGrouping("GetMetadataBolt", new Fields("tweetMetadata"));
			b.setBolt("EntityCount2CassandraBackBolt", new EntityCount2CassandraBackBolt() , EC_BOLT_PARALLELISM_HINT).fieldsGrouping("InfoFilterBolt", new Fields("entityInfo"));
		} else {
			b.setSpout("TwitterQuaryStreamDynaSpout", new TwitterQuaryStreamDynaSpout(keywordPhrases), TWITTER_SPOUT_PARALLELISM_HINT);
			b.setBolt("GetMetadataBolt", new GetMetadataBolt() , GMD_BOLT_PARALLELISM_HINT).shuffleGrouping("TwitterQuaryStreamDynaSpout");
			b.setBolt("InfoFilterBolt", new InfoFilterBolt() , IF_BOLT_PARALLELISM_HINT).fieldsGrouping("GetMetadataBolt", new Fields("tweetMetadata"));
			b.setBolt("EntityCount2ElasticsearchBolt", new EntityCount2ElasticsearchBolt() , EC_BOLT_PARALLELISM_HINT).fieldsGrouping("InfoFilterBolt", new Fields("entityInfo"));
		}

		final LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY_NAME, config, b.createTopology());

//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			@Override
//			public void run() {
//				cluster.killTopology(TOPOLOGY_NAME);
//				cluster.shutdown();
////				_cm.shutdownPool(); // shutdown cassandra pool
//				System.exit(0);
//			}
//		});
	}
	
	public static void main(String[] argv) throws IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, URISyntaxException, TException {
		PropertyConfigurator.configure(Topology.class.getClassLoader().getResource("log4j.properties"));
		InitializeWCR intiWcr = new InitializeWCR();
		intiWcr.getWiseCrowdRecConfigInfo();
		intiWcr.cassandraInitial();
		intiWcr.ElasticsearchInitial();
		
		Topology t = new Topology();
		
		boolean isDynamicSearch = false;
		t.startTopology(isDynamicSearch, "wcr_topology_back", "movie");
		
//		boolean isDynamicSearch = true;
//		t.startTopology(isDynamicSearch, "wcr_topology_dyna", "movie");
	}
}

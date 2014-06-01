package com.feiyu.storm.streamingdatacollection;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.TException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.feiyu.util.InitializeWCR;

public class TestTopology {
	static InitializeWCR initWcr = new InitializeWCR();
	
	@BeforeClass
	//executed only once, before the first test
	public static void setUpClass() throws IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, URISyntaxException, TException {
		initWcr.getWiseCrowdRecConfigInfo();
		initWcr.cassandraInitial();
		initWcr.elasticsearchInitial();
		PropertyConfigurator.configure(Topology.class.getClassLoader().getResource("log4j.properties"));
	}
	
	@Test
	public void testWholeProcess() throws IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, URISyntaxException, TException, InterruptedException {
		Thread threadBack = new Thread () {
			public void run () {
				Topology t = new Topology();

				boolean isDynamicSearch = false;
				try {
					t.startTopology(isDynamicSearch, "wcr_topology_back", "movie");
				} catch (NoSuchFieldException | IllegalAccessException | InstantiationException
						| ClassNotFoundException | IOException | URISyntaxException | TException e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread threadDyna = new Thread () {
			public void run () {
				Topology t = new Topology();

				boolean isDynamicSearch = true;
				try {
					t.startTopology(isDynamicSearch, "wcr_topology_dyna", "movie");
				} catch ( NoSuchFieldException | IllegalAccessException | InstantiationException
						| ClassNotFoundException | IOException | URISyntaxException | TException e) {
					e.printStackTrace();
				}
			}
		};
		threadBack.start();
		Thread.sleep(25000);
		threadDyna.start();
	}
	
	@Test
	public void testWholeProcess_Dynamic() throws IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, URISyntaxException, TException {
	}
}

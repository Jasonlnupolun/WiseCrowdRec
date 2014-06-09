package com.feiyu.storm.streamingdatacollection;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.thrift.TException;

import com.feiyu.utils.InitializeWCR;
import com.omertron.themoviedbapi.MovieDbException;

public class StartBackgroundTopologyAutomatically extends HttpServlet {
	private static final long serialVersionUID = 7251661877569490388L;

	public void init() throws ServletException {
		InitializeWCR initWcr = new InitializeWCR();
		BackgroundTopology t = new BackgroundTopology();
//		boolean isFakeTopologyForTest = false;
		boolean isFakeTopologyForTest = true;

		try {
			initWcr.getWiseCrowdRecConfigInfo();
			initWcr.twitterInitBack();
			initWcr.cassandraInitial();
			initWcr.coreNLPInitial();
			initWcr.themoviedbOrgInitial();

			t.startTopology(isFakeTopologyForTest, "wcr_topology_back", "I rated #IMDb");
		} catch ( NoSuchFieldException | IllegalAccessException | InstantiationException
				| ClassNotFoundException | URISyntaxException | IOException | TException | MovieDbException e) {
			e.printStackTrace();
		}
	}
}

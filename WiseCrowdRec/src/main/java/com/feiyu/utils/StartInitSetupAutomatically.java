package com.feiyu.utils;
/**
 * @author feiyu
 */

import javax.servlet.http.HttpServlet;

import com.feiyu.storm.streamingdatacollection.BackgroundTopology;
import com.feiyu.utils.InitializeWCR;
import com.feiyu.websocket.SparkHistogramWebSocketHandler;
import com.feiyu.websocket.SparkWebSocketHandler;
import com.feiyu.websocket.StormHistogramChartWebSocketHandler;

public class StartInitSetupAutomatically extends HttpServlet {

	private static final long serialVersionUID = -1685928905690566889L;

	public void init() {
		InitializeWCR initWcr = new InitializeWCR();

		try {
			initWcr.getWiseCrowdRecConfigInfo();
			initWcr.twitterInitDyna();
			initWcr.twitterInitBack();
			initWcr.coreNLPInitial();
			initWcr.cassandraInitial();
			initWcr.elasticsearchInitial();
			initWcr.themoviedbOrgInitial();
			initWcr.rabbitmqInit();
			initWcr.initializeRBM();
			
			GlobalVariables.SPARK_TWT_STREAMING.sparkInit();

			Thread StormHistogramChartWebSocketHandlerThread = new Thread () {
				public void run () {
					try {
						StormHistogramChartWebSocketHandler.start();
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			};
			StormHistogramChartWebSocketHandlerThread.start();
			
			Thread SparkHistogramWebSocketHandlerThread = new Thread () {
				public void run () {
					try {
						SparkHistogramWebSocketHandler.start();
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			};
			SparkHistogramWebSocketHandlerThread.start();

			Thread SparkWebSocketHandlerThread = new Thread () {
				public void run () {
					try {
						SparkWebSocketHandler.start();//Open Spark server side websocket
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			};
			SparkWebSocketHandlerThread.start();
			
			Thread StormThread = new Thread () {
				public void run () {
					try {
						BackgroundTopology t = new BackgroundTopology();
//						boolean isFakeTopologyForTest = true;
						boolean isFakeTopologyForTest = false;
						t.startTopology(isFakeTopologyForTest, "wcr_topology_back", "I rated #IMDb");
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			};
			StormThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

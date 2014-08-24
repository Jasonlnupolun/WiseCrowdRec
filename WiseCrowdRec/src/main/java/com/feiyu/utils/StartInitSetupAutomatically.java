package com.feiyu.utils;

import javax.servlet.http.HttpServlet;

import com.feiyu.utils.InitializeWCR;
import com.feiyu.websocket.SparkWebSocketHandler;

public class StartInitSetupAutomatically extends HttpServlet {

	private static final long serialVersionUID = -1685928905690566889L;

	public void init() {
		InitializeWCR initWcr = new InitializeWCR();

		try {
			initWcr.getWiseCrowdRecConfigInfo();
			initWcr.twitterInitDyna();
			initWcr.coreNLPInitial();
			initWcr.cassandraInitial();
			initWcr.elasticsearchInitial();
			initWcr.themoviedbOrgInitial();
			initWcr.rabbitmqInit_spark();
			//			initWcr.rabbitmqInit_smgSubGraph();

			GlobalVariables.SPARK_TWT_STREAMING.sparkInit();

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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

package com.feiyu.deeplearning.RBM;

import com.feiyu.spark.SparkTwitterStreaming;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;
import com.feiyu.websocket.SparkHistogramWebSocketHandler;
import com.feiyu.websocket.SparkWebSocketHandler;

public class PredictUserPreferencesSparkStreaming implements java.io.Serializable {
	private static final long serialVersionUID = 7231112414625079948L;

	public void init() throws Exception {

		InitializeWCR initWcr = new InitializeWCR();

		initWcr.getWiseCrowdRecConfigInfo();
		initWcr.twitterInitDyna();
		initWcr.elasticsearchInitial();
		initWcr.coreNLPInitial();
		initWcr.calaisNLPInitial();
		initWcr.getFreebaseInfo();

		initWcr.initializeRBM();

		initWcr.themoviedbOrgInitial();
		initWcr.rabbitmqInit();

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
	}

	public static void main(String[] argv) throws Exception {
		PredictUserPreferencesSparkStreaming rbmSpark = new PredictUserPreferencesSparkStreaming();
		rbmSpark.init();

		SparkTwitterStreaming sts = new SparkTwitterStreaming();
		sts.sparkInit();
		sts.startSpark("movie");

		GlobalVariables.RBM_PREDICT_USER_PREF.startPredictUserPreferences();
	}
}

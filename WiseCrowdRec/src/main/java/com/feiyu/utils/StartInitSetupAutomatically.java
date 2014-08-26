package com.feiyu.utils;

import javax.servlet.http.HttpServlet;

import com.feiyu.utils.InitializeWCR;

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
			GlobalVariables.SPARK_TWT_STREAMING.sparkInit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

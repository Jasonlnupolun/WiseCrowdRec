package com.feiyu.utils;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServlet;

import org.apache.thrift.TException;

import com.omertron.themoviedbapi.MovieDbException;
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
//			initWcr.rabbitmqInit_smgSubGraph();
		} catch (NoSuchFieldException | IllegalAccessException | InstantiationException | MovieDbException
				| ClassNotFoundException | URISyntaxException | IOException | TException e) {
			e.printStackTrace();
		}
		
		GlobalVariables.SPARK_TWT_STREAMING.sparkInit();
	}
}

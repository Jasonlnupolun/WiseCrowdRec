package com.feiyu.spark;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.feiyu.util.GlobalVariables;
import com.feiyu.util.InitializeWCR;

public class TestSparkTwitterStreaming implements java.io.Serializable {
	private static final long serialVersionUID = 4530749294942204573L;

	@Before
	public void init() throws IOException {
		GlobalVariables.WCR_PROPS = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        GlobalVariables.WCR_PROPS.load(in);
	}
	
	@Test
	public void testSpark() throws IOException {
		InitializeWCR intiWcr = new InitializeWCR();
		intiWcr.elasticsearchInitial();
		
		SparkTwitterStreaming sts = new SparkTwitterStreaming();
		sts.twitter4jInit();
		sts.sparkInit();
		sts.startSpark("movie");
	}

}

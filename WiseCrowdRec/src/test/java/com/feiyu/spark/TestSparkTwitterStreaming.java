package com.feiyu.spark;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.feiyu.utils.InitializeWCR;

public class TestSparkTwitterStreaming implements java.io.Serializable {
	private static final long serialVersionUID = 4530749294942204573L;
	InitializeWCR initWcr = new InitializeWCR();

	@Before
	public void init() throws IOException {
		initWcr.getWiseCrowdRecConfigInfo();
		initWcr.twitterInitDyna();
		initWcr.elasticsearchInitial();
		initWcr.coreNLPInitial();
	}

	@Test
	public void testSpark() throws IOException {
		SparkTwitterStreaming sts = new SparkTwitterStreaming();
		sts.sparkInit();
		sts.startSpark("movie");
	}
}

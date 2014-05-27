package com.feiyu.spark;
/**
 * reference: http://ampcamp.berkeley.edu/3/exercises/realtime-processing-with-spark-streaming.html
 * https://github.com/apache/spark/blob/c852201ce95c7c982ff3794c114427eb33e92922/external/twitter/src/test/java/org/apache/spark/streaming/twitter/JavaTwitterStreamSuite.java
 * https://github.com/amplab/training/tree/ampcamp4/streaming
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.*;

import com.feiyu.util.GlobalVariables;

import twitter4j.Status;
import twitter4j.conf.ConfigurationBuilder;

public class SparkTwitterStreaming {

	public static void main(String[] argv) throws IOException {
		GlobalVariables.WCR_PROPS = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        GlobalVariables.WCR_PROPS.load(in);
        
		// Set Twitter app oauth info
		ConfigurationBuilder _twitterConfBuilder_dyna= new ConfigurationBuilder();
		//twitterConf.setIncludeEntitiesEnabled(true);
		_twitterConfBuilder_dyna.setDebugEnabled(Boolean.valueOf(GlobalVariables.WCR_PROPS.getProperty("debug")))
		.setOAuthConsumerKey(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerKey2"))
		.setOAuthConsumerSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerSecret2"))
		.setOAuthAccessToken(GlobalVariables.WCR_PROPS.getProperty("oauth.accessToken2"))
		.setOAuthAccessTokenSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.accessTokenSecret2"));

		// Set spark streaming info
		JavaStreamingContext ssc = new JavaStreamingContext(
				"spark://Feis-MacBook-Pro.local:7077", "JavaTwitterStreaming", 
				new Duration(1000), System.getenv("SPARK_HOME"), 
				JavaStreamingContext.jarOfClass(SparkTwitterStreaming.class));

		JavaDStream<Status> tweets = TwitterUtils.createStream(ssc);

		JavaDStream<String> statuses = tweets.map(
				new Function<Status, String>() {
					private static final long serialVersionUID = -1124355253292906965L;
					public String call(Status status) { return status.getText(); }
				}
				);
		statuses.print();
		
//		// HDFS directory for checkpointing
		/*
		 * checkpoint saves the RDD to an HDFS file
		 * http://apache-spark-user-list.1001560.n3.nabble.com/checkpoint-and-not-running-out-of-disk-space-td1525.html
		 */
//		String checkpointDir = TutorialHelper.getHdfsUrl() + "/checkpoint/";
//		ssc.checkpoint(checkpointDir);
		
		ssc.start();
	}
}

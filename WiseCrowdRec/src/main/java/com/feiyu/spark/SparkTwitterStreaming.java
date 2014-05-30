package com.feiyu.spark;
/**
 * reference: http://ampcamp.berkeley.edu/3/exercises/realtime-processing-with-spark-streaming.html
 * https://github.com/amplab/training/blob/ampcamp4/streaming/java/Tutorial.java
 * https://github.com/apache/spark/blob/c852201ce95c7c982ff3794c114427eb33e92922/external/twitter/src/test/java/org/apache/spark/streaming/twitter/JavaTwitterStreamSuite.java
 * https://github.com/amplab/training/tree/ampcamp4/streaming
 * modified by feiyu
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.*;

import twitter4j.Status;

public class SparkTwitterStreaming {
	private static Properties props;

	public static void main(String[] argv) throws IOException {
		props = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
		props.load(in);

		System.setProperty("twitter4j.oauth.consumerKey", props.getProperty("oauth.consumerKey2"));
		System.setProperty("twitter4j.oauth.consumerSecret", props.getProperty("oauth.consumerSecret2"));
		System.setProperty("twitter4j.oauth.accessToken", props.getProperty("oauth.accessToken2"));
		System.setProperty("twitter4j.oauth.accessTokenSecret", props.getProperty("oauth.accessTokenSecret2"));

		// Set spark streaming info
		JavaStreamingContext ssc = new JavaStreamingContext(
				"local[2]", "JavaTwitterStreaming", 
				new Duration(1000), System.getenv("SPARK_HOME"), 
				JavaStreamingContext.jarOfClass(SparkTwitterStreaming.class));

		//	HDFS directory for checkpointing
		/*
		 * checkpoint saves the RDD to an HDFS file
		 * http://apache-spark-user-list.1001560.n3.nabble.com/checkpoint-and-not-running-out-of-disk-space-td1525.html
		 * dfs.namenode.checkpoint.dir -> hdfs-site.xml
		 */
		//		String checkpointDir = TutorialHelper.getHdfsUrl() + "/checkpoint/";

		String checkpointDir = "file:///Users/feiyu/workspace/Hadoop/hdfs/namesecondary/checkpoint";
		ssc.checkpoint(checkpointDir);

		JavaDStream<Status> tweets = TwitterUtils.createStream(ssc);
//		twitter4j.auth.Authorization
		
		JavaDStream<String> statuses = tweets.map(
				new Function<Status, String>() {
					private static final long serialVersionUID = -1124355253292906965L;
					public String call(Status status) { 
						return status.getText(); }
				}
				);
		statuses.print();

		JavaDStream<String> words = statuses.flatMap(
				new FlatMapFunction<String, String>() {
					private static final long serialVersionUID = 3822311085213005330L;
					public Iterable<String> call(String in) {
						return Arrays.asList(in.split(" "));
					}
				}
				);
		words.print();
		
		JavaDStream<String> hashTags = words.filter(
				new Function<String, Boolean>() {
					private static final long serialVersionUID = -6539496769011825490L;
					public Boolean call(String word) { 
						return word.startsWith("#"); }
				}
				);
		hashTags.print();

		ssc.start();
	}
}
package com.feiyu.spark;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.*;

import com.feiyu.nlp.EntityExtractionCalais;
import com.feiyu.nlp.SentimentAnalyzerCoreNLP;
import com.feiyu.springmvc.model.Tweet;

import twitter4j.Status;
import twitter4j.auth.Authorization;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

public class SparkTwitterStreaming implements java.io.Serializable   {
	private static final long serialVersionUID = -1741488739982924186L;
	private static Properties props;
	private static JavaStreamingContext ssc;
	private Authorization auth;

	public void twitter4jInit() throws IOException {
		props = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
		props.load(in);

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(props.getProperty("oauth.consumerKey2"))
		.setOAuthConsumerSecret(props.getProperty("oauth.consumerSecret2"))
		.setOAuthAccessToken(props.getProperty("oauth.accessToken2"))
		.setOAuthAccessTokenSecret(props.getProperty("oauth.accessTokenSecret2"));

		auth = new OAuthAuthorization(cb.build());
	}

	public void sparkInit() {
		// Set spark streaming info
		ssc = new JavaStreamingContext(
				"local[2]", "SparkTwitterStreamingJava", 
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
	}

	public void startSpark(String searchPhrases) {
		String[] keywords = searchPhrases.split(" ");

		JavaDStream<Status> tweets = TwitterUtils.createStream(ssc, auth, keywords);

		JavaDStream<Tweet> statuses = tweets.map(
				new Function<Status, Tweet>() {
					private static final long serialVersionUID = -1124355253292906965L;
					public Tweet call(Status tweetStatus) throws IOException { 

						Tweet _t = new Tweet();
						EntityExtractionCalais entityExtract = new EntityExtractionCalais();
						HashMap<String, String> hm = null;

						// Get Metadata
						String lang = tweetStatus.getIsoLanguageCode();
						if (lang.equals("en")) {
							_t.setLang(lang);
							_t.setTime(tweetStatus.getCreatedAt());
							_t.setText(tweetStatus.getText());

							hm = entityExtract.getEntities(_t.getText());
							_t.setEntities(hm);

							SentimentAnalyzerCoreNLP sentiment = new SentimentAnalyzerCoreNLP();
							_t.setSentiment(sentiment.getSentiment(tweetStatus.getText()));
							return _t;
						}
						return null;
					} 
				}
				);
		statuses.print();

		ssc.start();
	}

	public static void main(String[] argv) throws IOException {
		SparkTwitterStreaming sts = new SparkTwitterStreaming();
		sts.twitter4jInit();
		sts.sparkInit();
		sts.startSpark("movie music");
	}
}
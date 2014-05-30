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

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.*;

import scala.Tuple2;
import twitter4j.Status;

public class SparkTwitterStreamingDemo implements java.io.Serializable   {
	private static final long serialVersionUID = -1741488739982924186L;
	private static Properties props;
	private static JavaStreamingContext ssc;

	public void twitter4jInit() throws IOException {
		props = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
		props.load(in);

		System.setProperty("twitter4j.oauth.consumerKey", props.getProperty("oauth.consumerKey2"));
		System.setProperty("twitter4j.oauth.consumerSecret", props.getProperty("oauth.consumerSecret2"));
		System.setProperty("twitter4j.oauth.accessToken", props.getProperty("oauth.accessToken2"));
		System.setProperty("twitter4j.oauth.accessTokenSecret", props.getProperty("oauth.accessTokenSecret2"));
	}

	public void sparkInit() {
		// Set spark streaming info
		ssc = new JavaStreamingContext(
				"local[2]", "JavaTwitterStreaming", 
				new Duration(1000), System.getenv("SPARK_HOME"), 
				JavaStreamingContext.jarOfClass(SparkTwitterStreamingDemo.class));

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

	public void startSpark() {
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

		JavaPairDStream<String, Integer> tuples = hashTags.map(
				new PairFunction<String, String, Integer>() {
					private static final long serialVersionUID = 1L;

					public Tuple2<String, Integer> call(String in) {
						return new Tuple2<String, Integer>(in, 1);
					}
				}
				);
		JavaPairDStream<String, Integer> counts = tuples.reduceByKeyAndWindow(
				new Function2<Integer, Integer, Integer>() {
					private static final long serialVersionUID = 5529071413621576514L;

					public Integer call(Integer i1, Integer i2) { return i1 + i2; }
				},
				new Function2<Integer, Integer, Integer>() {
					private static final long serialVersionUID = -6489078100741562635L;

					public Integer call(Integer i1, Integer i2) { return i1 - i2; }
				},
				new Duration(60 * 5 * 1000),
				new Duration(1 * 1000)
				);
		counts.print();

		JavaPairDStream<Integer, String> swappedCounts = counts.map(
				new PairFunction<Tuple2<String, Integer>, Integer, String>() {
					private static final long serialVersionUID = 1L;

					public Tuple2<Integer, String> call(Tuple2<String, Integer> in) {
						return in.swap();
					}
				}
				);
		JavaPairDStream<Integer, String> sortedCounts = swappedCounts.transform(
				new Function<JavaPairRDD<Integer, String>, JavaPairRDD<Integer, String>>() {
					private static final long serialVersionUID = -5874306184950642137L;

					public JavaPairRDD<Integer, String> call(JavaPairRDD<Integer, String> in) throws Exception {
						return in.sortByKey(false);
					}
				});
		sortedCounts.foreach(
				new Function<JavaPairRDD<Integer, String>, Void> () {
					private static final long serialVersionUID = 4754852556443175871L;

					public Void call(JavaPairRDD<Integer, String> rdd) {
						String out = "\nTop 10 hashtags:\n";
						for (Tuple2<Integer, String> t: rdd.take(10)) {
							out = out + t.toString() + "\n";
						}
						System.out.println(out);
						return null;
					}
				}
				);

		ssc.start();
	}

	public static void main(String[] argv) throws IOException {
		SparkTwitterStreamingDemo sts = new SparkTwitterStreamingDemo();
		sts.twitter4jInit();
		sts.sparkInit();
		sts.startSpark();
	}
}
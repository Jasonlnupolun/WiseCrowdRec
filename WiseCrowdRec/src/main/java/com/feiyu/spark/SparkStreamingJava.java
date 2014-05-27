package com.feiyu.spark;
/**
 * From https://spark.apache.org/docs/0.9.0/streaming-programming-guide.html
 * http://ampcamp.berkeley.edu/3/exercises/realtime-processing-with-spark-streaming.html
 * modified by feiyu
 */

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

import com.google.common.collect.Lists;

public class SparkStreamingJava {
	@SuppressWarnings("serial")
	public static void main(String[] argv) {
		SparkConf sparkConf = new SparkConf()
			.setAppName("JavaStreamingWordCount")
			.setMaster("local")
			.setJars(JavaStreamingContext.jarOfClass(SparkStreamingJava.class));

		// Create a StreamingContext with a SparkConf configuration
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(1000));

		// Create a DStream that will connect to serverIP:serverPort
		JavaDStream<String> lines = jssc.socketTextStream("Feis-MacBook-Pro.local", 7077); //@ modify this

		// Split each line into words
		JavaDStream<String> words = lines.flatMap(
				new FlatMapFunction<String, String>() {
					@Override public Iterable<String> call(String x) {
						return Lists.newArrayList(x.split(" "));
					}
				});
		// Count each word in each batch
		JavaPairDStream<String, Integer> pairs = words.map(
				new PairFunction<String, String, Integer>() {
					@Override public Tuple2<String, Integer> call(String s) throws Exception {
						return new Tuple2<String, Integer>(s, 1);
					}
				});
		JavaPairDStream<String, Integer> wordCounts = pairs.reduceByKey(
				new Function2<Integer, Integer, Integer>() {
					@Override public Integer call(Integer i1, Integer i2) throws Exception {
						return i1 + i2;
					}
				});
		wordCounts.print();     // Print a few of the counts to the console
		jssc.start();              // Start the computation
		jssc.awaitTermination();   // Wait for the computation to terminate
	}
}

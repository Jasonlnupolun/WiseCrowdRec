package com.feiyu.utils;

import java.util.Properties;

import twitter4j.conf.ConfigurationBuilder;
import mx.bigdata.jcalais.CalaisClient;

import com.feiyu.database.AstyanaxCassandraManipulator;
import com.feiyu.elasticsearch.JestElasticsearchManipulator;
import com.feiyu.spark.SparkTwitterStreaming;
import com.omertron.themoviedbapi.TheMovieDbApi;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class GlobalVariables {
	public static Properties WCR_PROPS;
	
	public static SparkTwitterStreaming SPARK_TWT_STREAMING = new SparkTwitterStreaming();

	public static ConfigurationBuilder TWT_CONF_BUILDER_BACK; 
	public static ConfigurationBuilder TWT_CONF_BUILDER_DYNA; 

	public static AstyanaxCassandraManipulator AST_CASSANDRA_MNPLT;
	public static JestElasticsearchManipulator JEST_ES_MNPLT;
	public static boolean CLEAN_BEFORE_INSERT_ES = true;

	public static CalaisClient CALAIS_CLIENT; 
	public static StanfordCoreNLP CORENLP_PIPELINE;

	public static TheMovieDbApi TMDB;
	public static final String SENTI_CSS = "alert alert-success"; // change later

	public static final String RABBITMQ_QUEUE_NAME = "WCR_SPARK_RABBITMQ";
	public static Connection RABBITMQ_CNCT;
	public static Channel RABBITMQ_CHANNEL;
}

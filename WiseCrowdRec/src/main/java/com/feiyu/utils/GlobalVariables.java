package com.feiyu.utils;

import java.util.Properties;

import twitter4j.conf.ConfigurationBuilder;
import mx.bigdata.jcalais.CalaisClient;

import com.feiyu.Cassandra.AstyanaxCassandraManipulator;
import com.feiyu.Cassandra.AstyanaxCassandraUserList;
import com.feiyu.elasticsearch.JestElasticsearchManipulator;
import com.feiyu.spark.SparkTwitterStreaming;
import com.netflix.astyanax.Keyspace;
import com.omertron.themoviedbapi.TheMovieDbApi;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class GlobalVariables {
	public static Properties WCR_PROPS;
	
	public static String TWT_APP_OAUTH_CONSUMER_KEY ;
	public static String TWT_APP_OAUTH_CONSUMER_SECRET ;	
	
	public static SparkTwitterStreaming SPARK_TWT_STREAMING = new SparkTwitterStreaming();

	public static ConfigurationBuilder TWT_CONF_BUILDER_BACK; 
	public static ConfigurationBuilder TWT_CONF_BUILDER_DYNA; 

	public static AstyanaxCassandraManipulator AST_CASSANDRA_MNPLT;
	public static AstyanaxCassandraUserList AST_CASSANDRA_UL;
	public static Keyspace KS_AST;
	
	public static JestElasticsearchManipulator JEST_ES_MNPLT;
	public static boolean CLEAN_BEFORE_INSERT_ES = true;

	public static CalaisClient CALAIS_CLIENT; 
	public static StanfordCoreNLP CORENLP_PIPELINE;

	public static TheMovieDbApi TMDB;
	public static final String SENTI_CSS = "alert alert-success"; // change later

	public static final String RABBITMQ_QUEUE_NAME_SPARK = "WCR_SPARK_RABBITMQ";
	public static Connection RABBITMQ_CNCT;
	public static Channel RABBITMQ_CHANNEL;
//	public static Connection RABBITMQ_CNCT_SPARK;
//	public static Channel RABBITMQ_CHANNEL_SPARK;
	
	public static final String RABBITMQ_QUEUE_NAME_SMGSUBGRAPH= "WCR_SMGSUBGRAPH_RABBITMQ";
//	public static Connection RABBITMQ_CNCT_SMGSUBGRAPH;
//	public static Channel RABBITMQ_CHANNEL_SMGSUBGRAPH;
}

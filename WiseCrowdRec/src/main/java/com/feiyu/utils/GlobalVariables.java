package com.feiyu.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Queue;

import twitter4j.conf.ConfigurationBuilder;
import mx.bigdata.jcalais.CalaisClient;

import com.feiyu.Cassandra.AstyanaxCassandraManipulator;
import com.feiyu.Cassandra.AstyanaxCassandraUserList;
import com.feiyu.deeplearning.RBM.PredictUserPreferences;
import com.feiyu.elasticsearch.JestElasticsearchManipulator;
import com.feiyu.semanticweb.freebase.GetActorMovieGenreSubgraphVectorNEdge;
import com.feiyu.spark.SparkTwitterStreaming;
import com.feiyu.springmvc.model.MovieWithCountComparable;
import com.feiyu.springmvc.model.RBMClientWeightMatixForPredict;
import com.feiyu.springmvc.model.RBMDataQueueElementInfo;
import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.springmvc.model.RBMUserInfo;
import com.feiyu.storm.streamingdatacollection.bolt.MovieCounter;
import com.google.api.client.http.GenericUrl;
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
	public static final String RABBITMQ_QUEUE_NAME_SMCSUBGRAPH= "WCR_SMCSUBGRAPH_RABBITMQ";
	public static final String RABBITMQ_QUEUE_NAME_STORMHISTOGRAMCHART= "WCR_STORMHISTOGRAMCHART_RABBITMQ";
	public static final String RABBITMQ_QUEUE_NAME_SPARKHISTOGRAMCHART= "WCR_SPARKHISTOGRAMCHART_RABBITMQ";
	public static final String RABBITMQ_QUEUE_NAME_RBMDATACOLLECTION= "WCR_RBMDATACOLLECTION_RABBITMQ";
	public static Connection RABBITMQ_CNCT;
	public static Channel RABBITMQ_CHANNEL;

	public static final PriorityQueue<MovieWithCountComparable> STORM_MOVIELIST_HEAP = new PriorityQueue<MovieWithCountComparable>();
	public static final HashMap<String, MovieCounter> STORM_MOVIELIST_HM = new HashMap<String, MovieCounter>();

	public static final GetActorMovieGenreSubgraphVectorNEdge FREEBASE_GET_ACTOR_MOVIES = new GetActorMovieGenreSubgraphVectorNEdge();
	public static GenericUrl FREEBASE_URL;

	public static int D3_MAX_IDX = -1; 
	
	public static final PredictUserPreferences RBM_PREDICT_USER_PREF = new PredictUserPreferences();
//	public static long RBM_OVERHEAD;
//	public static long RBM_DATA_COLLECTION_DURATION;
//	public static long RBM_EACH_TRAIN_DURATION;
//	public static long RBM_EACH_TEST_DURATION;
	public static int RBM_USER_MAX_NUMBER_TRAIN;
	public static int RBM_USER_MAX_NUMBER_TEST;
	public static int RBM_SIZE_SOFTMAX;
	public static int RBM_SIZE_HIDDEN_UNITS;
	public static double RBM_LEARNING_RATE;
	public static int RBM_NUM_EPOCHS;
	public static boolean RBM_DRAW_CHART;
	
	public static Thread RBM_COLLECT_TRAINING_DATA_THREAD;
	public static Thread RBM_COLLECT_TESTING_DATA_THREAD;

	public static int KTH_RBM = -1;
	public static final Queue<RBMDataQueueElementInfo> RBM_DATA_QUEUE = new LinkedList<RBMDataQueueElementInfo>(); // for the whole RBMs process
	public static RBMDataQueueElementInfo RBM_DATA_QUEUE_ELEMENT; 
	public static HashMap<String, RBMMovieInfo> RBM_MOVIE_HASHMAP;
	public static ArrayList<String> RBM_MOVIE_LIST;
	public static int RBM_MOVIE_MAX_IDX;
	public static HashMap<String, RBMUserInfo> RBM_USER_HASHMAP;
	public static ArrayList<String> RBM_USER_LIST;
	public static int RBM_USER_MAX_IDX;
	public static RBMClientWeightMatixForPredict RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT;
}

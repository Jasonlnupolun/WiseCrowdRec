package com.feiyu.utils;
/**
 * @author feiyu
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

//import redis.clients.jedis.Jedis;
import twitter4j.conf.ConfigurationBuilder;
import mx.bigdata.jcalais.CalaisClient;

import com.feiyu.classes.RBMClientWeightMatixForPredict;
import com.feiyu.classes.RBMDataQueueElementInfo;
import com.feiyu.classes.RBMMovieInfo;
import com.feiyu.classes.RBMUserInfo;
import com.feiyu.freebase.FreebaseOperations;
//import com.feiyu.redis.RedisOperations;
import com.feiyu.spark.SparkTwitterStreaming;
import com.feiyu.starter.StartRBMDataCollectionModelTrainingTesting;
import com.google.api.client.http.GenericUrl;
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

  public static boolean CLEAN_BEFORE_INSERT_ES = true;

  public static CalaisClient CALAIS_CLIENT; 
  public static StanfordCoreNLP CORENLP_PIPELINE;

  public static TheMovieDbApi TMDB;
  public static final String SENTI_CSS = "alert alert-success"; // change later

  public static final String RABBITMQ_QUEUE_NAME_SPARK = "WCR_SPARK_RABBITMQ";
  //public static final String RABBITMQ_QUEUE_NAME_SPARKHISTOGRAMCHART= "WCR_SPARKHISTOGRAMCHART_RABBITMQ";
  public static final String RABBITMQ_QUEUE_NAME_RBMDATACOLLECTION= "WCR_RBMDATACOLLECTION_RABBITMQ";
  public static final String RABBITMQ_QUEUE_NAME_PANEL_TWEET= "WCR_PANEL_TWEET_RABBITMQ";
  public static final String RABBITMQ_QUEUE_NAME_PANEL_PERSONNAME= "WCR_PANEL_PERSONNAME_RABBITMQ";
  public static final String RABBITMQ_QUEUE_NAME_PANEL_PSN_HISTOGRAM= "WCR_PANEL_PSN_HISTOGRAM_RABBITMQ";
  public static final String RABBITMQ_QUEUE_NAME_PANEL_MOVIE= "WCR_PANEL_MOVIE_RABBITMQ";
  public static final String RABBITMQ_QUEUE_NAME_PANEL_REC= "WCR_PANEL_REC_RABBITMQ";
  public static final String RABBITMQ_QUEUE_NAME_SMCSUBGRAPH= "WCR_SMCSUBGRAPH_RABBITMQ";
  public static Connection RABBITMQ_CNCT;
  public static Channel RABBITMQ_CHANNEL;

  public static final FreebaseOperations FREEBASE_OPS = new FreebaseOperations();
  public static GenericUrl FREEBASE_URL;

  public static final StartRBMDataCollectionModelTrainingTesting RBM_DATA_CLC_MDL_TRN_TST = new StartRBMDataCollectionModelTrainingTesting();
  //public static long RBM_OVERHEAD;
  //public static long RBM_DATA_COLLECTION_DURATION;
  //public static long RBM_EACH_TRAIN_DURATION;
  //public static long RBM_EACH_TEST_DURATION;
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

  public static final HashSet<String> RBM_CLIENT_DISLIKED_GENRES = new HashSet<String>();
  public static final HashSet<String> RBM_CLIENT_DISLIKED_MOVIES = new HashSet<String>();
  public static HashMap<Integer, Integer> RBM_CLIENT_RATED_MOVIES_CUR_RBM;
  public static RBMClientWeightMatixForPredict RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT;
  public static double[][][] RBM_Client_PREDICTED_PREFERENCE;

  public static final String LOG_DIR="/Users/feiyu/workspace/wisecrowdrec-nodejs-logs/";
  
  //public static Jedis JEDIS_API = new Jedis("localhost");
  //public static RedisOperations JEDIS_OPS = new RedisOperations();
  
  public static String RECENT_ACTOR_MOVIES_GENRES_GRAPH = null;
  public static boolean FREEBASE_IS_REACH_LIMIT_9_13 = false;
  public static boolean FREEBASE_IS_REACH_LIMIT_13_17 = false;
  public static boolean FREEBASE_IS_REACH_LIMIT_OTHERS = false;
}

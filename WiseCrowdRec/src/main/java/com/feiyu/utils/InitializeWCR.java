package com.feiyu.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import mx.bigdata.jcalais.rest.CalaisRestClient;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

import twitter4j.conf.ConfigurationBuilder;

import com.feiyu.Cassandra.AstyanaxCassandraManipulator;
import com.feiyu.Cassandra.AstyanaxCassandraUserList;
import com.feiyu.elasticsearch.JestElasticsearchManipulator;
import com.google.api.client.http.GenericUrl;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.rabbitmq.client.ConnectionFactory;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class InitializeWCR implements java.io.Serializable{
	private static final long serialVersionUID = 8908925050404621467L;

	public void getWiseCrowdRecConfigInfo () throws IOException {
		GlobalVariables.WCR_PROPS = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
		GlobalVariables.WCR_PROPS.load(in);
	}
	
	public void signInWithTwitterGetAppOauth() {
		GlobalVariables.TWT_APP_OAUTH_CONSUMER_KEY = GlobalVariables.WCR_PROPS.getProperty("oauth.consumerKey3");
		GlobalVariables.TWT_APP_OAUTH_CONSUMER_SECRET = GlobalVariables.WCR_PROPS.getProperty("oauth.consumerSecret3");	
	}

	public void twitterInitBack() {
		// Set Twitter app oauth infor
		GlobalVariables.TWT_CONF_BUILDER_BACK = new ConfigurationBuilder();
		//twitterConf.setIncludeEntitiesEnabled(true);
		GlobalVariables.TWT_CONF_BUILDER_BACK.setDebugEnabled(Boolean.valueOf(GlobalVariables.WCR_PROPS.getProperty("debug")))
		.setOAuthConsumerKey(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerKey1"))
		.setOAuthConsumerSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerSecret1"))
		.setOAuthAccessToken(GlobalVariables.WCR_PROPS.getProperty("oauth.accessToken1"))
		.setOAuthAccessTokenSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.accessTokenSecret1"));
	}

	public void twitterInitDyna() throws Exception {
		// Set Twitter app oauth infor
		GlobalVariables.TWT_CONF_BUILDER_DYNA = new ConfigurationBuilder();
		//twitterConf.setIncludeEntitiesEnabled(true);
		GlobalVariables.TWT_CONF_BUILDER_DYNA.setDebugEnabled(Boolean.valueOf(GlobalVariables.WCR_PROPS.getProperty("debug")))
		.setOAuthConsumerKey(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerKey2"))
		.setOAuthConsumerSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.consumerSecret2"))
		.setOAuthAccessToken(GlobalVariables.WCR_PROPS.getProperty("oauth.accessToken2"))
		.setOAuthAccessTokenSecret(GlobalVariables.WCR_PROPS.getProperty("oauth.accessTokenSecret2"));	
	}

	public void cassandraInitial() 
			throws NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, 
			IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, 
			URISyntaxException, IOException, TException {
		GlobalVariables.AST_CASSANDRA_MNPLT= new AstyanaxCassandraManipulator("wcrCluster","wcrkeyspace","wcrPool","localhost",9160);
		GlobalVariables.AST_CASSANDRA_MNPLT.initialSetup();
		
		GlobalVariables.AST_CASSANDRA_UL = new AstyanaxCassandraUserList("userlist");
		GlobalVariables.AST_CASSANDRA_UL.initialSetup();
	}

	public void elasticsearchInitial() {
		GlobalVariables.JEST_ES_MNPLT = new JestElasticsearchManipulator("wcresidx","dynamicsearchestype");
	} 

	public void calaisNLPInitial() {
		GlobalVariables.CALAIS_CLIENT = new CalaisRestClient(GlobalVariables.WCR_PROPS.getProperty("CalaisApiKey"));
	}

	public void coreNLPInitial() { 
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
		GlobalVariables.CORENLP_PIPELINE = new StanfordCoreNLP(props);
	}

	public void themoviedbOrgInitial() throws MovieDbException {
		GlobalVariables.TMDB = new TheMovieDbApi(GlobalVariables.WCR_PROPS.getProperty("themoviedbApiKey"));
	}
	
	public void rabbitmqInit() throws IOException  {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		GlobalVariables.RABBITMQ_CNCT= factory.newConnection();
		GlobalVariables.RABBITMQ_CHANNEL= GlobalVariables.RABBITMQ_CNCT.createChannel();

		GlobalVariables.RABBITMQ_CHANNEL.queueDeclare(GlobalVariables.RABBITMQ_QUEUE_NAME_SPARK, false, false, false, null);
		GlobalVariables.RABBITMQ_CHANNEL.queueDeclare(GlobalVariables.RABBITMQ_QUEUE_NAME_SMCSUBGRAPH, false, false, false, null);
		GlobalVariables.RABBITMQ_CHANNEL.queueDeclare(GlobalVariables.RABBITMQ_QUEUE_NAME_STORMHISTOGRAMCHART, false, false, false, null);
		GlobalVariables.RABBITMQ_CHANNEL.queueDeclare(GlobalVariables.RABBITMQ_QUEUE_NAME_SPARKHISTOGRAMCHART, false, false, false, null);
		GlobalVariables.RABBITMQ_CHANNEL.queueDeclare(GlobalVariables.RABBITMQ_QUEUE_NAME_RBMDATACOLLECTION, false, false, false, null);
	}
	
	public void initializeRBM() {
//		GlobalVariables.RBM_OVERHEAD = 10000;
//		GlobalVariables.RBM_DATA_COLLECTION_DURATION = 30*1000; 
//		GlobalVariables.RBM_EACH_TRAIN_DURATION = 8*8*1000; 
//		GlobalVariables.RBM_EACH_TEST_DURATION = 2*8*1000; 
		GlobalVariables.RBM_USER_MAX_NUMBER_TRAIN = 1; //3
		GlobalVariables.RBM_USER_MAX_NUMBER_TEST = 1; //2
		GlobalVariables.RBM_SIZE_SOFTMAX = 5; // Sentiment(5-point scale/5-way softmax): "Very negative(0)", "Negative(1)", "Neutral(2)", "Positive(3)", "Very positive(4)"
		GlobalVariables.RBM_SIZE_HIDDEN_UNITS = 6; // http://en.wikipedia.org/wiki/List_of_genres
		GlobalVariables.RBM_LEARNING_RATE = 0.1;
		GlobalVariables.RBM_NUM_EPOCHS = 6; //50
		GlobalVariables.RBM_DRAW_CHART = true; // true
	}
	
	public void getFreebaseInfo(){
		GlobalVariables.FREEBASE_URL = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
		// http://wiki.freebase.com/wiki/How_to_obtain_an_API_key
		GlobalVariables.FREEBASE_URL.put("key", GlobalVariables.WCR_PROPS.getProperty("freebase.api.key")); 
	}
}

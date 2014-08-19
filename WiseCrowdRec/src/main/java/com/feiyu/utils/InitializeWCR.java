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

import com.feiyu.database.AstyanaxCassandraManipulator;
import com.feiyu.elasticsearch.JestElasticsearchManipulator;
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

	public void twitterInitDyna() {
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
		GlobalVariables.RABBITMQ_CNCT = factory.newConnection();
		GlobalVariables.RABBITMQ_CHANNEL = GlobalVariables.RABBITMQ_CNCT.createChannel();

		GlobalVariables.RABBITMQ_CHANNEL.queueDeclare(GlobalVariables.RABBITMQ_QUEUE_NAME, false, false, false, null);
	}
}

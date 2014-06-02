package com.feiyu.util;

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

import com.feiyu.database.AstyanaxCassandraManipulator;
import com.feiyu.elasticsearch.JestElasticsearchManipulator;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class InitializeWCR {
	public void getWiseCrowdRecConfigInfo () throws IOException {
		GlobalVariables.WCR_PROPS = new Properties();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        GlobalVariables.WCR_PROPS.load(in);
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
}

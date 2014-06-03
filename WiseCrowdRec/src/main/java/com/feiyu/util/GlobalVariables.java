package com.feiyu.util;

import java.util.Properties;

import twitter4j.conf.ConfigurationBuilder;
import mx.bigdata.jcalais.CalaisClient;

import com.feiyu.database.AstyanaxCassandraManipulator;
import com.feiyu.elasticsearch.JestElasticsearchManipulator;
import com.omertron.themoviedbapi.TheMovieDbApi;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class GlobalVariables {
	public static Properties WCR_PROPS;
	
	public static ConfigurationBuilder TWT_CONF_BUILDER_BACK; 
	public static ConfigurationBuilder TWT_CONF_BUILDER_DYNA; 

	public static AstyanaxCassandraManipulator AST_CASSANDRA_MNPLT;
	public static JestElasticsearchManipulator JEST_ES_MNPLT;
	public static boolean CLEAN_BEFORE_INSERT_ES = true;

	public static CalaisClient CALAIS_CLIENT; 
	public static StanfordCoreNLP CORENLP_PIPELINE;

	public static TheMovieDbApi TMDB;
	public static final String SENTI_CSS = "alert alert-success"; // change later
}

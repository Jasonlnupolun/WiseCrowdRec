package com.feiyu.util;

import java.util.Properties;

import com.feiyu.database.AstyanaxCassandraManipulator;
import com.feiyu.elasticsearch.JestElasticsearchManipulator;

public class GlobalVariables {
	public static AstyanaxCassandraManipulator AST_CASSANDRA_MNPLT;
	public static JestElasticsearchManipulator JEST_ES_MNPLT;
	public static final String SENTI_CSS = "alert alert-success"; // change later
	public static Properties WCR_PROPS;
	public static boolean CLEAN_BEFORE_INSERT_ES = true;
}

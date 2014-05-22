package com.feiyu.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;

import com.feiyu.database.AstyanaxCassandraManipulator;

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
}

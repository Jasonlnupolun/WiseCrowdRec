package com.feiyu.database;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.log4j.PropertyConfigurator;
import org.apache.thrift.TException;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class TestAstyanaxCassandraManipulator {
	private static AstyanaxCassandraManipulator _acm;

	@BeforeClass
	//executed only once, before the first test
	public static void setUpClass()
			throws NotFoundException, InvalidRequestException, NoSuchFieldException, 
			UnavailableException, IllegalAccessException, InstantiationException, URISyntaxException, 
			IOException, TException, ClassNotFoundException, TimedOutException {
		PropertyConfigurator.configure(AstyanaxCassandraManipulator.class.getClassLoader().getResource("log4j.properties"));
		_acm = new AstyanaxCassandraManipulator("wcrCluster","wcrkeyspace","tweets","wcrPool","localhost",9160);
		_acm.initialSetup();
		_acm.cliSchema();
	}

	@Test
	public void testWholeProcess_noAsynchronous() 
			throws ConnectionException, InterruptedException, ExecutionException {
		_acm.insertDataToDB(1, "Ann", "Person");
		_acm.queryDB(1);
	}

	@Test
	public void testWholeProcess_Asynchronous() 
			throws ConnectionException, InterruptedException, ExecutionException {
		_acm.insertDataToDB_asynchronous(2, "Twitter", "Company");
		_acm.queryDB_asynchronous(2);
	}

	@Test
	public void testQueryWithRowKey() 
			throws ConnectionException, InterruptedException, ExecutionException {
		_acm.queryDB_asynchronous(1);
		_acm.queryDB(2);
	}
}

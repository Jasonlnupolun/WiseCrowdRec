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
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;

public class TestAstyanaxCassandraManipulator {
	private static AstyanaxCassandraManipulator _acm;

	@BeforeClass
	//executed only once, before the first test
	public static void setUpClass()
			throws NotFoundException, InvalidRequestException, NoSuchFieldException, 
			UnavailableException, IllegalAccessException, InstantiationException, URISyntaxException, 
			IOException, TException, ClassNotFoundException, TimedOutException {
		PropertyConfigurator.configure(TestAstyanaxCassandraManipulator.class.getClassLoader().getResource("log4j.properties"));
		_acm = new AstyanaxCassandraManipulator("wcrCluster","wcrkeyspace","tweets","wcrPool","localhost",9160);
		_acm.initialSetup();
		_acm.cliSchema();
	}

	@Test
	public void testWholeProcess_noAsynchronous() 
			throws ConnectionException, InterruptedException, ExecutionException {
		_acm.insertDataToDB("6", "Ann", "Person", "1","time","text", "2", "info...");
		_acm.insertDataToDB("3", "Bob", "Person","3","time","text","6","Bob...");
		_acm.queryWithRowkey("6");
	}

	@Test
	public void testWholeProcess_Asynchronous() 
			throws ConnectionException, InterruptedException, ExecutionException {
		_acm.insertDataToDB_asynchronous("2", "Twitter", "Company");
		_acm.queryDB_asynchronous("2");
	}

	@Test
	public void testQueryWithRowKey() 
			throws ConnectionException, InterruptedException, ExecutionException {
		_acm.queryDB_asynchronous("6");
		_acm.queryWithRowkey("2");
	}
	
	@Test
	public void testQueryAllRowsOneCF() {
		Rows<String, String> rows = _acm.queryAllRowsOneCF();
		for (Row<String, String> row : rows) {
		    System.out.println("ROW: " + row.getKey() + " " + row.getColumns().size());
		}
		// can not show the row inserted by _acm.insertDataToDB_asynchronous(...)
	}
}

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
		_acm = new AstyanaxCassandraManipulator("wcrCluster","wcrkeyspace","wcrPool","localhost",9160);
		_acm.initialSetup();
	}

	@Test
	public void testWholeProcess_noAsynchronous_back() 
			throws ConnectionException, InterruptedException, ExecutionException {
		_acm.insertDataToDB("6", "Ann", "Person", "1","time","text", "2", "info...", false);
		_acm.insertDataToDB("3", "Bob", "Person","3","time","text","6","Bob...", false);
		_acm.queryWithRowkey("6", false);
	}

	@Test
	public void testWholeProcess_Asynchronous_back() 
			throws ConnectionException, InterruptedException, ExecutionException {
		_acm.insertDataToDB_asynchronous("2", "Twitter", "Company","3","time","text","6","Bob...", false);
		_acm.queryDB_asynchronous("2",false);
	}

	@Test
	public void testQueryWithRowKey_back() 
			throws ConnectionException, InterruptedException, ExecutionException {
		_acm.queryDB_asynchronous("6",false);
		_acm.queryWithRowkey("2",false);
	}
	
	@Test
	public void testQueryAllRowsOneCF_back() {
		Rows<String, String> rows = _acm.queryAllRowsOneCF(false);
		for (Row<String, String> row : rows) {
		    System.out.println("ROW: " + row.getKey() + " " + row.getColumns().size());
		}
		// can not show the row inserted by _acm.insertDataToDB_asynchronous(...)
	}
	
//	@Test
//	public void testAddOneCF_DYNA() throws CharacterCodingException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, TException, ConnectionException, InterruptedException, ExecutionException {
//		_acm.addDynaColumnFamily("movie");
//		_acm.insertDataToDB("3", "Bob", "Person","3","time","text","6","Bob...", true);
//		_acm.queryWithRowkey("3",true);
//	}
//	
//	@Test
//	public void testAddNewCF_DYNA() throws CharacterCodingException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, TException, ConnectionException, InterruptedException, ExecutionException {
//		_acm.addDynaColumnFamily("music");
//		_acm.insertDataToDB("6", "Ann", "Person", "1","time","text", "2", "info...", true);
//		_acm.queryWithRowkey("6",true);
//	}
}

package com.feiyu.database;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.junit.Test;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

public class TestAstyanaxCassandraManipulator {
	@Test
	public void testWholeProcess() throws NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, URISyntaxException, IOException, TException, ConnectionException, InterruptedException, ExecutionException {
		//		PropertyConfigurator.configure(AstyanaxCassandraManipulator.class.getClassLoader().getResource("log4j.properties"));
		AstyanaxCassandraManipulator acm = new AstyanaxCassandraManipulator("wcrCluster","wcrkeyspace","tweets","wcrPool","localhost",9160);
		acm.initialSetup();
		acm.cliSchema();
		acm.insertDataToDB(2, "an", "Person");
		acm.queryDB(2);
	}
}

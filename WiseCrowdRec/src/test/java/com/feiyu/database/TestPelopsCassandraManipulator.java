package com.feiyu.database;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.junit.Test;

public class TestPelopsCassandraManipulator {
	@Test
	public void testWholeProcess() 
			throws NotFoundException, InvalidRequestException, NoSuchFieldException, 
			UnavailableException, IllegalAccessException, InstantiationException, 
			URISyntaxException, IOException, TException {
		//		PropertyConfigurator.configure(AstyanaxCassandraManipulator.class.getClassLoader().getResource("log4j.properties"));
		PelopsCassandraManipulator cm = new PelopsCassandraManipulator("pool","wcrkeyspace","tweets","localhost",9160);
		cm.initialSchema();
		cm.addToPool();
		cm.insertDataToDB("tw","ann","person");
		cm.queryDB("tw");
		cm.shutdownPool();
	}
}

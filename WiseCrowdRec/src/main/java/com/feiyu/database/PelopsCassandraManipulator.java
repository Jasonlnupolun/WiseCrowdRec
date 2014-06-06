/**
 * From https://github.com/s7/scale7-pelops
 * modified by feiyu
 */
package com.feiyu.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.cassandra.cli.CliMain;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.scale7.cassandra.pelops.Cluster;
import org.scale7.cassandra.pelops.Mutator;
import org.scale7.cassandra.pelops.Pelops;
import org.scale7.cassandra.pelops.Selector;

public class PelopsCassandraManipulator {
	private static Logger LOG = Logger.getLogger(PelopsCassandraManipulator.class.getName());
	private final String _pool;
	private final String _keyspace;
	private final String _colFamily;
	private final Cluster _cluster;
	private final String _host;
	private final Integer _port;
	private final String _sqlFilePath = "cassandra/schemaCassandra.txt";

	public PelopsCassandraManipulator(String pool, String keyspace, 
			String colFamily, String host, Integer port) {
		_pool = pool;
		_keyspace = keyspace;
		_colFamily = colFamily;
		_host = host;
		_port = port;
		_cluster = new Cluster(_host, _port);
	}

	public void initialSchema() throws URISyntaxException, IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, TException, ClassNotFoundException, TimedOutException {
		CliMain.connect(_host, _port);
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(_sqlFilePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			CliMain.processStatement(line);
		}
		br.close();
		CliMain.disconnect();
	}

	public void addToPool() {
		/**
		 * from https://github.com/s7/scale7-pelops
		 * 1) static Pelops methods -> more concise: Pelops.addPool(pool, cluster, keyspace);
		 * 2) non-static methods -> IThriftPool pool = new CommonsBackedPool(cluster, keyspace);
		 */
		Pelops.addPool(_pool, _cluster, _keyspace);
	}

	public void shutdownPool() {
		Pelops.shutdown();
	}

	public void insertDataToDB(String rowKey, String entity, String category) {
		Mutator mutator = Pelops.createMutator(_pool);
		mutator.writeColumns(
				_colFamily, rowKey,
				mutator.newColumnList(
						mutator.newColumn("entity", entity),
						mutator.newColumn("category", category)
						)
				);
		mutator.execute(ConsistencyLevel.ONE);
	}

	public void queryDB(String rowKey) {
		Selector selector = Pelops.createSelector(_pool);
		List<Column> columns = selector.getColumnsFromRow(_colFamily, rowKey, false, ConsistencyLevel.ONE);

		LOG.info("Entity: " + Selector.getColumnStringValue(columns, "entity"));
		LOG.info("Category: " + Selector.getColumnStringValue(columns, "category"));
	}
}

/**
 * From https://github.com/s7/scale7-pelops
 * modified by feiyu
 */
package com.feiyu.database;

import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.scale7.cassandra.pelops.Cluster;
import org.scale7.cassandra.pelops.Mutator;
import org.scale7.cassandra.pelops.Pelops;
import org.scale7.cassandra.pelops.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraManipulator {
	private static final Logger _logger = LoggerFactory.getLogger(CassandraManipulator.class);
	private static String _pool = null;
	private static String _keyspace = null;
	private static String _colFamily = null;
	private static Cluster _cluster ;
	
	public CassandraManipulator(String pool, String keyspace, String colFamily, String host) {
		_pool = pool;
		_keyspace = keyspace;
		_colFamily = colFamily;
		_cluster = new Cluster(host, 9160);
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

		_logger.info("Entity: " + Selector.getColumnStringValue(columns, "entity"));
		_logger.info("Category: " + Selector.getColumnStringValue(columns, "category"));
	}

//	public static void main(String[] argv) {
//		CassandraManipulator cm = new CassandraManipulator("pool","wcrkeyspace","tweets","localhost");
//		cm.addToPool();
//		cm.insertDataToDB("tw","ann","person");
//		cm.queryDB("tw");
//		cm.shutdownPool();
//	}
}

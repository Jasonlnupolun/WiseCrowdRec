/**
 * @author feiyu
 * reference: https://github.com/Netflix/astyanax/wiki/Getting-Started
 */
package com.feiyu.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.apache.cassandra.cli.CliMain;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

public class AstyanaxCassandraManipulator {
	private static final Logger _logger = LoggerFactory.getLogger(AstyanaxCassandraManipulator.class);
	private final String _cluster;
	private final String _pool;
	private final String _host;
	private final Integer _port;
	private AstyanaxContext<Keyspace> context;
	private static Keyspace _keyspace;
	private final String _keyspaceName;
	private ColumnFamily<Integer, String> _columnFamily;
	private static String _columnFamilyName;
	private final String _sqlFilePath = "cassandra/schemaCassandra.txt";

	public AstyanaxCassandraManipulator(String cluster, String keyspaceName, 
			String columnFamilyName, String pool, String host, Integer port) {
		_cluster = cluster;
		_keyspaceName = keyspaceName;
		_columnFamilyName = columnFamilyName;
		_pool = pool;
		_host = host;
		_port = port;
	}

	public void initialSetup() {
		context = new AstyanaxContext.Builder()
		.forCluster(_cluster) //"Test Cluster"
		.forKeyspace(_keyspaceName)
		.withAstyanaxConfiguration(new AstyanaxConfigurationImpl().setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE))
		.withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl(_pool).setPort(_port).setMaxConnsPerHost(1).setSeeds(_host+":"+_port))
		.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
		.buildKeyspace(ThriftFamilyFactory.getInstance());

		context.start();
		_keyspace = context.getClient();
		_columnFamily = ColumnFamily.newColumnFamily(_columnFamilyName,
				IntegerSerializer.get(),  // Key Serializer
				StringSerializer.get());  // Column Serializer
	}

	public void cliSchema() throws URISyntaxException, IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, TException {
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


	public void insertDataToDB(int rowKey, String entity, String category) throws ConnectionException, InterruptedException, ExecutionException {
		MutationBatch mb = _keyspace.prepareMutationBatch();//The mutator is not thread safe
		mb.withRow(_columnFamily, rowKey)
		.putColumn("entity", entity)
		.putColumn("category", category);

		// no asynchronous feature
		@SuppressWarnings("unused")
		OperationResult<Void> result = mb.execute();

		// asynchronous feature
		//		Future<OperationResult<Void>> future = mb.executeAsync();
		//		_result = future.get();
	}

	public void queryDB(Integer rowKey) throws ConnectionException, InterruptedException, ExecutionException {
		ColumnList<String> columns = _keyspace.prepareQuery(_columnFamily)
				.getKey(rowKey)
				.execute().getResult();
		for (Column<String> column : columns) {
			_logger.info(column.getName()+":"+column.getValue(StringSerializer.get()));
		}
	}
}

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

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
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
	private static final Logger LOG = LoggerFactory.getLogger(AstyanaxCassandraManipulator.class);
	private static final String SQL_FILE_PATH = "cassandra/schemaCassandra.txt";
	private final String _cluster;
	private final String _keyspaceName;
	private final String _columnFamilyName;
	private final String _pool;
	private final String _host;
	private final Integer _port;
	private AstyanaxContext<Keyspace> context;
	private static Keyspace KS_AST;
	private static ColumnFamily<Integer, String> CF_AST;

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
		KS_AST = context.getClient();
		CF_AST = ColumnFamily.newColumnFamily(_columnFamilyName,
				IntegerSerializer.get(),  // Key Serializer
				StringSerializer.get());  // Column Serializer
	}

	public void cliSchema() throws URISyntaxException, IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, TException {
		CliMain.connect(_host, _port);
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(SQL_FILE_PATH);
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			CliMain.processStatement(line);
		}
		br.close();
		CliMain.disconnect();
	}

	public void insertDataToDB(int rowKey, String entity, String category) throws ConnectionException, InterruptedException, ExecutionException {
		MutationBatch mb = KS_AST.prepareMutationBatch();//The mutator is not thread safe
		mb.withRow(CF_AST, rowKey)
		.putColumn("entity", entity)
		.putColumn("category", category);

		// no asynchronous feature
		@SuppressWarnings("unused")
		OperationResult<Void> result = mb.execute();
	}


	public void insertDataToDB_asynchronous(int rowKey, String entity, String category) throws ConnectionException, InterruptedException, ExecutionException {
		MutationBatch mb = KS_AST.prepareMutationBatch();//The mutator is not thread safe
		mb.withRow(CF_AST, rowKey)
		.putColumn("entity", entity)
		.putColumn("category", category);

		// asynchronous feature
		ListenableFuture<OperationResult<Void>> future = mb.executeAsync();
		@SuppressWarnings("unused")
		OperationResult<Void> result = future.get();
	}

	public void queryDB(Integer rowKey) throws ConnectionException, InterruptedException, ExecutionException {
		// no asynchronous feature
		ColumnList<String> columns = KS_AST.prepareQuery(CF_AST)
				.getKey(rowKey)
				.execute()
				.getResult();
		String logger = "";
		for (Column<String> column : columns) {
			logger += "<"+column.getName()+":"+column.getValue(StringSerializer.get()) +">";
		}
		LOG.info(logger);
	}

	public void queryDB_asynchronous(Integer rowKey) throws ConnectionException, InterruptedException, ExecutionException {
		// asynchronous feature
		final ListenableFuture<OperationResult<ColumnList<String>>> listenableFuture = KS_AST
				.prepareQuery(CF_AST)
				.getKey(rowKey)
				.executeAsync();
		listenableFuture.addListener( new Runnable() {
			@Override
			public void run() {
				if (listenableFuture.isCancelled() || !listenableFuture.isDone()) {
					LOG.info("listenableFuture is cancelled or is not ");
					return;
				}
				OperationResult<ColumnList<String>> result = null;

				try {
					result = listenableFuture.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
					LOG.error("Can not retrieve the result.");
				}

				ColumnList<String> columns = result.getResult();
				String logger = "";
				for (Column<String> column : columns) {
					logger += "<"+column.getName()+":"+column.getValue(StringSerializer.get()) +">";
				}
				LOG.info(logger+"-->listenableFuture is successful");
			}
		}, MoreExecutors.sameThreadExecutor());
	}
}

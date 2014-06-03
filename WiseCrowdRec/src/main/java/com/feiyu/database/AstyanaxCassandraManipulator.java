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
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.ExceptionCallback;
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
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import com.netflix.astyanax.util.RangeBuilder;

public class AstyanaxCassandraManipulator {
	private static Logger LOG = Logger.getLogger(AstyanaxCassandraManipulator.class.getName());
	private static final String SQL_FILE_PATH = "cassandra/schemaCassandra.txt";
	private final String _cluster;
	private final String _keyspaceName;
	private final String _columnFamilyNameBack = "backgroundsearch";
	private String _columnFamilyNameDyna;
	private final String _pool;
	private final String _host;
	private final Integer _port;
	private AstyanaxContext<Keyspace> context;
	private static Keyspace KS_AST;
	private static ColumnFamily<String, String> CF_AST_BACK;
	private static ColumnFamily<String, String> CF_AST_DYNA;

	public AstyanaxCassandraManipulator(String cluster, String keyspaceName, 
			 String pool, String host, Integer port) {
		_cluster = cluster;
		_keyspaceName = keyspaceName;
		_pool = pool;
		_host = host;
		_port = port;
	}
	
	/*
	 * Run initialSetup() only once
	 */
	public void initialSetup() throws NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, ClassNotFoundException, TimedOutException, URISyntaxException, IOException, TException {
		context = new AstyanaxContext.Builder()
		.forCluster(_cluster) //"Test Cluster"
		.forKeyspace(_keyspaceName)
		.withAstyanaxConfiguration(new AstyanaxConfigurationImpl().setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE))
		.withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl(_pool).setPort(_port).setMaxConnsPerHost(1).setSeeds(_host+":"+_port))
		.withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
		.buildKeyspace(ThriftFamilyFactory.getInstance());

		context.start();
		KS_AST = context.getClient();
		
		CF_AST_BACK = ColumnFamily
				.newColumnFamily(_columnFamilyNameBack,
						StringSerializer.get(),  // Key Serializer
						StringSerializer.get()) ;  // Column Serializer 
		
		CF_AST_DYNA = ColumnFamily
				.newColumnFamily(_columnFamilyNameDyna,
						StringSerializer.get(),  // Key Serializer
						StringSerializer.get()) ;  // Column Serializer
		
		this.cliSchema();
	}
	
	private void cliSchema() throws URISyntaxException, IOException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, TException, ClassNotFoundException, TimedOutException {
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
	
//	public void addDynaColumnFamily(String newCF) throws CharacterCodingException, NotFoundException, InvalidRequestException, NoSuchFieldException, UnavailableException, IllegalAccessException, InstantiationException, TException {
//		_columnFamilyNameDyna = newCF;
//		CF_AST_DYNA = ColumnFamily
//				.newColumnFamily(_columnFamilyNameDyna,
//						StringSerializer.get(),  // Key Serializer
//						StringSerializer.get()) ;  // Column Serializer
//		
//		CliMain.connect(_host, _port);
//		CliMain.processStatement("use wcrkeyspace;");
//		CliMain.processStatement("create column family "+_columnFamilyNameDyna+" with"
//				+ " column_type = 'Standard' and comparator = 'UTF8Type' and default_validation_class "
//				+ "= 'UTF8Type' and key_validation_class = 'UTF8Type'; ");		
//		CliMain.disconnect();
//	}

	public void insertDataToDB(String rowKey, String entity, String category, 
			 String sentiment, String time, String text, String count, String entityInfo, boolean isDynamicSearch) throws ConnectionException, InterruptedException, ExecutionException {

		MutationBatch mb = KS_AST.prepareMutationBatch();//The mutator is not thread safe
		ColumnFamily<String, String> CF_AST;
		if (isDynamicSearch) {
			CF_AST = CF_AST_DYNA;
		} else {
			CF_AST = CF_AST_BACK;
		}
		
		mb.withRow(CF_AST, rowKey)
		.putColumn("entity", entity)
		.putColumn("category", category)
		.putColumn("sentiment", sentiment)
		.putColumn("time", time)
		.putColumn("text", text)
		.putColumn("count", count)
		.putColumn("entityInfo", entityInfo);

		// no asynchronous feature
		@SuppressWarnings("unused")
		OperationResult<Void> result = mb.execute();
	}

	public void insertDataToDB_asynchronous(String rowKey, String entity, String category, 
			 String sentiment, String time, String text, String count, String entityInfo, boolean isDynamicSearch) 
					 throws ConnectionException, InterruptedException, ExecutionException {

		MutationBatch mb = KS_AST.prepareMutationBatch();//The mutator is not thread safe
		ColumnFamily<String, String> CF_AST;
		if (isDynamicSearch) {
			CF_AST = CF_AST_DYNA;
		} else {
			CF_AST = CF_AST_BACK;
		}
		
		mb.withRow(CF_AST, rowKey)
		.putColumn("entity", entity)
		.putColumn("category", category)
		.putColumn("sentiment", sentiment)
		.putColumn("time", time)
		.putColumn("text", text)
		.putColumn("count", count)
		.putColumn("entityInfo", entityInfo);

		// asynchronous feature
		ListenableFuture<OperationResult<Void>> future = mb.executeAsync();
		@SuppressWarnings("unused")
		OperationResult<Void> result = future.get();
	}
	
	public void insertMovieDataToDB_asynchronous(String rowKey, String movieName, String hybridRating, String count) 
			throws ConnectionException, InterruptedException, ExecutionException {

		MutationBatch mb = KS_AST.prepareMutationBatch();//The mutator is not thread safe
		ColumnFamily<String, String> CF_AST = CF_AST_BACK;

		mb.withRow(CF_AST, rowKey)
		.putColumn("movieName", movieName)
		.putColumn("hybridRating", hybridRating)
		.putColumn("count", count);

		// asynchronous feature
		ListenableFuture<OperationResult<Void>> future = mb.executeAsync();
		@SuppressWarnings("unused")
		OperationResult<Void> result = future.get();
	}
	
	public String queryWithRowKeyGetRating(String rowKey) throws ConnectionException {
		String rating = null;
		ColumnFamily<String, String> CF_AST = CF_AST_BACK;
		ColumnList<String> columns = KS_AST.prepareQuery(CF_AST)
				.getKey(rowKey)
				.execute()
				.getResult();
		for (Column<String> column : columns) {
			if (column.getName().equals("hybridRating")) {
				return column.getValue(StringSerializer.get());
			}
		}
		return rating;
	}

	public void queryWithRowkey(String rowKey, boolean isDynamicSearch) throws ConnectionException, InterruptedException, ExecutionException {
		ColumnFamily<String, String> CF_AST;
		if (isDynamicSearch) {
			CF_AST = CF_AST_DYNA;
		} else {
			CF_AST = CF_AST_BACK;
		}

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

	public void queryDB_asynchronous(String rowKey, boolean isDynamicSearch) throws ConnectionException, InterruptedException, ExecutionException {
		ColumnFamily<String, String> CF_AST;
		if (isDynamicSearch) {
			CF_AST = CF_AST_DYNA;
		} else {
			CF_AST = CF_AST_BACK;
		}
		
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

	public Rows<String, String> queryAllRowsOneCF(boolean isDynamicSearch) {
		ColumnFamily<String, String> CF_AST;
		if (isDynamicSearch) {
			CF_AST = CF_AST_DYNA;
		} else {
			CF_AST = CF_AST_BACK;
		}
		
		/*
		 * reference: https://github.com/Netflix/astyanax/wiki/Reading-Data
		 */
		Rows<String, String> rows = null;
		try {
			rows = KS_AST.prepareQuery(CF_AST)
					.getAllRows()
					.withColumnRange(new RangeBuilder().build())
					.setExceptionCallback(new ExceptionCallback() {
						@Override
						public boolean onException(ConnectionException e) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e1) {
							}
							return true;
						}})
						.execute().getResult();
		} catch (ConnectionException e) {
		}
		return rows;
	}
}

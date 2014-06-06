package com.feiyu.elasticsearch;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.feiyu.springmvc.model.Entity;
import com.feiyu.utils.ElasticsearchInit;

import io.searchbox.client.JestResult;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.Flush;
import io.searchbox.indices.IndicesExists;


public class JestElasticsearchManipulator {
	private static Logger LOG = Logger.getLogger(JestElasticsearchManipulator.class.getName());
	private static JestHttpClient jestHttpClient = ElasticsearchInit.getClient();
	private static String _esIndexName; 
	private static String _esTypeName; 

	public JestElasticsearchManipulator(String idxName, String typeName) {
		_esIndexName = idxName;
		_esTypeName = typeName;
	}
	
	public void builderIndex_OneRecord(String json, boolean cleanBeforeInsert) {
		long start = System.currentTimeMillis();
		try {
			if (cleanBeforeInsert) {
				jestHttpClient.execute(new DeleteIndex(new DeleteIndex.Builder(_esIndexName)));
			}
			JestResult jestResult = jestHttpClient.execute(new IndicesExists.Builder(_esIndexName).build());
			if (!jestResult.isSucceeded()) {
				jestHttpClient.execute(new CreateIndex.Builder(_esIndexName).build());
			}

			Index index = new Index.Builder(json)
			.index(_esIndexName)
			.type(_esTypeName)
			.build();
			jestHttpClient.execute(index);
			//            jestHttpClient.shutdownClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		LOG.info("->> One Record(default id): time for create index --> " + (end - start) + " milliseconds"); 
	}

	public void builderIndex_OneRecord(String json, String esID, boolean cleanBeforeInsert) {
		long start = System.currentTimeMillis();
		try {
			if (cleanBeforeInsert) {
				jestHttpClient.execute(new DeleteIndex(new DeleteIndex.Builder(_esIndexName)));
			}
			JestResult jestResult = jestHttpClient.execute(new IndicesExists.Builder(_esIndexName).build());
			if (!jestResult.isSucceeded()) {
				jestHttpClient.execute(new CreateIndex.Builder(_esIndexName).build());
			}

			Index index = new Index.Builder(json)
			.index(_esIndexName)
			.type(_esTypeName)
			.id(esID)
			.build();
			jestHttpClient.execute(index);
			//            jestHttpClient.shutdownClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		LOG.info("->> One Record: time for create index --> " + (end - start) + " milliseconds"); 
	}

	public void builderIndex_Bulk(List<Entity> entityList, boolean cleanBeforeInsert) {
		int nRecords = entityList.size();
		long start = System.currentTimeMillis();
		try {
			if (cleanBeforeInsert) {
				jestHttpClient.execute(new DeleteIndex(new DeleteIndex.Builder(_esIndexName)));
			}
			JestResult jestResult = jestHttpClient.execute(new IndicesExists.Builder(_esIndexName).build());
			if (!jestResult.isSucceeded()) {
				jestHttpClient.execute(new CreateIndex.Builder(_esIndexName).build());
			}

			
			SerializeBeans2JSON sb2json = new SerializeBeans2JSON(); 
			Bulk.Builder bulkBuilder = new Bulk.Builder();
			for (int i = 0; i < nRecords; i++) {
				Index index = new Index
						.Builder(sb2json.serializeBeans2JSON(entityList.get(i)))
				.index(_esIndexName)
				.type(_esTypeName)
				.id(entityList.get(i).getEntityID())
				.build();
				bulkBuilder.addAction(index);
			}
			jestHttpClient.execute(bulkBuilder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		LOG.info("->> Bulk: total time for create index --> " + (end - start) + " milliseconds, # of records: " + nRecords); 
	}

	public String getJsonById(String id) throws IOException {
		Flush flush = new Flush.Builder().build();
		jestHttpClient.execute(flush);

		long start = System.currentTimeMillis();
		Get get = new Get.Builder(_esIndexName, id).build();
		JestResult result = null;
		try {
			result = jestHttpClient.execute(get);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		LOG.info("->> GetJsonByID: search time --> " + (end - start) + " milliseconds");
		return result.getJsonString();
	}

	public void searchsByKeyword(String keyword) throws IOException {
		// important: flush es before search, since some data might be stored in memory 
		Flush flush = new Flush.Builder().build();
		jestHttpClient.execute(flush);

		long start = System.currentTimeMillis();

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.queryString(keyword));
		LOG.info("searchSourceBuilder.toString():"+searchSourceBuilder.toString());

		Search search = new Search.Builder(searchSourceBuilder.toString())
		.addIndex(_esIndexName)
		.addType(_esTypeName)
		.build();

		JestResult result = null;
		try {
			result = jestHttpClient.execute(search);
			LOG.info("----->"+result.getJsonString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		LOG.info("->> SearchByKeyword: search time --> " + (end - start) + " milliseconds");
	}
}

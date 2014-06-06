/**
 * reference http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/index.html
 * 
 */
package com.feiyu.elasticsearch;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;

import java.io.IOException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class JavaApiElasticsearchManipulator {
	private static Settings settings;
	private static Client client;
	private static Node node;
	private static String _indexName;
	private static String _typeName;

	public JavaApiElasticsearchManipulator(String indexName, String typeName) {
		_indexName = indexName;
		_typeName = typeName;
	} 

	public void initSetting() throws IOException {
		settings = settingsBuilder()
				.put("index.store.type", "memory")
				.put("gateway.type", "none")
				.put("path.data", "src/main/resources/esPath")
				// .put("discovery.zen.ping.unicast.hosts", "host1:9300,host2:9300")
				.build();

		node = NodeBuilder.nodeBuilder()
				.clusterName("wcrESCluster")
				.settings(settings)
				.local(true) // not join existing cluster on the network.
				.node();

		client = node.client();
	}

	public IndexResponse indexJson(String indexName, String typeName, String json) {
		// index -> database
		// type -> table
		IndexResponse response = client.prepareIndex(indexName, typeName)
				.setSource(json)
				.execute()
				.actionGet();

		//		String indexName = response.getIndex();
		//		String typeName = response.getType();
		//		String docId = response.getId();
		//		long version = response.getVersion(); // will get 1 if this is the first time you index this document
		return response;
	}

	public IndexResponse indexJsonSetId(String indexName, String typeName, String json, String id) {
		// e.g. percolator id = "1"
		IndexResponse response = client.prepareIndex(indexName, typeName, id)
				.setSource(json)
				.execute()
				.actionGet();

		//		List<String> matches = response.matches();
		return response;
	}

	public GetResponse getARecord(String indexName, String typeName, String id) {
		return client.prepareGet(indexName, typeName, id)
				.execute()
				.actionGet();
	}

	public DeleteResponse deleteAJsonFile(String indexName, String typeName, String id) {
		return client.prepareDelete(indexName, typeName, id)
				.execute()
				.actionGet();
	}
	public DeleteResponse deleteAJsonFile(String id) {
		return client.prepareDelete(_indexName, _typeName, id)
				.execute()
				.actionGet();
	}
}

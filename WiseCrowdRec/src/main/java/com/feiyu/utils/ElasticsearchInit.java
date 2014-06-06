package com.feiyu.utils;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;

public class ElasticsearchInit {
	private static JestHttpClient client;

	private ElasticsearchInit() {

	}

	public synchronized static JestHttpClient getClient() {
		if (client == null) {
			JestClientFactory factory = new JestClientFactory();
			factory.setHttpClientConfig(new HttpClientConfig
					.Builder("http://localhost:9200")
					.multiThreaded(true)
					.build());
			client = (JestHttpClient) factory.getObject();
		}
		return client;
	}
}
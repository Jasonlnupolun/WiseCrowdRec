package com.feiyu.springmvc.Dao;

import org.junit.Test;

import com.feiyu.util.ElasticsearchInit;

import io.searchbox.client.http.JestHttpClient;

public class TestElasticsearchInit {
	@Test
	public void testESInit() {
		JestHttpClient client = ElasticsearchInit.getClient();
		System.out.println("->> "+client.getServers());
		System.out.println("->> "+client.getAsyncClient());
		client.shutdownClient();
	}
}

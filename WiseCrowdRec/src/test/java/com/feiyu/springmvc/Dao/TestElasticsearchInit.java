package com.feiyu.springmvc.Dao;
/**
 * @author feiyu
 */

import org.junit.Test;

import com.feiyu.utils.ElasticsearchInit;

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

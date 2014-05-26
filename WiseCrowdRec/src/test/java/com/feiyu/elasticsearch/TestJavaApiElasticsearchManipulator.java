package com.feiyu.elasticsearch;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.junit.Before;
import org.junit.Test;

import com.feiyu.springmvc.model.EntityInfo;

public class TestJavaApiElasticsearchManipulator {
	JavaApiElasticsearchManipulator esm;
	String _idxName = "wcrindex";
	String _typeName = "wcrtype";
	SerializeBeans2JSON serBean2Json;
	
	@Before
	public void init() throws IOException {
		esm = new JavaApiElasticsearchManipulator(_idxName,_typeName);
		esm.initSetting();
		serBean2Json = new SerializeBeans2JSON();
	}
	
	@Test
	public void testIndexAJsonFile() throws JsonGenerationException, JsonMappingException, IOException {
		EntityInfo entityInfo = new EntityInfo("Ann", "People", 1, "alert alert-success", "time","text");
		String json = serBean2Json.serializeBeans2JSON_EntityInfo(entityInfo);
		IndexResponse idxResponse = esm.indexJson(_idxName, _typeName, json);

		String indexName = idxResponse.getIndex();
		String typeName = idxResponse.getType();
		String docId = idxResponse.getId();
		long version = idxResponse.getVersion(); // will get 1 if this is the first time you index this document

		assertEquals(indexName, _idxName); // index name must be in lower case
		assertEquals(typeName, _typeName);
		System.out.println("--> docId: " + docId);
		assertEquals(version, 1);
	}
	
	@Test
	public void testIndexAJsonFileWithDocId() throws JsonGenerationException, JsonMappingException, IOException {
		EntityInfo entityInfo = new EntityInfo("Bob", "People", 3, "alert alert-success", "time","text");
		String json = serBean2Json.serializeBeans2JSON_EntityInfo(entityInfo);
		IndexResponse idxResponse = esm.indexJsonSetId(_idxName, _typeName,json, "6");

		String docId = idxResponse.getId();
		long version = idxResponse.getVersion(); // will get 1 if this is the first time you index this document

		System.out.println("--> docId (assigned before): " + docId);
		assertEquals(version, 1);
	}
}

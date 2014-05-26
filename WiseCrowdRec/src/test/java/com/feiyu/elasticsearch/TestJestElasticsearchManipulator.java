package com.feiyu.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.feiyu.springmvc.model.Entity;
import com.feiyu.springmvc.model.EntityInfo;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJestElasticsearchManipulator {
	JestElasticsearchManipulator _jesm = new JestElasticsearchManipulator("wcresidx","dynamicsearchestype");
	SerializeBeans2JSON sb2json = new SerializeBeans2JSON(); // ElasticSearch requires index data as JSON.
 	
	@Test
	public void a_TestbuilderIndex_OneRecord() throws JsonGenerationException, JsonMappingException, IOException {
//		JestElasticsearchManipulator _jesm = new JestElasticsearchManipulator("wcresidx","dynamicsearchestype");
		EntityInfo entityInfo = new EntityInfo("SF","city",3,"css","time","text the movie");
		Entity entity = new Entity("102", 6, entityInfo);
		_jesm.builderIndex_OneRecord(new String(sb2json.serializeBeans2JSON_Entity(entity)), entity.getEntityID(), true);
	}
	
	@Test
	public void b_TestbuilderIndex_Bulk() throws JsonGenerationException, JsonMappingException, IOException {
//		JestElasticsearchManipulator _jesm = new JestElasticsearchManipulator("esidx","dynamicsearch");
		List<Entity> entityList = new ArrayList<Entity>();
		
		entityList.add(
				new Entity("100", 10, 
						new EntityInfo("ann","people",2,"css1","time1","text1 movie")));
		entityList.add(
				new Entity("101", 16, 
						new EntityInfo("bob","people",3,"css2","time2","teeeeeeeeeext2 the")));
		_jesm.builderIndex_Bulk(entityList, false);
	}
	
	@Test
	public void c_TestQueryGetJson() throws IOException {
		System.out.println("==> thirdTestQueryGetJson: "+_jesm.getJsonById("102"));
	}
	
	@Test
	public void d_TestSearchByKeywords() throws IOException {
		_jesm.searchsByKeyword("city");
	}
}

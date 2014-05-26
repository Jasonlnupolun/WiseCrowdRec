package com.feiyu.elasticsearch;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.feiyu.springmvc.model.Entity;
import com.feiyu.springmvc.model.EntityInfo;

public class SerializeBeans2JSON {
	/*
	 * there are four ways of generating JSON files
	 * http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/index_.html
	 */
	public String serializeBeans2JSON_EntityInfo(EntityInfo entityInfo) 
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(entityInfo);
		return json;
	}
	
	public String serializeBeans2JSON_Entity(Entity entity) 
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(entity);
		return json;
	}
}

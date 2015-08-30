package com.feiyu.utils;
/**
 * @author feiyu
 */

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class SerializeBeans2JSON implements java.io.Serializable {
  private static final long serialVersionUID = -6687319108174378090L;

  /*
   * there are four ways of generating JSON files
   * http://www.elasticsearch.org/guide/en/elasticsearch/client/java-api/current/index_.html
   */
  public String serializeBeans2JSON(Object object) 
      throws JsonGenerationException, JsonMappingException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(object);
    return json;
  }
}

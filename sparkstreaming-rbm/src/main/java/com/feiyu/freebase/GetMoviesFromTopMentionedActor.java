package com.feiyu.freebase;
/**
 * @author Fei Yu (@faustineinsun)
 */

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.feiyu.utils.GlobalVariables;
import com.jayway.jsonpath.JsonPath;

public class GetMoviesFromTopMentionedActor {
  private JSONParser parser = new JSONParser();

  public void getMoviesForRec(String json) throws ParseException, IOException {
    JSONParser jsonPrsr = new JSONParser();
    JSONArray jsonArray  = (JSONArray)jsonPrsr.parse(json);
    if (jsonArray != null) { 
      int len = jsonArray.size();
      for (int i = 0; i < len; i++) {
        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
        String name = (String) jsonObject.get("MovieStarName");

        String movieListFromActorNameValue;
        JSONArray results = new JSONArray();
        try {
          movieListFromActorNameValue = GlobalVariables.FREEBASE_OPS.getMovieListByActorName(name, false);
          JSONObject response = (JSONObject)parser.parse(movieListFromActorNameValue);
          results = (JSONArray)response.get("result");
        } catch (IOException | ParseException e) {
          System.out.println("############## FREEBASE_OPS IOException | ParseException");
        } 

        if (results.size() > 0 ) {
          for (Object result : results) {
            String movieName = JsonPath.read(result,"$.name").toString();
            GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_REC, null, movieName.getBytes());
          }
          break;
        }
      }
    } 

  }
}

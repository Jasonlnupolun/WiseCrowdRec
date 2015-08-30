package com.feiyu.freebase;
/**
 * reference: https://developers.google.com/freebase/v1/mql-overview
 * @author Fei Yu (@faustineinsun)
 * 
 * freebase query tool https://www.freebase.com/query
 */

import java.io.IOException;

import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import com.omertron.themoviedbapi.MovieDbException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FreebaseOperations{
  private static Logger log = Logger.getLogger(FreebaseOperations.class.getName());
  HttpTransport httpTransport = new NetHttpTransport();
  HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
  JSONParser parser = new JSONParser();

  public String getMovieListByActorName(String actorName, boolean printResult) throws IOException, ParseException {
    log.debug("-----Actor Name: "+actorName+"------getMovieListByActorName");
    String query = "[{\"starring\": [{\"actor\": \""
        + actorName 
        +"\"}],\"type\": \"/film/film\",\"name\": null,\"mid\": null}]";
    //String query = "[{\"id\":null,\"name\":null,\"type\":\"/astronomy/planet\"}]";
    GlobalVariables.FREEBASE_URL.put("query", query);
    HttpRequest request = requestFactory.buildGetRequest(GlobalVariables.FREEBASE_URL);
    HttpResponse httpResponse = request.execute();

    String returnMsg = httpResponse.parseAsString();

    if (printResult) {
      JSONObject response = (JSONObject)parser.parse(returnMsg);
      JSONArray results = (JSONArray)response.get("result");
      for (Object result : results) {
        log.debug(JsonPath.read(result,"$.name").toString());
        //log.debug(JsonPath.read(result,"$.mid").toString());
      }
    }

    return returnMsg;
  }

  public void getActorNamesByMovieName(String movieName) throws IOException, ParseException {
    log.debug("-----Movie Name: "+movieName+"------getActorNamesByMovieName");
    // https://code.google.com/p/json-path/
    String query = "[{\"type\":\"/film/film\",\"name\":\""
        + movieName
        + "\",\"mid\":null,\"starring\":[{\"actor\":null,\"mid\":null}]}]";
    GlobalVariables.FREEBASE_URL.put("query", query);
    HttpRequest request = requestFactory.buildGetRequest(GlobalVariables.FREEBASE_URL);
    HttpResponse httpResponse = request.execute();

    JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
    JSONArray results = (JSONArray)response.get("result");
    for (Object result : results) {
      //				log.debug(JsonPath.read(result,"$.starring[*].actor").toString());
      String[] actors= JsonPath.read(result,"$.starring[*].actor").toString()
          .replace("[\"", "")
          .replace("\"]", "")
          .split("\",\"");
      for (String actor : actors) {
        log.debug(actor);
      }
    }
  }

  public void getActorNamesByIMDbMovieID(String IMDbID) throws MovieDbException, IOException, ParseException {
    IMDbInfoQuery imdbIQ = new IMDbInfoQuery();
    String movieName = imdbIQ.getMoiveName(IMDbID);
    log.debug("-----Movie Name: "+movieName+"------getActorNamesByIMDbMovieID");
    this.getActorNamesByMovieName(movieName);
  }

  public String getFilmGenresByActorNMovieName(String actorName, String movieName, boolean printResult) throws IOException, ParseException {
    log.debug("-----Movie Genre of "+movieName+" "+actorName+"------getFilmGenreByActorNMovieName");

    String query = "[{\"type\":\"/film/film\",\"name\":\""
        +movieName
        +"\",\"genre\": [],\"starring\":[{\"actor\":\""
        +actorName
        +"\"}]}]";
    GlobalVariables.FREEBASE_URL.put("query", query);
    HttpRequest request = requestFactory.buildGetRequest(GlobalVariables.FREEBASE_URL);
    HttpResponse httpResponse = request.execute();

    String returnMsg = httpResponse.parseAsString();

    JSONObject response = (JSONObject)parser.parse(returnMsg);
    JSONArray results = (JSONArray)response.get("result");
    for (Object result : results) {
      //log.debug(JsonPath.read(result,"$.starring[*].actor").toString());
      String[] genres = JsonPath.read(result,"$.genre").toString()
          .replace("[\"", "")
          .replace("\"]", "")
          .split("\",\"");
      for (String genre : genres) {
        log.debug(genre);
      }
    }

    return returnMsg;
  }

  public String getFilmGenresByMovieId(String mid, boolean printResult) throws IOException, ParseException {
    log.debug("-----Movie Genre of " + mid + " ------getFilmGenreByActorNMovieName");
    String query = "[{ \"type\": \"/film/film\", \"mid\": \""
        + mid
        + "\", \"/film/film/genre\": [] }]"; 
    GlobalVariables.FREEBASE_URL.put("query", query);
    HttpRequest request = requestFactory.buildGetRequest(GlobalVariables.FREEBASE_URL);
    HttpResponse httpResponse = request.execute();

    String returnMsg = httpResponse.parseAsString();

    JSONObject response = (JSONObject)parser.parse(returnMsg);
    JSONArray results = (JSONArray)response.get("result");
    for (Object result : results) {
      String[] genres = JsonPath.read(result,"$./film/film/genre").toString()
          .replace("[\"", "")
          .replace("\"]", "")
          .split("\",\"");
      for (String genre : genres) {
        log.debug(genre);
      }
    }

    return returnMsg;
  }

  public static void main(String[] args) {
    try {
      InitializeWCR initWCR = new InitializeWCR();
      initWCR.getWiseCrowdRecConfigInfo();
      initWCR.themoviedbOrgInitial();
      initWCR.getFreebaseInfo();

      FreebaseOperations getAMGSubGraphVE = new FreebaseOperations();
      getAMGSubGraphVE.getMovieListByActorName("Kiefer Sutherland", true);
      getAMGSubGraphVE.getActorNamesByMovieName("Stand by Me");
      getAMGSubGraphVE.getActorNamesByIMDbMovieID("tt0109830");
      getAMGSubGraphVE.getFilmGenresByActorNMovieName("Kiefer Sutherland", "Flatliners", true);
      getAMGSubGraphVE.getFilmGenresByMovieId("/m/0bdjd", true);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
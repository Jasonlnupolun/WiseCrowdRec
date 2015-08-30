package com.feiyu.freebase;
/**
 * @author Fei Yu (@faustineinsun)
 */

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.TimeOperations;
import com.jayway.jsonpath.JsonPath;

public class GenerateActorMoviesGenersGraph2Json {
  private static Logger log = Logger.getLogger(GenerateActorMoviesGenersGraph2Json.class.getName());
  private JSONObject d3Data = new JSONObject();
  private JSONParser parser = new JSONParser();
  private JSONArray d3Vertices = new JSONArray();
  private JSONObject d3Vertex;  
  private JSONArray d3Edges = new JSONArray();
  private JSONObject d3Edge;    
  private int actorMaxIdx = -1;
  private int movieMaxIdx = -1;
  private int genreMaxIdx = -1;
  private HashMap<String, Integer> actorIdxJson = new HashMap<String, Integer>();
  private HashMap<String, Integer> movieIdxJson = new HashMap<String, Integer>();
  private HashMap<String, Integer> genreIdxJson = new HashMap<String, Integer>();
  private String movieInfo2UIMsg;
  //private String freebaseReachLimitMsgRec = "Freebase API's daily quota has been reached limit, best time period to come each day is from 9:00 AM PST to 5:00 PM PST";
  private String freebaseReachLimitMsgMovie = "Freebase API reaches daily limit, best time period to come each day is from 9 AM to 5 PM PST, here shows the latest Actor-Movies-Genres Graph -> ";
  private TimeOperations timeOps = new TimeOperations();

  public void getD3ActorMoviesGenresInJson(String jsonTriple) throws ParseException, IOException {
    timeOps.chooseFreebaseKeyByTime();

    JSONParser parserJsonTriple = new JSONParser();
    JSONObject jsonTipleUCR = (JSONObject)parserJsonTriple.parse(jsonTriple);
    String personName = jsonTipleUCR.get("candidateactor").toString();
    String rating = jsonTipleUCR.get("rating").toString();

    if ((timeOps.is9To13PST() && GlobalVariables.FREEBASE_IS_REACH_LIMIT_9_13) 
        ||(timeOps.is13To17PST() && GlobalVariables.FREEBASE_IS_REACH_LIMIT_13_17)
        ||(timeOps.isOtherTime() && GlobalVariables.FREEBASE_IS_REACH_LIMIT_OTHERS)) {
      //GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_REC, null, freebaseReachLimitMsgRec.getBytes());
      if (GlobalVariables.RECENT_ACTOR_MOVIES_GENRES_GRAPH != null) {
        GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_MOVIE, null, freebaseReachLimitMsgMovie.getBytes());
        GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_SMCSUBGRAPH, null, GlobalVariables.RECENT_ACTOR_MOVIES_GENRES_GRAPH.getBytes());
      }
    } else {
      this.getD3VertexEdgeActorMovies(personName, rating);
    }

  }

  @SuppressWarnings("unchecked")
  public void getD3VertexEdgeActorMovies(String personName, String rating) throws IOException {
    /*
    String actorMoviesKey = "actormovies:"+personName;
    String movieListFromActorNameValue = GlobalVariables.JEDIS_API.get(actorMoviesKey) ;

    if (movieListFromActorNameValue == null) {
      movieListFromActorNameValue = GlobalVariables.FREEBASE_OPS.getMovieListByActorName(personName, false); 
      GlobalVariables.JEDIS_OPS.cacheAKeyInCertainTime(actorMoviesKey, movieListFromActorNameValue);
    } 
     */

    String movieListFromActorNameValue;
    JSONArray results = new JSONArray();
    try {
      movieListFromActorNameValue = GlobalVariables.FREEBASE_OPS.getMovieListByActorName(personName, false);
      JSONObject response = (JSONObject)parser.parse(movieListFromActorNameValue);
      results = (JSONArray)response.get("result");
    } catch (IOException | ParseException e) {
      timeOps.setIsReachLimitFlags();
      System.out.println("############## FREEBASE_OPS IOException | ParseException");
      //GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_REC, null, freebaseReachLimitMsgRec.getBytes());
    } 

    if (results.size() > 0 ) {

      if (!this.actorIdxJson.containsKey(personName)) {
        log.debug("Put actor " + personName +" into JSONArray d3Vertices..");

        d3Vertex = new JSONObject();  
        d3Vertex.put("name", personName.substring(
          0, personName.length()<3?personName.length():3));
        d3Vertex.put("fullname", personName);
        d3Vertex.put("entity", "actor");

        ++actorMaxIdx;

        d3Vertices.add(d3Vertex);
        //this.sendMessage(d3Vertex.toString());

        this.actorIdxJson.put(personName, actorMaxIdx);
      }

      movieMaxIdx = this.d3Vertices.size()-1; 
      for (Object result : results) {
        String movieName = JsonPath.read(result,"$.name").toString();

        movieInfo2UIMsg = "["+personName+"]'s movie ["+movieName+"] is rated as ["+rating+"]";
        //GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_REC, null, movieName.getBytes());
        GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_MOVIE, null, movieInfo2UIMsg.getBytes());

        int curMovieIdxInJson = -1;
        if (!this.movieIdxJson.containsKey(movieName)) {
          log.debug("Put movie " + movieName +" into JSONArray d3Vertices..");

          d3Vertex = new JSONObject();    
          d3Vertex.put("name", movieName.substring(
            0, movieName.length()<3?movieName.length():3));
          d3Vertex.put("fullname", movieName);
          d3Vertex.put("entity", "movie");

          curMovieIdxInJson = ++movieMaxIdx;
          d3Vertices.add(d3Vertex);
          //this.sendMessage(d3Vertex.toString());

          this.movieIdxJson.put(movieName, curMovieIdxInJson);
        } else {
          curMovieIdxInJson = this.movieIdxJson.get(movieName);
        }

        log.debug("Put actor-movie link into JSONArray d3Edges..");
        d3Edge= new JSONObject(); 
        d3Edge.put("source", actorMaxIdx);
        d3Edge.put("target", curMovieIdxInJson);
        d3Edge.put("type", "linkactormovie");
        d3Edges.add(d3Edge);
        //this.sendMessage(d3Edge.toString());

        this.getD3VertexEdgeMovieGenres(personName, movieName);
        movieMaxIdx = this.d3Vertices.size()-1;   
      }
      actorMaxIdx = this.d3Vertices.size()-1;

      log.debug(d3Vertices);
      log.debug(d3Edges);
      d3Data.put("nodes", d3Vertices);
      d3Data.put("links", d3Edges);
      log.debug(d3Data);

      String msg = d3Data.toString();
      //GlobalVariables.JEDIS_OPS.cacheLatestActorMoviesGenreGraphJson(msg);
      GlobalVariables.RECENT_ACTOR_MOVIES_GENRES_GRAPH = msg;
      GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_SMCSUBGRAPH, null, msg.getBytes());
      log.debug(" [x] RABBITMQ_QUEUE_NAME_SMCSUBGRAPH: message Sent to queue buffer: " + msg);
    }
  }

  @SuppressWarnings("unchecked")
  private void getD3VertexEdgeMovieGenres(String actorName, String movieName) throws IOException {
    /*
    String movieGenersKey = "moviegeners:"+actorName+":"+movieName;
    String genresListValue = GlobalVariables.JEDIS_API.get(movieGenersKey);

    if (genresListValue == null) {
      genresListValue = GlobalVariables.FREEBASE_OPS.getFilmGenresByActorNMovieName(actorName, movieName, false);
      GlobalVariables.JEDIS_OPS.cacheAKeyInCertainTime(movieGenersKey, genresListValue);
    }
     */

    String genresListValue;
    JSONArray results = new JSONArray();
    try {
      genresListValue = GlobalVariables.FREEBASE_OPS.getFilmGenresByActorNMovieName(actorName, movieName, false);
      JSONObject response = (JSONObject)parser.parse(genresListValue);
      results = (JSONArray)response.get("result");
    } catch (IOException | ParseException e) {
      System.out.println("############## FREEBASE_OPS IOException | ParseException");
      //GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_REC, null, freebaseReachLimitMsgRec.getBytes());
    }

    for (Object result : results) {
      //System.out.println(JsonPath.read(result,"$.starring[*].actor").toString());
      String[] genres = JsonPath.read(result,"$.genre").toString()
          .replace("[\"", "")
          .replace("\"]", "")
          .split("\",\"");
      genreMaxIdx = this.d3Vertices.size()-1;
      for (String genre : genres) {
        int curGenreIdxInJson = -1;
        if (!this.genreIdxJson.containsKey(genre)) {
          log.debug("Put genre " + genre +" into JSONArray d3Vertices..");

          d3Vertex = new JSONObject();  
          d3Vertex.put("name", genre.substring(
            0, genre.length()<3?genre.length():3));
          d3Vertex.put("fullname", genre);
          d3Vertex.put("entity", "genre");

          curGenreIdxInJson = ++genreMaxIdx;
          d3Vertices.add(d3Vertex);
          //                    this.sendMessage(d3Vertex.toString());
          this.genreIdxJson.put(genre, curGenreIdxInJson);
        } else {
          curGenreIdxInJson = this.genreIdxJson.get(genre);
        }

        log.debug("Put movie-genre link into JSONArray d3Edges..");
        d3Edge= new JSONObject();   
        d3Edge.put("source", this.movieIdxJson.get(movieName));
        d3Edge.put("target", curGenreIdxInJson);
        d3Edge.put("type", "linkmoviegenre");
        d3Edges.add(d3Edge);
      }
    }
  }

}

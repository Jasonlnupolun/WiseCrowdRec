package com.feiyu.deeplearning.RBM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.feiyu.springmvc.model.RBMClientWeightMatixForPredict;
import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.utils.GlobalVariables;
import com.jayway.jsonpath.JsonPath;

/**
 * 	 After getting the training and the testing data as well as trained RBM model for current RBM
 *	 show Movies and their Genres to Client immediately 
 *	 In which, don't show those Genres this Client dislikes
 *	 don't show those Movies this Client dislikes and those Movies related to those Genres this Client dislikes
 *	 get trained RBM weight matrix
 * @author feiyu
 */

public class ThreadRBMShowDataToClientPMins implements Runnable {
  private static Logger log = Logger.getLogger(ThreadRBMShowDataToClientPMins.class.getName());
  private String threadName;

  private JSONParser parser = new JSONParser();
  private JSONObject d3Data = new JSONObject();
  private JSONArray d3Vertices = new JSONArray();
  private JSONArray d3Edges = new JSONArray();
  private JSONObject d3Vertex;	
  private JSONObject d3Edge;	
  private int movieMaxIdx = -1;
  private int genreMaxIdx = -1;
  private HashMap<String, Integer> genreIdxJson = new HashMap<String, Integer>();
  private RBMClientWeightMatixForPredict rbmClientWeightMatix;
  private double[][][] Mw_rbm_client;
  private int numMovies;

  public ThreadRBMShowDataToClientPMins(String threadName) {
    this.threadName = threadName;
    this.numMovies = GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getMovieHashMap().size(); 

    this.Mw_rbm_client = new double[this.numMovies+1][GlobalVariables.RBM_SIZE_HIDDEN_UNITS+1][GlobalVariables.RBM_SIZE_SOFTMAX];
    for (int x=0; x<this.numMovies+1; x++) {
      for (int y=0; y<GlobalVariables.RBM_SIZE_HIDDEN_UNITS+1; y++) {
        for (int z=0; z<GlobalVariables.RBM_SIZE_SOFTMAX; z++) {
          this.Mw_rbm_client[x][y][z] = GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getWeightMatrixCurrentRBM()[x][y][z];
        }
      }
    }
    this.rbmClientWeightMatix = new RBMClientWeightMatixForPredict(
      GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getKthRBM(),
      GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getTimeTrained(),
      new HashMap<String, RBMMovieInfo>(GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getMovieHashMap()),
      this.Mw_rbm_client,
      GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getSuccessfullyTrainedThisRBM(),
      new ArrayList<String>(GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getMovieNameWithIdx())
        );

    log.info("Creating " +  this.threadName + " at " +System.currentTimeMillis());
  }

  public void run() {
    try {
      this.showMoviesGenresToClient_currentRBM();
      Thread.sleep(1);
    } catch (InterruptedException e) {
      log.info(this.threadName+" is interrupted at " + System.currentTimeMillis());
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  private void showMoviesGenresToClient_currentRBM() throws IOException, ParseException {
    log.info("^^^^^^Client disliked genres:");
    for (String g : GlobalVariables.RBM_CLIENT_DISLIKED_GENRES) {
      log.info("Client disliked the genre: "+ g);
    }

    log.info("^^^^^^Client disliked movies:");
    for (String m : GlobalVariables.RBM_CLIENT_DISLIKED_MOVIES) {
      log.info("Client disliked the movie: "+ m);
    }

    // send message to the RabbitMQ queue
    log.info("^^^^^^show movie list to client");
    log.info("movieList " + this.rbmClientWeightMatix.getMovieHashMap().toString());
    Iterator<Entry<String, RBMMovieInfo>> itMovie = this.rbmClientWeightMatix.getMovieHashMap().entrySet().iterator();
    movieloop: while (itMovie.hasNext()) {
      Map.Entry<String, RBMMovieInfo> pairs = (Map.Entry<String, RBMMovieInfo>)itMovie.next();
      //			itMovie.remove(); // avoids a ConcurrentModificationException

      String movieName = pairs.getKey();
      log.info(">>>>> movieName "+movieName);
      // if Client doesn't like this movie
      //			if (!this.currentData.getMovieHashMap().containsKey(movieName)) {
      //				log.info("&&&&& "+movieName+" not in MovieHashMap");
      //				//				GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(this.currentData.getMovieHashMap().get(movieName).getMovieIdx(), 0); 
      //				continue;
      //			}
      if (GlobalVariables.RBM_CLIENT_DISLIKED_MOVIES.contains(movieName))  {
        GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(
          this.rbmClientWeightMatix.getMovieHashMap().get(movieName).getMovieIdx(), 0); // 0 client dislikes this movie
        continue;
      }

      // Client might like this movie, then find the genres of this movie
      JSONObject response = (JSONObject)parser.parse(
        GlobalVariables.FREEBASE_GET_ACTOR_MOVIES.getFilmGenresByMovieId(
          pairs.getValue().getMid(), 
          false
            ));
      JSONArray results = (JSONArray)response.get("result");
      //			for (Object result : results) { // since using Mid to search in Freebase, results.size() = 1 
      String[] genres = JsonPath.read(results.get(0),"$./film/film/genre").toString()
          .replace("[\"", "")
          .replace("\"]", "")
          .split("\",\"");

      if (this.movieContainsAtLeastOneGenreClientDislike(genres)) {
        //				if (!this.currentData.getMovieHashMap().containsKey(movieName)) {
        //					log.info("&&&&& "+movieName+" not in MovieHashMap");
        //					//				GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(this.currentData.getMovieHashMap().get(movieName).getMovieIdx(), 0); 
        //					continue movieloop;
        //				}
        GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(
          this.rbmClientWeightMatix.getMovieHashMap().get(movieName).getMovieIdx(), 0); // 0 client dislikes one of the genres of this movie
        continue movieloop;
      } 
      //			}

      // then this client might like this movie and its genres 
      //			if (!this.currentData.getMovieHashMap().containsKey(movieName)) {
      //				log.info("&&&&& "+movieName+" not in MovieHashMap");
      //				//				GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(this.currentData.getMovieHashMap().get(movieName).getMovieIdx(), 0); 
      //				continue movieloop;
      //			}
      GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(
        this.rbmClientWeightMatix.getMovieHashMap().get(movieName).getMovieIdx(), 4); // 4 client might like this movie
      // later this client clicks and shows disliking this movie, then change to 0
      log.info("Rec --> Put movie " + movieName +" into JSONArray d3Vertices..");
      this.d3Vertex = new JSONObject();	
      this.d3Vertex.put("name", movieName.substring(
        0, movieName.length()<3?movieName.length():3));
      this.d3Vertex.put("fullname", movieName);
      this.d3Vertex.put("entity", "movie");

      ++this.movieMaxIdx;
      this.d3Vertices.add(this.d3Vertex);

      this.genreMaxIdx = this.d3Vertices.size()-1;	
      for (String genre : genres) {
        int curGenreIdxInJson = -1;
        if (!this.genreIdxJson.containsKey(genre)) {
          log.info("Rec --> Put genre " + genre +" into JSONArray d3Vertices..");

          this.d3Vertex = new JSONObject();	
          this.d3Vertex.put("name", genre.substring(
            0, genre.length()<3?genre.length():3));
          this.d3Vertex.put("fullname", genre);
          this.d3Vertex.put("entity", "genre");

          curGenreIdxInJson = ++this.genreMaxIdx;
          this.d3Vertices.add(this.d3Vertex);

          this.genreIdxJson.put(genre, curGenreIdxInJson);
        } else {
          curGenreIdxInJson = this.genreIdxJson.get(genre);
        }

        log.info("Rec --> Put movie-genre link into JSONArray d3Edges..");
        this.d3Edge= new JSONObject();	
        this.d3Edge.put("source", this.movieMaxIdx);
        d3Edge.put("target", curGenreIdxInJson);
        d3Edge.put("type", "linkmoviegenre");
        d3Edges.add(d3Edge);
      }
      this.movieMaxIdx = this.d3Vertices.size() - 1;
    }

    d3Data.put("nodes", d3Vertices);
    d3Data.put("links", d3Edges);
    String d3DataString = d3Data.toString();
    log.info(" [x] RABBITMQ_QUEUE_NAME_SPARK: Message Sent to queue buffer: "+ d3DataString);
    GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_SPARK, null, d3DataString.getBytes());
  }

  private boolean movieContainsAtLeastOneGenreClientDislike(String[] genres) {
    for (String genre : genres) {
      log.debug("movieContainsAtLeastOneGenreClientDislike genre: "+genre);
      if (GlobalVariables.RBM_CLIENT_DISLIKED_GENRES.contains(genre)) {
        return true;
      }
    }
    return false;
  }
}

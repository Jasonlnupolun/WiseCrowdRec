package com.feiyu.deeplearning.RBM;
/**
 * @author feiyu
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.feiyu.springmvc.model.Tuple;
import com.feiyu.utils.GlobalVariables;

public class MovieRecommendation {
  private static Logger log = Logger.getLogger(MovieRecommendation.class.getName());
  private BufferedWriter bw;

  @SuppressWarnings("unchecked")
  public void startMovieRec() throws IOException {
    this.createFileForRMSE();

    RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax = new RestrictedBoltzmannMachinesWithSoftmax(
      GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getMovieHashMap().size(), 
      GlobalVariables.RBM_SIZE_SOFTMAX, GlobalVariables.RBM_SIZE_HIDDEN_UNITS, 
      GlobalVariables.RBM_LEARNING_RATE, GlobalVariables.RBM_NUM_EPOCHS, this.bw
        );

    ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices = new ArrayList<Tuple<Integer,Integer>>();
    Iterator<Entry<Integer, Integer>> itRatedMovie = GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.entrySet().iterator();
    while (itRatedMovie.hasNext()) {
      Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>)itRatedMovie.next();
      ratedMoviesIndices.add(new Tuple<Integer,Integer>(pairs.getKey(),pairs.getValue()));
      log.info(">>>>>>>> ratedMovie: "+pairs.getKey() + " = " + pairs.getValue());
      //itRatedMovie.remove(); // avoids a ConcurrentModificationException
    }

    rbmSoftmax.predictUserPreference_VisibleToHiddenToVisible(ratedMoviesIndices, true);

    int nMovies = GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getMovieHashMap().size(); 
    JSONObject d3Data = new JSONObject();
    JSONArray d3Vertices = new JSONArray();
    JSONArray d3Edges = new JSONArray();
    JSONObject d3Vertex;	
    String movieName;
    for (int y=1; y<nMovies+1; y++) {
      movieName = GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getMovieNameWithIdx().get(y-1); //@
      log.info("$$$$$ movie client might like: " + movieName);
      d3Vertex = new JSONObject();	
      d3Vertex.put("name", movieName.substring(
        0, movieName.length()<3?movieName.length():3));
      d3Vertex.put("fullname", movieName);
      d3Vertex.put("entity", "movie");
      d3Vertices.add(d3Vertex);
    }
    log.info("d3Vertices:"+d3Vertices.toString());
    log.info("d3Edges:"+d3Edges.toString());
    d3Data.put("nodes", d3Vertices);
    d3Data.put("links", d3Edges);
    String d3DataString = d3Data.toString();
    log.info(" [x] RABBITMQ_QUEUE_NAME_SPARK Message Sent to queue buffer, movies client might like: "+ d3DataString);
    GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_SPARK, null, d3DataString.getBytes());
    bw.close();
  }

  private void createFileForRMSE() throws IOException {
    File file = new File("/Library/Tomcat/logs/MovieRecommendation"
        +GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getKthRBM()
        +".txt");
    if (!file.exists()) {
      file.createNewFile();
    }
    FileWriter fw = new FileWriter(file.getAbsoluteFile());
    bw = new BufferedWriter(fw);
  }

}

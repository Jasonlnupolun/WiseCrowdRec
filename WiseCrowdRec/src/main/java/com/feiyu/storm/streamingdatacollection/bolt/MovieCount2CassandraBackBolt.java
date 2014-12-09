package com.feiyu.storm.streamingdatacollection.bolt;
/**
 * @author feiyu
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;

import com.feiyu.springmvc.model.Movie;
import com.feiyu.springmvc.model.MovieWithCount;
import com.feiyu.springmvc.model.MovieWithCountComparable;
import com.feiyu.utils.GetTopK;
import com.feiyu.utils.GlobalVariables;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

@SuppressWarnings("serial")
public class MovieCount2CassandraBackBolt implements IBasicBolt {
  private static Logger log = Logger.getLogger(MovieCount2CassandraBackBolt.class.getName());
  Map<String, Integer> _counts;

  @SuppressWarnings("rawtypes")
  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    _counts = new HashMap<String, Integer>();
  }

  @Override
  public void execute(Tuple input, BasicOutputCollector collector) {
    // Later sliding window save hourly data into database -> note need to update both count and rating
    Movie movie = (Movie) input.getValueByField("movie");
    String movieIMDbID = movie.getIMDbID();
    String ratingPrev = null;
    int hybridRatingFromOneTweet = movie.getRating(), hybridRating = 0;

    // count how many times this movie rated by people in a sliding window
    int count = 0;
    if (_counts.containsKey(movieIMDbID)) {
      count = _counts.get(movieIMDbID);
    }
    count++;
    _counts.put(movieIMDbID, count);

    MovieWithCount movieWithCount = new MovieWithCount();
    movieWithCount.setCount(count);
    movieWithCount.setMovie(movie);
    collector.emit(new Values(movieWithCount)); //? _counts or collector
    // collector.emit(tuple(movieIMDbID, count)); //? _counts or collector

    count = count*movie.getRating(); //@@count -> HybridRating
    String movieName = movie.getMovieName();
    // send to ws://localhost:9998/stormchartws for storm real-time histogram chart
    if (!GlobalVariables.STORM_MOVIELIST_HM.containsKey(movieName)) {
      MovieCounter countOfMovie = new MovieCounter(count);
      GlobalVariables.STORM_MOVIELIST_HEAP.add(new MovieWithCountComparable(movieName, countOfMovie));
      GlobalVariables.STORM_MOVIELIST_HM.put(movieName, countOfMovie);
    }else {
      MovieCounter preCountOfMovie =GlobalVariables.STORM_MOVIELIST_HM.get(movieName);
      preCountOfMovie._count = count;
    }

    GetTopK getTopK = new GetTopK();
    try {
      getTopK.getTopKMovies(10);
    } catch (JSONException | IOException e2) {
      e2.printStackTrace();
    }

    // InsertData into Cassandra
    try {
      ratingPrev = GlobalVariables.AST_CASSANDRA_MNPLT.queryWithRowKeyGetRating(movieIMDbID);
    } catch (ConnectionException e) {
      //			e.printStackTrace();
      log.warn("Can not get hybridRating info from Cassandra");
    } 

    if (ratingPrev!= null) {
      hybridRating = Integer.valueOf(ratingPrev) + hybridRatingFromOneTweet;
    } else {
      hybridRating = hybridRatingFromOneTweet; 
    }

    try {
      GlobalVariables.AST_CASSANDRA_MNPLT.insertMovieDataToDB_asynchronous(
        movieIMDbID, 
        movie.getMovieName(), 
        Integer.toString(hybridRating), 
        Integer.toString(count));
    } catch (ConnectionException | InterruptedException | ExecutionException e1) {
      //			e1.printStackTrace();
      log.info("Run Cassandra first, please");
    }

    log.info("Count:"+count + "-> " + movie.toString());
  }

  @Override
  public void cleanup() {
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("movieWithCount"));
  }

  @Override
  public Map<String, Object> getComponentConfiguration() {
    return null;
  }
}
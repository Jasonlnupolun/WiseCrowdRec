package com.feiyu.storm.streamingdatacollection.bolt;
/**
 * @author feiyu
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;

import com.feiyu.springmvc.model.Movie;
import com.feiyu.springmvc.model.MovieWithCount;
import com.feiyu.springmvc.model.MovieWithCountComparable;
import com.feiyu.utils.GetTopK;
import com.feiyu.utils.GlobalVariables;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

@SuppressWarnings("serial")
public class ForTestMovieCountBolt implements IBasicBolt {
  private static Logger log = Logger.getLogger(ForTestGetMovieDataBolt.class.getName());
  Map<String, Integer> _counts;
  Movie movie;

  @SuppressWarnings("rawtypes")
  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    _counts = new HashMap<String, Integer>();
  }


  @Override
  public void execute(Tuple input, BasicOutputCollector collector) {
    // Later sliding window save hourly data into database 
    movie = (Movie) input.getValueByField("movie");
    String movieIMDbID = movie.getIMDbID();

    int count = 0;
    if (_counts.containsKey(movieIMDbID)) {
      count = _counts.get(movieIMDbID);
    }
    count++;

    _counts.put(movieIMDbID, count);
    log.info("Count:"+count + "-> " + movie.toString());

    MovieWithCount movieWithCount = new MovieWithCount();
    movieWithCount.setCount(count);
    movieWithCount.setMovie(movie);
    collector.emit(new Values(movieWithCount)); //? _counts or collector

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

  public static void main(String[] argv) {
    String movieName = "M1";
    int count = 1;

    if (!GlobalVariables.STORM_MOVIELIST_HM.containsKey(movieName)) {
      MovieCounter countOfMovie = new MovieCounter(count);
      GlobalVariables.STORM_MOVIELIST_HEAP.add(new MovieWithCountComparable(movieName, countOfMovie));
      GlobalVariables.STORM_MOVIELIST_HM.put(movieName, countOfMovie);
    }else {
      MovieCounter preCountOfMovie =GlobalVariables.STORM_MOVIELIST_HM.get(movieName);
      preCountOfMovie._count = count;
    }
    System.out.println("hm count:"+GlobalVariables.STORM_MOVIELIST_HM.get(movieName));
    System.out.println("hp count:"+GlobalVariables.STORM_MOVIELIST_HEAP.peek().toString());

    count = 3;

    if (!GlobalVariables.STORM_MOVIELIST_HM.containsKey(movieName)) {
      MovieCounter countOfMovie = new MovieCounter(count);
      GlobalVariables.STORM_MOVIELIST_HEAP.add(new MovieWithCountComparable(movieName, countOfMovie));
      GlobalVariables.STORM_MOVIELIST_HM.put(movieName, countOfMovie);
    }else {
      MovieCounter preCountOfMovie =GlobalVariables.STORM_MOVIELIST_HM.get(movieName);
      preCountOfMovie._count = count;
    }
    System.out.println("hm count:"+GlobalVariables.STORM_MOVIELIST_HM.get(movieName));
    System.out.println("hp count:"+GlobalVariables.STORM_MOVIELIST_HEAP.peek().toString());
  }
}
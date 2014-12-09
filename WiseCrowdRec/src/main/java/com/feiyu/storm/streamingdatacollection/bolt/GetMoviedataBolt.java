/**
 * @author feiyu
 */
package com.feiyu.storm.streamingdatacollection.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.URLEntity;

import com.feiyu.nlp.SentimentAnalyzerCoreNLP;
import com.feiyu.semanticweb.freebase.IMDbInfoQuery;
import com.feiyu.springmvc.model.EntityWithSentiment;
import com.feiyu.springmvc.model.Movie;
import com.omertron.themoviedbapi.MovieDbException;

@SuppressWarnings("serial")
public class GetMoviedataBolt extends BaseRichBolt {
  private static Logger log = Logger.getLogger(GetMoviedataBolt.class.getName());
  private OutputCollector _collector;
  private boolean tvShows = false; 

  @SuppressWarnings("rawtypes")
  @Override
  public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
    _collector = collector;
  }

  @Override
  public void execute(Tuple input) {
    Status tweet = (Status) input.getValueByField("tweet");

    Movie movie = new Movie();
    EntityWithSentiment ews;
    String lang = tweet.getIsoLanguageCode();
    String text = tweet.getText();
    int rating = -1;
    String IMDbID = null;

    if (lang.equals("en")) {

      // Get Movie ID
      URLEntity[] urls = tweet.getURLEntities();
      if (urls.length > 0 ) {
        String[] ary = urls[urls.length-1].toString().split("/");
        IMDbID = ary[ary.length-1].replaceAll("[^a-zA-Z0-9]","");
        log.debug("--------- "+IMDbID);
      }
      movie.setIMDbID(IMDbID);

      // Get Moive Name
      SentimentAnalyzerCoreNLP sacn = new SentimentAnalyzerCoreNLP();
      ews = sacn.getEntitiesWithSentiment(text);
      IMDbInfoQuery imdbIQ = new IMDbInfoQuery();	
      String movieName = "";
      try {
        movieName = imdbIQ.getMoiveName(IMDbID);
        movie.setMovieName(movieName);
        tvShows = false;
      } catch (MovieDbException e) {
        //e.printStackTrace();
        movie.setMovieName("TV Shows"); 
        tvShows = true;
        // v4.0 of api-themoviedb support TV shows' information ->  https://github.com/Omertron/api-themoviedb
      }

      // Get the rating of this movie
      HashMap<String, String> hm = ews.getEntityWithCategory();
      String entity = "", category = "";

      if (hm != null) {
        Iterator<Entry<String, String>> it = hm.entrySet().iterator();
        while (it.hasNext()) {
          Map.Entry<String, String> pairs = it.next();
          entity =  pairs.getKey();
          category =  pairs.getValue();

          if (category.equals("NUMBER")) {
            String[] list = entity.split("/");
            System.out.println(list[0]);
            rating = Integer.valueOf(list[0].replaceAll("[^0-9]","")); 
            // @@@ modify this later -> might lose some tweets -> e.g. Hunger Games 2 9/10 -> "2 9/10"=NUMBER
          }
          it.remove(); // avoids a ConcurrentModificationException
        } 

        // hybridRating = (FavoriteCount of this tweet + RetweetCount of this tweet)*rating got from this tweet
        int hybridRating = 0;
        int crowdCount = tweet.getFavoriteCount() + tweet.getRetweetCount(); 
        // @@@@@ modify this later -> Streaming data can not get favorite and retweet count info
        if (crowdCount <= 0) {
          hybridRating = rating;
        } else {
          hybridRating = crowdCount * rating;
        }

        movie.setRating(hybridRating);
      } else {
        movie.setRating(-1);
      }
    }

    if ((IMDbID != null) && !tvShows && (rating >= 0 && rating <= 10) ) {
      log.debug("===== " + movie.toString());
      _collector.emit(new Values(movie));
    }
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("movie"));
  }
}
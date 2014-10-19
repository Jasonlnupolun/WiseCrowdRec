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

import com.feiyu.nlp.SentimentAnalyzerCoreNLP;
import com.feiyu.semanticweb.freebase.IMDbInfoQuery;
import com.feiyu.springmvc.model.EntityWithSentiment;
import com.feiyu.springmvc.model.Movie;
import com.omertron.themoviedbapi.MovieDbException;

@SuppressWarnings("serial")
public class ForTestGetMovieDataBolt extends BaseRichBolt {
	private static Logger log= Logger.getLogger(ForTestGetMovieDataBolt.class.getName());
	private OutputCollector _collector;
	private boolean tvShows = false; 

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
		_collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		String tweet_fake = (String) input.getValueByField("tweet");

		Movie movie = new Movie();
		EntityWithSentiment ews;
		int rating = -1;
		String lang = "en";

		if (lang.equals("en")) {

			// Get Movie ID
			String[] ary = tweet_fake.split("/");
			String IMDbID = ary[ary.length-1];
			movie.setIMDbID(IMDbID);

			// Get Moive Name
			SentimentAnalyzerCoreNLP sacn = new SentimentAnalyzerCoreNLP();
			ews = sacn.getEntitiesWithSentiment(tweet_fake);
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
					entity = pairs.getKey();
					category = pairs.getValue();

					if (category.equals("NUMBER")) {
						String[] list = entity.split("/");
						rating = Integer.valueOf(list[0]);
					}
					it.remove(); // avoids a ConcurrentModificationException
				} 
				movie.setRating(rating);
			} else {
				movie.setRating(-1);
			}
		}

		if (!tvShows || rating>10 || rating<0) {
			log.debug("===== " + movie.toString());
			_collector.emit(new Values(movie));
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("movie"));
	}
}
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
import twitter4j.Status;
import twitter4j.URLEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.feiyu.nlp.SentimentAnalyzerCoreNLP;
import com.feiyu.semanticweb.IMDbInfoQuery;
import com.feiyu.springmvc.model.EntityInfo;
import com.feiyu.springmvc.model.EntityWithSentiment;
import com.feiyu.springmvc.model.Movie;
import com.feiyu.springmvc.model.Tweet;
import com.feiyu.util.GlobalVariables;
import com.omertron.themoviedbapi.MovieDbException;

@SuppressWarnings("serial")
public class GetMetadataBolt extends BaseRichBolt {
//	private static final Logger _logger = LoggerFactory.getLogger(GetMetadataBolt.class);
	private static Tweet _t = new Tweet();
	private OutputCollector _collector;
	private static Logger log = Logger.getLogger(GetMetadataBolt.class.getName());

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
		_collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		Status tweet = (Status) input.getValueByField("tweet");
		Movie movie = new Movie();
		
		String lang = tweet.getIsoLanguageCode();
		if (lang.equals("en")) {
			
			URLEntity[] urls = tweet.getURLEntities();
			String[] ary = urls[urls.length-1].toString().replace("'","").split("/");
			String IMDbID = ary[ary.length-1];
			log.info("========== " + IMDbID);
			movie.setIMDbID(IMDbID);
			
			SentimentAnalyzerCoreNLP sacn = new SentimentAnalyzerCoreNLP();
			EntityWithSentiment ews = sacn.getEntitiesWithSentiment(_t.getText());
			IMDbInfoQuery imdbIQ = new IMDbInfoQuery();	
			String movieName = "";
			try {
				movieName = imdbIQ.getMoiveName(IMDbID);
			} catch (MovieDbException e) {
//				e.printStackTrace();
				log.error("themoviedb api: can not get movie name");
			}
			movie.setMovieName(movieName);
			
			HashMap<String, String> hm = ews.getEntityWithCategory();
			String entity = "", category = "";
			if (hm != null) {
				int rating = -1;
				Iterator<Entry<String, String>> it = hm.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
					entity = (String) pairs.getKey();
					category = (String) pairs.getValue();
					
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

			log.info("========== " + ews.getEntityWithCategory());
			log.info("========== " + movie.toString());
		}
		
		_collector.emit(new Values(movie));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("movie"));
	}
}

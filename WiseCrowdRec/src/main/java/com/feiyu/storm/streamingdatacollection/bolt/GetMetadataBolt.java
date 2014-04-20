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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feiyu.model.Tweet;
import com.feiyu.tools.EntityExtraction;

@SuppressWarnings("serial")
public class GetMetadataBolt extends BaseRichBolt {
	private static final Logger _logger = LoggerFactory.getLogger(GetMetadataBolt.class);
	private static Tweet _t = new Tweet();
	private OutputCollector _collector;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
		_collector = collector;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Tuple input) {
		Status tweet = (Status) input.getValueByField("tweet");
		EntityExtraction entityExtract = new EntityExtraction();
		HashMap<String, String> hm = null;
		
		// Get Metadata
		_t.setLang(tweet.getLang());
		_t.setTime(tweet.getCreatedAt());
		_t.setText(tweet.getText());
		
		try {
			hm = entityExtract.getEntities(_t.getText());
		} catch (IOException e) {
			//e.printStackTrace();
			//_logger.info("No entities have been extracted from this tweet!");
		}
		_t.setEntities(hm);
		String entities = "{";
		if (hm != null) {
			Iterator it = hm.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				entities += "<"+pairs.getKey() + " = " + pairs.getValue()+">";
				it.remove(); // avoids a ConcurrentModificationException
			} 
		}
		entities += "}";
		
		if (_t.getLang().equals("en")) {
			_logger.info("---> " + _t.getLang() 
					+ " --> "+ _t.getTime().toString()
					+ " --> "+ _t.getText() 
					+ " --> " + entities);
		}
		_collector.emit(new Values(tweet));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("metadata"));
	}
}

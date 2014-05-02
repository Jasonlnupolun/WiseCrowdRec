package com.feiyu.storm.streamingdatacollection.bolt;

import static backtype.storm.utils.Utils.tuple;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feiyu.model.Tweet;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

@SuppressWarnings("serial")
public class InfoFilterBolt implements IBasicBolt {
	private static final Logger _logger = LoggerFactory.getLogger(GetMetadataBolt.class);
	private static Tweet _t = new Tweet();
	HashMap<String, String> hm = null;
    Map<String, Integer> counts;
    private String _showInfo;

    @SuppressWarnings("rawtypes")
	@Override
    public void prepare(Map stormConf, TopologyContext context) {
        this.counts = new HashMap<String, Integer>();
    }

    @SuppressWarnings("rawtypes")
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
    	_t = (Tweet)input.getValueByField("tweetMetadata");
    	hm = _t.getEntities();
    	
		String entities = "{";
		if (hm != null) {
			Iterator it = hm.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				entities += "<"+pairs.getKey() + " = " + pairs.getValue()+">";
				collector.emit(tuple(pairs.getKey(), pairs.getValue()));
				it.remove(); // avoids a ConcurrentModificationException
			} 
		}
		entities += "}";
		
		if (_t.getLang().equals("en")) {
			_showInfo = "---> " + _t.getLang() 
					+ " --> "+ _t.getTime().toString()
					+ " --> "+ _t.getText() 
					+ " --> " + entities;
			_logger.info(_showInfo);
		}
    	
    }

	@Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("entity", "category"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

	@Override
	public void cleanup() {
	}

}
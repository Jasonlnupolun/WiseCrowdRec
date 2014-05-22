package com.feiyu.storm.streamingdatacollection.bolt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feiyu.model.EntityInfo;
import com.feiyu.model.Tweet;
import com.feiyu.util.GlobalVariables;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

@SuppressWarnings("serial")
public class InfoFilterBolt implements IBasicBolt {
	private static final Logger _logger = LoggerFactory.getLogger(InfoFilterBolt.class);
	private static Tweet _t = new Tweet();
	HashMap<String, String> hm = null;

    @SuppressWarnings("rawtypes")
	@Override
    public void prepare(Map stormConf, TopologyContext context) {
    }

    @SuppressWarnings("rawtypes")
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
    	_t = (Tweet)input.getValueByField("tweetMetadata");
    	hm = _t.getEntities();
		String entity= null, category = null;
		
		if (_t.getLang().equals("en") && hm != null) {
			Iterator it = hm.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry)it.next();
				entity = (String) pairs.getKey();
				category = (String) pairs.getValue();
				EntityInfo eInfo = new EntityInfo(entity, category, 
						_t.getSentiment(), GlobalVariables.SENTI_CSS, _t.getTime().toString(), _t.getText());
				collector.emit(new Values(eInfo));
				_logger.info(eInfo.toString());
				it.remove(); // avoids a ConcurrentModificationException
			} 
		}
    }

	@Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("entityInfo"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

	@Override
	public void cleanup() {
	}
}
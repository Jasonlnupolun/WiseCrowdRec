package com.feiyu.storm.streamingdatacollection.bolt;

import static backtype.storm.utils.Utils.tuple;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

@SuppressWarnings("serial")
public class EntityCountBolt implements IBasicBolt {
	private static final Logger _logger = LoggerFactory.getLogger(GetMetadataBolt.class);
    Map<String, Integer> _counts;
//    CassandraManipulator _cm;

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        _counts = new HashMap<String, Integer>();
    }

    @Override
	public void execute(Tuple input, BasicOutputCollector collector) {
        String entity = (String) input.getValues().get(0);
        String category= (String) input.getValues().get(1);
//        _cm.insertDataToDB("tw", entity, category);
    	int count = 0;
        if (_counts.containsKey(entity)) {
            count = _counts.get(entity);
        }
        count++;
        _counts.put(entity, count);
    	
        _logger.info("entity:category<" + entity+":"+category+">, count:"+count);
//        _cm.queryDB("tw");
        collector.emit(tuple(entity, count));
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

}
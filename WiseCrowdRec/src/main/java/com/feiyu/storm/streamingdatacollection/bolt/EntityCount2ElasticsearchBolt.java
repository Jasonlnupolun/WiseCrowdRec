package com.feiyu.storm.streamingdatacollection.bolt;

import static backtype.storm.utils.Utils.tuple;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feiyu.elasticsearch.SerializeBeans2JSON;
import com.feiyu.springmvc.model.Entity;
import com.feiyu.springmvc.model.EntityInfo;
import com.feiyu.util.GlobalVariables;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

@SuppressWarnings("serial")
public class EntityCount2ElasticsearchBolt implements IBasicBolt {
	private static final Logger _logger = LoggerFactory.getLogger(EntityCount2ElasticsearchBolt.class);
    Map<String, Integer> _counts;

    @SuppressWarnings("rawtypes")
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        _counts = new HashMap<String, Integer>();
    }

    @Override
	public void execute(Tuple input, BasicOutputCollector collector) {
    	// Later sliding window save hourly data into database 
    	EntityInfo entityInfo = (EntityInfo) input.getValueByField("entityInfo");
        String entity = entityInfo.getEntity();
        String category= entityInfo.getCategory(); 
    	int count = 0;
        if (_counts.containsKey(entity)) {
            count = _counts.get(entity);
        }
        count++;
        _counts.put(entity, count);
        _logger.info("EntityCount:category<" + entity+":"+category+">, count:"+count);
        collector.emit(tuple(entity, count)); //? _counts or collector
       
        Random rand = new Random();
		Entity entityObj = new Entity(
				Integer.toString(rand.nextInt(60)), // modify this later
				count, 
				entityInfo);

        // InsertData into ES 
        SerializeBeans2JSON sb2json = new SerializeBeans2JSON(); // ElasticSearch requires index data as JSON.
        String entityObjJson = null;
		try {
			entityObjJson = sb2json.serializeBeans2JSON_Entity(entityObj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		GlobalVariables.JEST_ES_MNPLT.builderIndex_OneRecord(entityObjJson, entityObj.getEntityID(), GlobalVariables.CLEAN_BEFORE_INSERT_ES);
		if (GlobalVariables.CLEAN_BEFORE_INSERT_ES){
			GlobalVariables.CLEAN_BEFORE_INSERT_ES = false;
		}
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("entity", "count"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

}
package com.feiyu.storm.streamingdatacollection.bolt;

import static backtype.storm.utils.Utils.tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feiyu.model.EntityInfo;
import com.feiyu.util.GlobalVariables;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

@SuppressWarnings("serial")
public class EntityCount2CassandraDynaBolt implements IBasicBolt {
	private static final Logger _logger = LoggerFactory.getLogger(EntityCount2CassandraDynaBolt.class);
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
       
        // InsertData into Cassandra
        Random rand = new Random();
        try {
			GlobalVariables.AST_CASSANDRA_MNPLT.insertDataToDB_asynchronous(
					Integer.toString(rand.nextInt(60)), 
					entityInfo.getEntity(), entityInfo.getCategory(), Integer.toString(3), 
					entityInfo.getTime().toString(), entityInfo.getText(), 
					Integer.toString(count), entityInfo.toString(),true);
		} catch (ConnectionException | InterruptedException | ExecutionException e) {
//			e.printStackTrace();
			_logger.info("Run Cassandra first, please");
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
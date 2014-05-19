/**
 * @author feiyu
 * 
 * spout working process:
 * spout.nextTuple() -> spout.open(...,SpoutOutputCollector collector) -> emit tuple to bolt
 * if a tuple is fully processed, storm call spout.ack(Object msgId)
 * if a tuple is not fully processed, storm call spout.fail(Object msgId)
 * spout.close()
 * 
 * pending messages are put back on the queue
 * ack/fail used for guaranteeing message processing reference: 
 * https://github.com/nathanmarz/storm/wiki/Guaranteeing-message-processi
 */
package com.feiyu.storm.streamingdatacollection.spout;

import backtype.storm.Config;
import twitter4j.TwitterStream;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.conf.ConfigurationBuilder;

import com.feiyu.util.SearchTweetsImpl;

@SuppressWarnings("serial")
public class TwitterQuaryStreamSpout extends BaseRichSpout {
	private SpoutOutputCollector _collector;
	private LinkedBlockingQueue<Status> _queue = null;
	private TwitterStream _twitterStream;
	private static Properties _wcrProps;
	private static ConfigurationBuilder _twitterConf;

	public TwitterQuaryStreamSpout (ConfigurationBuilder twitterConf, Properties wcrProps) {
		_twitterConf = twitterConf;
		_wcrProps = wcrProps;
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config ret = new Config();
		ret.setMaxTaskParallelism(1);
		return ret;
	}    

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		_queue = new LinkedBlockingQueue<Status>(1000);
		_collector = collector;
		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {
				_queue.offer(status);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice sdn) {
			}

			@Override
			public void onTrackLimitationNotice(int i) {
			}

			@Override
			public void onScrubGeo(long l, long l1) {
			}

			@Override
			public void onException(Exception e) {
			}

			@Override
			public void onStallWarning(StallWarning warning) {

			}

		};
		SearchTweetsImpl t = new SearchTweetsImpl(_twitterConf, _wcrProps, listener, _twitterStream);
		t.searchTweetsFromNowOn();
		//t.searchTweetsRandomSample();
	}

	@Override
	public void nextTuple() {
		Status ret = _queue.poll();
		if(ret==null) {
			Utils.sleep(5);
		} else {
			_collector.emit(new Values(ret));
		}
	}

	@Override
	public void ack(Object id) {
	}

	@Override
	public void fail(Object id) {
	}

	@Override
	public void close() {
		_twitterStream.shutdown();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet"));
	}
}
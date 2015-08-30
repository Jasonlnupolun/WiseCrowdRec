package com.feiyu.storm.streamingdatacollection.spout;
/**
 * from https://github.com/apache/incubator-storm/blob/master/examples/storm-starter/src/jvm/storm/starter/spout/RandomSentenceSpout.java
 * modified by feiyu
 */

import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

@SuppressWarnings("serial")
public class ForTestFakeSpout extends BaseRichSpout {
  private SpoutOutputCollector _collector;
  private Random _rand;

  @SuppressWarnings("rawtypes")
  @Override
  public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
    _collector = collector;
    _rand = new Random();
  }

  @Override
  public void nextTuple() {
    Utils.sleep(5000);
    String[] tweets = new String[]{ 
                                   "I rated X-Men: Days of Future Past 8/10  #IMDb http://www.imdb.com/title/tt1877832",
                                   "I rated Game of Thrones 10/10  #IMDb http://www.imdb.com/title/tt0944947",
                                   "I rated Snowpiercer 7/10  #IMDb really enjoyed this. Beautifully shot & choreographed. Great performance from Swinton http://www.imdb.com/title/tt1706620",
                                   "Back on form. That ending = awesome! - I rated X-Men: Days of Future Past 7/10  #IMDb http://www.imdb.com/title/tt1877832",
                                   "A great movie especially for those trying to learn German ~> I rated Run Lola Run 8/10  #IMDb http://www.imdb.com/title/tt0130827",
                                   "I rated Breaking Bad 8/10  #IMDb :: I would say 7 but last season made it worth it... Matter of taste @mmelgarejoc  http://www.imdb.com/title/tt0903747",
                                   "I rated White House Down 7/10  #IMDb bunch of explosions and one liners, fun for all http://www.imdb.com/title/tt2334879"
    };
    String tweet = tweets[_rand.nextInt(tweets.length)];
    _collector.emit(new Values(tweet));	
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("tweet"));
  }
}

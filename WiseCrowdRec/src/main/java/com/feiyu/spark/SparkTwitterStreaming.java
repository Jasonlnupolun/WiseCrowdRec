package com.feiyu.spark;
/**
 * reference: http://ampcamp.berkeley.edu/3/exercises/realtime-processing-with-spark-streaming.html
 * https://github.com/rabbitmq/rabbitmq-tutorials/tree/master/java
 * @author feiyu
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.feiyu.elasticsearch.SerializeBeans2JSON;
import com.feiyu.nlp.SentimentAnalyzerCoreNLP;
import com.feiyu.springmvc.model.EntityInfo;
import com.feiyu.springmvc.model.EntityWithSentiment;
import com.feiyu.springmvc.model.Tweet;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;

import scala.Tuple2;
import twitter4j.Status;
import twitter4j.auth.Authorization;
import twitter4j.auth.OAuthAuthorization;

public class SparkTwitterStreaming implements java.io.Serializable   {
	private static final long serialVersionUID = -1741488739982924186L;
	private static JavaStreamingContext ssc;
	private static Logger log = Logger.getLogger(SparkTwitterStreaming.class.getName());
	private String labe1 = "MovieStarName";
	private String labe2 = "CountInFiveMinus";

	public void sparkInit() {
		PropertyConfigurator.configure(SparkTwitterStreaming.class.getClassLoader().getResource("log4j.properties"));
		//		note: import org.apache.log4j.Logger;
		//		note: import org.apache.log4j.Level;
		//		Logger.getLogger("org").setLevel(Level.WARN);
		//		Logger.getLogger("akka").setLevel(Level.WARN);
		// Set spark streaming info
		ssc = new JavaStreamingContext(
				"local[2]", "SparkTwitterStreamingJava", 
				new Duration(1000), System.getenv("SPARK_HOME"), 
				JavaStreamingContext.jarOfClass(SparkTwitterStreaming.class));

		//	HDFS directory for checkpointing
		/*
		 * checkpoint saves the RDD to an HDFS file
		 * http://apache-spark-user-list.1001560.n3.nabble.com/checkpoint-and-not-running-out-of-disk-space-td1525.html
		 * dfs.namenode.checkpoint.dir -> hdfs-site.xml
		 */
		//		String checkpointDir = TutorialHelper.getHdfsUrl() + "/checkpoint/";

		String checkpointDir = "file:///Users/feiyu/workspace/Hadoop/hdfs/namesecondary/checkpoint";
		ssc.checkpoint(checkpointDir);
	}

	public void startSpark(String searchPhrases) {
		String[] keywords = searchPhrases.split(" ");

		Authorization auth = new OAuthAuthorization(GlobalVariables.TWT_CONF_BUILDER_DYNA.build());
		JavaDStream<Status> tweets = TwitterUtils.createStream(ssc, auth, keywords);

		log.info("<Info>------start spark");
		JavaDStream<Tweet> tweetsInfo = tweets.map(
				new Function<Status, Tweet>() {
					private static final long serialVersionUID = -1124355253292906965L;
					@Override
					public Tweet call(Status tweetStatus) throws IOException { 

						Tweet _t = new Tweet();

						// Get Metadata
						String lang = tweetStatus.getIsoLanguageCode();
						if (lang.equals("en")) {
							_t.setUserId(tweetStatus.getUser().getId());
							_t.setLang(lang);
							_t.setTime(tweetStatus.getCreatedAt());
							_t.setText(tweetStatus.getText().replaceAll("[^a-zA-Z0-9]"," ").toLowerCase());
							// [^a-zA-Z0-9]
							// (?:^|\s)[a-zA-Z]+(?=\s|$)
							log.debug("text=============="+tweetStatus.getText());

							SentimentAnalyzerCoreNLP sacn = new SentimentAnalyzerCoreNLP();
							EntityWithSentiment ews = sacn.getEntitiesWithSentiment(_t.getText());
							_t.setEntities(ews.getEntityWithCategory());

							_t.setSentiment(ews.getSentiment());

							log.debug("_t---------->"+_t.toString());
							return _t;
						}
						return null;
					} 
				}
				);
		//		tweetsInfo.print();

		JavaDStream<EntityInfo> entities = tweetsInfo.map(
				new Function<Tweet, EntityInfo>() {
					private static final long serialVersionUID = -8772015990194582381L;

					@Override
					public EntityInfo call(Tweet tweet) throws Exception {
						if (tweet != null) {
							HashMap<String, String> hm = tweet.getEntities();
							String entity= null, category = null;

							if (hm != null) {
								Iterator<Entry<String, String>> it = hm.entrySet().iterator();
								while (it.hasNext()) {
									Map.Entry<String, String> pairs = it.next();
									entity =  pairs.getKey();
									category =  pairs.getValue();
									EntityInfo eInfo = new EntityInfo(entity, category, 
											tweet.getSentiment(), GlobalVariables.SENTI_CSS, 
											tweet.getTime().toString(), tweet.getText(), tweet.getUserId());
									it.remove(); // avoids a ConcurrentModificationException
									return eInfo;
								} 
							}
						}
						return null;
					}
				}
				);

		//		entities.print();

		JavaDStream<EntityInfo> filteredEntitiesN2ES = entities.filter(
				new Function<EntityInfo, Boolean>() {
					private static final long serialVersionUID = 4606758552085228337L;

					@Override
					public Boolean call(EntityInfo entityInfo) throws Exception {
						if (entityInfo == null || (!entityInfo.getCategory().equals("PERSON"))) {
							return false;
						}
						log.debug("entityInfo---------->"+entityInfo.toString());

						// Insert entityInfo into ES 
						SerializeBeans2JSON sb2json = new SerializeBeans2JSON(); // ElasticSearch requires index data as JSON.
						String entityInfoJson = null;
						try {
							entityInfoJson = sb2json.serializeBeans2JSON(entityInfo);
						} catch (IOException e) {
							e.printStackTrace();
						}

						GlobalVariables.JEST_ES_MNPLT.builderIndex_OneRecord(
								entityInfoJson,
								//								id, //modify this later
								GlobalVariables.CLEAN_BEFORE_INSERT_ES);
						if (GlobalVariables.CLEAN_BEFORE_INSERT_ES){
							GlobalVariables.CLEAN_BEFORE_INSERT_ES = false;
						}
						return true;
					}
				}
				);

		//		filteredEntitiesN2ES.print();

		JavaDStream<String> entitiesWithEntityInfo = filteredEntitiesN2ES.map(
				new Function<EntityInfo, String>() {
					private static final long serialVersionUID = -7600117219392603005L;

					@Override
					public String call(EntityInfo entityInfo) throws Exception {
						JSONObject jsonObject;
						jsonObject = new JSONObject();
						try {
							jsonObject.put("userid", String.valueOf(entityInfo.getUserid()));
							jsonObject.put("candidateactor", entityInfo.getEntity());
							jsonObject.put("rating", String.valueOf(entityInfo.getSentitment())); //sentiment
						} catch (Exception e) {
							e.printStackTrace();
						}

						String json = jsonObject.toString();
						log.debug("\n-------------------\n-------------------\ntriple(userid, candidateactor, rating):\n"+json);
						
						// rabbitmq, sending training/testing to RBM
						GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_RBMDATACOLLECTION, null, json.getBytes());
						log.info(" [x] RABBITMQ_QUEUE_NAME_RBMDATACOLLECTION: Message Sent to queue buffer: "+ json);

						return entityInfo.getEntity();
					}
				});

		JavaPairDStream<String, Integer> entityTuples = entitiesWithEntityInfo.mapToPair(
				new PairFunction<String, String, Integer>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Tuple2<String, Integer> call(String entity) throws Exception {
						return new Tuple2<String, Integer>(entity, 1);
					}
				}
				);
		//		entityTuples.print();

		JavaPairDStream<String, Integer> entityCount = entityTuples.reduceByKeyAndWindow(
				new Function2<Integer, Integer, Integer> () {
					private static final long serialVersionUID = 1L;

					@Override
					public Integer call(Integer c1, Integer c2) throws Exception {
						return c1+c2;
					}
				},
				new Duration(5*1000*60)); // 5 mins
		//		entityCount.print();

		JavaPairDStream<Integer, String> swappedCounts = entityCount.mapToPair(
				new PairFunction<Tuple2<String, Integer>, Integer, String>() {
					private static final long serialVersionUID = -4651464169440876349L;

					public Tuple2<Integer, String> call(Tuple2<String, Integer> in) {
						return in.swap();
					}
				}
				);
		JavaPairDStream<Integer, String> sortedCounts = swappedCounts.transformToPair(
				new Function<JavaPairRDD<Integer, String>, JavaPairRDD<Integer, String>>() {

					private static final long serialVersionUID = -5874306184950642137L;

					public JavaPairRDD<Integer, String> call(JavaPairRDD<Integer, String> in) throws Exception {
						return in.sortByKey(false);
					}
				});
		sortedCounts.foreach(
				new Function<JavaPairRDD<Integer, String>, Void> () {

					private static final long serialVersionUID = 4754852556443175871L;

					public Void call(JavaPairRDD<Integer, String> rdd) throws IOException {
						JSONArray jsonArray = new JSONArray();
						JSONObject jsonObject;
						int i=0;
						for (Tuple2<Integer, String> t: rdd.take(7)) {
							jsonObject = new JSONObject();
							try {
								jsonObject.put(labe1, t._2);
								jsonObject.put(labe2, String.valueOf(t._1));
							} catch (Exception e) {
								e.printStackTrace();
							}

							jsonArray.put(jsonObject);
							i++;
						}
						String json = jsonArray.toString();
						log.debug("\n-------------------\n-------------------\nTop 7 entities:\n"+json);

						if (i>0) {
							// send message to the RabbitMQ queue
//							GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_SPARK, null, json.getBytes());
//							log.info(" [x] RABBITMQ_QUEUE_NAME_SPARK: Message Sent to queue buffer: "+json);

							GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_SPARKHISTOGRAMCHART, null, json.getBytes());
							log.info(" [x] RABBITMQ_QUEUE_NAME_SPARKHISTOGRAMCHART: Message Sent to queue buffer: "+json);
						}
						return null;
					}
				}
				);

		ssc.start();
	}

	//	public void closeRabbitmq() throws IOException {
	//		channel.close();
	//	    connection.close();
	//	}

	public static void main(String[] argv) throws Exception {

		InitializeWCR initWcr = new InitializeWCR();
		initWcr.getWiseCrowdRecConfigInfo();
		initWcr.twitterInitDyna();
		initWcr.elasticsearchInitial();
		initWcr.coreNLPInitial();
		initWcr.rabbitmqInit();

		SparkTwitterStreaming sts = new SparkTwitterStreaming();
		sts.sparkInit();
		sts.startSpark("movie");

		//		sts.closeRabbitmq();
	}
}
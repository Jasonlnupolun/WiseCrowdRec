package com.feiyu.deeplearning.RBM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.feiyu.semanticweb.freebase.GetActorMovieGenreSubgraphVectorNEdge;
import com.feiyu.springmvc.model.RBMDataQueueElementInfo;
import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.springmvc.model.RBMUserInfo;
import com.feiyu.utils.GlobalVariables;
import com.jayway.jsonpath.JsonPath;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author feiyu
 */

public class RBMRabbitMQServerSide {
	private final static String QUEUE_NAME = GlobalVariables.RABBITMQ_QUEUE_NAME_RBMDATACOLLECTION;
	private HashMap<String, RBMUserInfo> userHashMap;
	private ArrayList<String> userList;
	private int userMaxIdx;

	public RBMRabbitMQServerSide() {
		userHashMap  = new HashMap<String, RBMUserInfo>();
		userList = new ArrayList<String>();
		userMaxIdx = -1;
	}

	public void rbmRabbitMQServerSide(String threadName, boolean isForTrain) throws ParseException {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection rabbitmqConnection;
			rabbitmqConnection = factory.newConnection();
			Channel channel = rabbitmqConnection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			System.out.println(" [*] "+ threadName +" server is waiting for training/testing data (user-movie-rating triple). To exit press CTRL+C");

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_NAME, true, consumer);

			while (true) {
				QueueingConsumer.Delivery delivery = null;
				try {
					delivery = consumer.nextDelivery();
					String message = new String(delivery.getBody());
					System.out.println(" [...x...] "+ threadName +"server received '" + message.replaceAll("\\s+","") + "'");

					// triple(userid, candidateactor, rating)
					JSONParser parser = new JSONParser();
					JSONObject jsonTipleUCR = (JSONObject)parser.parse(message);

					GetActorMovieGenreSubgraphVectorNEdge getActorMovies = new GetActorMovieGenreSubgraphVectorNEdge();
					String actorMovieList = getActorMovies.getMovieListByActorName(jsonTipleUCR.get("candidateactor").toString());
					JSONObject jsonActorMovieList= (JSONObject)parser.parse(actorMovieList);
					JSONArray jsonArrayMovieList = (JSONArray)jsonActorMovieList.get("result");
					for (Object result : jsonArrayMovieList) {
						this.storeTripleIntoRBMDataMatix(jsonTipleUCR.get("userid").toString(), 
								JsonPath.read(result,"$.name").toString(), 
								jsonTipleUCR.get("rating").toString(),
								isForTrain);
						System.out.println(jsonTipleUCR.get("userid")+" -- "+JsonPath.read(result,"$.name").toString()+" -- "+jsonTipleUCR.get("rating"));
					}
				} catch (ShutdownSignalException | ConsumerCancelledException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					GlobalVariables.RBM_DATA_QUEUE.add(new RBMDataQueueElementInfo(
							isForTrain,
							isForTrain ? ++GlobalVariables.KTH_RBM : GlobalVariables.KTH_RBM,
							new HashMap<String, RBMUserInfo>(this.userHashMap)
							));
					System.out.println(threadName+ " Rabbitmq is interrupted at " + System.currentTimeMillis());

					// For test
					System.out.println("\nmmmmmmmmmmmmmmmKthRbm "+GlobalVariables.KTH_RBM);
					System.out.println("========DataQueueSize "+GlobalVariables.RBM_DATA_QUEUE.size());
					for (RBMDataQueueElementInfo item : GlobalVariables.RBM_DATA_QUEUE) {
						System.out.println(item);
					}
					System.out.println("========numMovies "+GlobalVariables.RBM_MOVIE_LIST.size());
					System.out.println("========MOVIE_MAX_IDX "+GlobalVariables.RBM_MOVIE_MAX_IDX);
					System.out.println("========RBM_MOVIE_HASHMAP size "+GlobalVariables.RBM_MOVIE_HASHMAP.size());
					Iterator<Entry<String, RBMMovieInfo>> itMovie = GlobalVariables.RBM_MOVIE_HASHMAP.entrySet().iterator();
					while (itMovie.hasNext()) {
						Map.Entry<String, RBMMovieInfo> pairs = (Map.Entry<String, RBMMovieInfo>)itMovie.next();
						System.out.println(pairs.getKey() + " = " + pairs.getValue());
						itMovie.remove(); // avoids a ConcurrentModificationException
					}
					System.out.println("========numUsers "+this.userList.size());
					System.out.println("========userMaxIdx "+this.userMaxIdx);
					System.out.println("========userHashMap size "+this.userHashMap.size());
					Iterator<Entry<String, RBMUserInfo>> itUser = this.userHashMap.entrySet().iterator();
					while (itUser.hasNext()) {
						Map.Entry<String, RBMUserInfo> pairs = (Map.Entry<String, RBMUserInfo>)itUser.next();
						System.out.println(pairs.getKey() + " = " + pairs.getValue());
						itUser.remove(); // avoids a ConcurrentModificationException
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private int getMovieIdx (String movieName) {
		// put Movie into List and HashMap, as well as return movie index
		if (!GlobalVariables.RBM_MOVIE_HASHMAP.containsKey(movieName)) {
			GlobalVariables.RBM_MOVIE_HASHMAP.put(
					movieName, 
					new RBMMovieInfo(++GlobalVariables.RBM_MOVIE_MAX_IDX, 1));
			GlobalVariables.RBM_MOVIE_LIST.add(movieName);
			return GlobalVariables.RBM_MOVIE_MAX_IDX;
		}

		int movieIdx = GlobalVariables.RBM_MOVIE_HASHMAP.get(movieName).getMovieIdx();
		int movieCount = GlobalVariables.RBM_MOVIE_HASHMAP.get(movieName).getMovieCount();
		// update movie count
		GlobalVariables.RBM_MOVIE_HASHMAP.put(
				movieName, 
				new RBMMovieInfo(movieIdx, movieCount+1));
		return movieIdx;
	}

	private void storeTripleIntoRBMDataMatix(String userid, String movieName, String rating, boolean isForTrain) {
		int movieIdx = this.getMovieIdx(movieName);


		if (!this.userHashMap.containsKey(userid)) {
			HashMap<Integer, Integer> ratedMovies = new HashMap<Integer,Integer>();
			ratedMovies.put(
					movieIdx,  // movieIdx
					Integer.valueOf(rating) // softmaxIdx(rating)
					);
			// Sentiment(5-point scale/5-way softmax):  
			// "Very negative(0)", "Negative(1)", "Neutral(2)", "Positive(3)", "Very positive(4)"
			this.userHashMap.put(
					userid, 
					new RBMUserInfo(
							++this.userMaxIdx,  //userIdx
							ratedMovies  // contains movieIdx and softmaxIdx(rating)
							)
					);
			this.userList.add(userid);
		} else {
			HashMap<Integer, Integer> ratedMovies = this.userHashMap.get(userid).getRatedMovies();
			ratedMovies.put(  // update this movie or add new movie
					movieIdx,  // movieIdx
					Integer.valueOf(rating) // softmaxIdx(rating)
					);
			// no matter whether this movie has been rated by this user or not
			// Since, if this user rated this movie before, we only need to update this movie rating with this user's most sentiment to this movie
		}
	}
}
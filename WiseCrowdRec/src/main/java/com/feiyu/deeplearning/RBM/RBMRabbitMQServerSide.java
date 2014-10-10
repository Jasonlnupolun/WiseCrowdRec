package com.feiyu.deeplearning.RBM;

import java.io.IOException;
import java.util.HashMap;

import com.feiyu.semanticweb.freebase.GetActorMovieGenreSubgraphVectorNEdge;
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
 * 
 * @author feiyu
 *
 */

public class RBMRabbitMQServerSide {
	private final static String QUEUE_NAME = GlobalVariables.RABBITMQ_QUEUE_NAME_RBMDATACOLLECTION;

	public void rbmRabbitMQServerSide(String threadName) throws ParseException {
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
														jsonTipleUCR.get("rating").toString());
						System.out.println(jsonTipleUCR.get("userid")+" -- "+JsonPath.read(result,"$.name").toString()+" -- "+jsonTipleUCR.get("rating"));
					}
				} catch (ShutdownSignalException | ConsumerCancelledException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					System.out.println(threadName+ " Rabbitmq is interrupted at " + System.currentTimeMillis());
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
	
	private void storeTripleIntoRBMDataMatix(String userid, String movieName, String rating) {
		int movieIdx = this.getMovieIdx(movieName);
		
		if (!GlobalVariables.RBM_USER_HASHMAP.containsKey(userid)) {
			HashMap<Integer, Integer> ratedMovies = new HashMap<Integer,Integer>();
			ratedMovies.put(
					movieIdx,  // movieIdx
					Integer.valueOf(rating) // softmaxIdx(rating)
				);
			// Sentiment(5-point scale/5-way softmax):  
			// "Very negative(0)", "Negative(1)", "Neutral(2)", "Positive(3)", "Very positive(4)"
			GlobalVariables.RBM_USER_HASHMAP.put(
					userid, 
					new RBMUserInfo(
							++GlobalVariables.RBM_USER_MAX_IDX,  //userIdx
							ratedMovies  // contains movieIdx and softmaxIdx(rating)
							)
					);
			GlobalVariables.RBM_USER_LIST.add(userid);
		} else {
			HashMap<Integer, Integer> ratedMovies = GlobalVariables.RBM_USER_HASHMAP.get(userid).getRatedMovies();
			ratedMovies.put(  // update this movie or add new movie
					movieIdx,  // movieIdx
					Integer.valueOf(rating) // softmaxIdx(rating)
					);
			// no matter whether this movie has been rated by this user or not
			// Since, if this user rated this movie before, we only need to update this movie rating with this user's most sentiment to this movie
		}
	}
}
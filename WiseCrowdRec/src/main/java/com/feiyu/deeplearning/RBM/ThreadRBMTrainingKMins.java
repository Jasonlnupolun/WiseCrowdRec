package com.feiyu.deeplearning.RBM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.feiyu.springmvc.model.RBMClientWeightMatixForPredict;
import com.feiyu.springmvc.model.RBMDataQueueElementInfo;
import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.springmvc.model.RBMUserInfo;
import com.feiyu.springmvc.model.Tuple;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;
import com.jayway.jsonpath.JsonPath;
import com.omertron.themoviedbapi.MovieDbException;

/**
 * @author feiyu
 */

public class ThreadRBMTrainingKMins  implements Runnable {
	private static Logger log = Logger.getLogger(ThreadRBMTrainingKMins.class.getName());
	private String threadName;
	private RBMDataQueueElementInfo currentData;
	private int numMovies; // move list size changes in each RBM Model 
	private boolean successfullyTrainedThisRBM;

	private JSONParser parser = new JSONParser();
	private JSONObject d3Data = new JSONObject();
	private JSONArray d3Vertices = new JSONArray();
	private JSONArray d3Edges = new JSONArray();
	private JSONObject d3Vertex;	
	private JSONObject d3Edge;	
	private int movieMaxIdx = -1;
	private int genreMaxIdx = -1;
	private HashMap<String, Integer> genreIdxJson = new HashMap<String, Integer>();

	public ThreadRBMTrainingKMins(String threadName , RBMDataQueueElementInfo currentData) {
		this.threadName = threadName;
		this.currentData = new RBMDataQueueElementInfo(
				currentData.getKthRBM(),
				new HashMap<String, RBMMovieInfo>(currentData.getMovieHashMap()),
				new HashMap<String, RBMUserInfo>(currentData.getUserHashMapTrain()),
				new HashMap<String, RBMUserInfo>(currentData.getUserHashMapTest()));

		this.numMovies = currentData.getMovieHashMap().size();
		this.successfullyTrainedThisRBM = false;
		GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM = new HashMap<Integer, Integer>();
		log.info("Creating " +  this.threadName + " at " +System.currentTimeMillis());
	}

	public void run() {
		try {
			this.showTrainingTestingData();

			// after getting the training and the testing data for current RBM
			// show Movies and their Genres to Client immediately 
			// In which, don't show those Genres this Client dislikes
			// don't show those Movies this Client dislikes and those Movies related to those Genres this Client dislikes
			this.showMoviesGenresToClient_currentRBM();

			// Train RBM and Show movies to the Client at the same time
			this.trainRBM();
			Thread.sleep(1);
		} catch (InterruptedException e) {
			log.info(this.threadName+" is interrupted at " + System.currentTimeMillis());
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	private void trainRBM() throws IOException {
		// create a file for collecting the RMSE of Epochs from 1 to numEpochs
		String RMSEfileName = "RMSEByEpochs_TrainedRBMModel+"+this.currentData.getKthRBM();
		File file = new File("/Library/Tomcat/logs/"+RMSEfileName+".txt");
//		File file = new File("src/main/resources/RBM/"+RMSEfileName+".txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		// Train Model
		if (GlobalVariables.RBM_DRAW_CHART) {
			for (int i=1; i<=GlobalVariables.RBM_NUM_EPOCHS; i++) {
				this.trainRBMWithCertainEpoch(i, bw);
			}
			bw.close();
			log.info("Saved rmse-by-epoch to src/main/resources/"+RMSEfileName+".txt!!");
		} else {
			this.trainRBMWithCertainEpoch(GlobalVariables.RBM_NUM_EPOCHS, bw);
		}
	}

	private void trainRBMWithCertainEpoch(int epochs, BufferedWriter bw) throws IOException {			
		RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax = new RestrictedBoltzmannMachinesWithSoftmax(
				this.numMovies, GlobalVariables.RBM_SIZE_SOFTMAX, GlobalVariables.RBM_SIZE_HIDDEN_UNITS, 
				GlobalVariables.RBM_LEARNING_RATE, epochs, bw
				);
		// Train RBM
		this.trainOrTestRBM(rbmSoftmax, true);

		// get trained RBM weight matrix
		this.updateClientWeightMatixForPrediction(rbmSoftmax);

		// Test RBM
		this.trainOrTestRBM(rbmSoftmax, false);

		// Show results
		//		rbmSoftmax.getTrainedWeightMatrix_RBM();
		rbmSoftmax.getRMSEOfRBMModel();
	}

	private void trainOrTestRBM(RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax, boolean isForTrain) {
		Iterator<Entry<String, RBMUserInfo>> itUser;
		if (isForTrain) {
			this.successfullyTrainedThisRBM = false;
			itUser = this.currentData.getUserHashMapTrain().entrySet().iterator();
			log.info("~~~~~~~~~~~Train RBM "+itUser.hasNext());
		} else {
			itUser = this.currentData.getUserHashMapTest().entrySet().iterator();
			log.info("~~~~~~~~~~~Test RBM "+itUser.hasNext());
		}
		while (itUser.hasNext()) {
			Map.Entry<String, RBMUserInfo> pairs = (Map.Entry<String, RBMUserInfo>)itUser.next();
			log.debug(" ++++++++ "+pairs.getKey() + " = " + pairs.getValue());
//			itUser.remove(); // avoids a ConcurrentModificationException

			ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices = new ArrayList<Tuple<Integer,Integer>>();
			Iterator<Entry<Integer,Integer>> itMovie = pairs.getValue().getRatedMovies().entrySet().iterator();
			while (itMovie.hasNext()) {
				Map.Entry<Integer,Integer> movieRating = (Map.Entry<Integer,Integer>)itMovie.next();
				log.debug(" ++++++++ "+movieRating.getKey() + " = " + movieRating.getValue());
//				itMovie.remove(); // avoids a ConcurrentModificationException

				// for each movie
				ratedMoviesIndices.add(new Tuple<Integer, Integer>(movieRating.getKey(),movieRating.getValue()));
			}
			// for each user
			if (isForTrain) {
				rbmSoftmax.trainRBMWeightMatrix(ratedMoviesIndices);
				this.successfullyTrainedThisRBM = true;
			} else {
				rbmSoftmax.predictUserPreference_VisibleToHiddenToVisible(ratedMoviesIndices);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void showMoviesGenresToClient_currentRBM() throws IOException, ParseException {
		log.info("^^^^^^Client disliked genres:");
		for (String g : GlobalVariables.RBM_CLIENT_DISLIKED_GENRES) {
			log.info("Client disliked the genre: "+ g);
		}

		log.info("^^^^^^Client disliked movies:");
		for (String m : GlobalVariables.RBM_CLIENT_DISLIKED_MOVIES) {
			log.info("Client disliked the movie: "+ m);
		}

		// send message to the RabbitMQ queue
		log.info("^^^^^^show movie list to client");
		log.info("movieList " + this.currentData.getMovieHashMap().toString());
		Iterator<Entry<String, RBMMovieInfo>> itMovie = this.currentData.getMovieHashMap().entrySet().iterator();
		movieloop: while (itMovie.hasNext()) {
			Map.Entry<String, RBMMovieInfo> pairs = (Map.Entry<String, RBMMovieInfo>)itMovie.next();
			//			itMovie.remove(); // avoids a ConcurrentModificationException

			String movieName = pairs.getKey();
			log.info(">>>>> movieName "+movieName);
			// if Client doesn't like this movie
//			if (!this.currentData.getMovieHashMap().containsKey(movieName)) {
//				log.info("&&&&& "+movieName+" not in MovieHashMap");
//				//				GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(this.currentData.getMovieHashMap().get(movieName).getMovieIdx(), 0); 
//				continue;
//			}
			if (GlobalVariables.RBM_CLIENT_DISLIKED_MOVIES.contains(movieName))  {
				GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(
						this.currentData.getMovieHashMap().get(movieName).getMovieIdx(), 0); // 0 client dislikes this movie
				continue;
			}

			// Client might like this movie, then find the genres of this movie
			JSONObject response = (JSONObject)parser.parse(
					GlobalVariables.FREEBASE_GET_ACTOR_MOVIES.getFilmGenresByMovieId(
							pairs.getValue().getMid(), 
							false
							));
			JSONArray results = (JSONArray)response.get("result");
			//			for (Object result : results) { // since using Mid to search in Freebase, results.size() = 1 
			String[] genres = JsonPath.read(results.get(0),"$./film/film/genre").toString()
					.replace("[\"", "")
					.replace("\"]", "")
					.split("\",\"");

			if (this.movieContainsAtLeastOneGenreClientDislike(genres)) {
//				if (!this.currentData.getMovieHashMap().containsKey(movieName)) {
//					log.info("&&&&& "+movieName+" not in MovieHashMap");
//					//				GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(this.currentData.getMovieHashMap().get(movieName).getMovieIdx(), 0); 
//					continue movieloop;
//				}
				GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(
						this.currentData.getMovieHashMap().get(movieName).getMovieIdx(), 0); // 0 client dislikes one of the genres of this movie
				continue movieloop;
			} 
			//			}

			// then this client might like this movie and its genres 
//			if (!this.currentData.getMovieHashMap().containsKey(movieName)) {
//				log.info("&&&&& "+movieName+" not in MovieHashMap");
//				//				GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(this.currentData.getMovieHashMap().get(movieName).getMovieIdx(), 0); 
//				continue movieloop;
//			}
			GlobalVariables.RBM_CLIENT_RATED_MOVIES_CUR_RBM.put(
					this.currentData.getMovieHashMap().get(movieName).getMovieIdx(), 4); // 4 client might like this movie
			// later this client clicks and shows disliking this movie, then change to 0
			log.info("Rec --> Put movie " + movieName +" into JSONArray d3Vertices..");
			this.d3Vertex = new JSONObject();	
			this.d3Vertex.put("name", movieName.substring(
					0, movieName.length()<3?movieName.length():3));
			this.d3Vertex.put("fullname", movieName);
			this.d3Vertex.put("entity", "movie");

			++this.movieMaxIdx;
			this.d3Vertices.add(this.d3Vertex);

			this.genreMaxIdx = this.d3Vertices.size()-1;	
			for (String genre : genres) {
				int curGenreIdxInJson = -1;
				if (!this.genreIdxJson.containsKey(genre)) {
					log.info("Rec --> Put genre " + genre +" into JSONArray d3Vertices..");

					this.d3Vertex = new JSONObject();	
					this.d3Vertex.put("name", genre.substring(
							0, genre.length()<3?genre.length():3));
					this.d3Vertex.put("fullname", genre);
					this.d3Vertex.put("entity", "genre");

					curGenreIdxInJson = ++this.genreMaxIdx;
					this.d3Vertices.add(this.d3Vertex);

					this.genreIdxJson.put(genre, curGenreIdxInJson);
				} else {
					curGenreIdxInJson = this.genreIdxJson.get(genre);
				}

				log.info("Rec --> Put movie-genre link into JSONArray d3Edges..");
				this.d3Edge= new JSONObject();	
				this.d3Edge.put("source", this.movieMaxIdx);
				d3Edge.put("target", curGenreIdxInJson);
				d3Edge.put("type", "linkmoviegenre");
				d3Edges.add(d3Edge);
			}
			this.movieMaxIdx = this.d3Vertices.size() - 1;
		}

		d3Data.put("nodes", d3Vertices);
		d3Data.put("links", d3Edges);
		String d3DataString = d3Data.toString();
		log.info(" [x] RABBITMQ_QUEUE_NAME_SPARK: Message Sent to queue buffer: "+ d3DataString);
		GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_SPARK, null, d3DataString.getBytes());
	}

	private boolean movieContainsAtLeastOneGenreClientDislike(String[] genres) {
		for (String genre : genres) {
			log.debug("movieContainsAtLeastOneGenreClientDislike genre: "+genre);
			if (GlobalVariables.RBM_CLIENT_DISLIKED_GENRES.contains(genre)) {
				return true;
			}
		}
		return false;
	}

	private void updateClientWeightMatixForPrediction(RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax) {		
		if (!this.successfullyTrainedThisRBM ) {
			log.info("Didn't update the client weight matix for prediction, "
					+"cuz this newly weight matrix didn't trained curretly or it's null!!");
		} else if (GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT == null 
				|| GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getKthRBM() < this.currentData.getKthRBM()) {
			double[][][] trainedMwrbm = new double[this.numMovies+1][GlobalVariables.RBM_SIZE_HIDDEN_UNITS+1][GlobalVariables.RBM_SIZE_SOFTMAX];
			double[][][] curMwrbm = rbmSoftmax.getTrainedWeightMatrix_RBM();
			for (int x=0; x<this.numMovies+1; x++) {
				for (int y=0; y<GlobalVariables.RBM_SIZE_HIDDEN_UNITS+1; y++) {
					for (int z=0; z<GlobalVariables.RBM_SIZE_SOFTMAX; z++) {
						trainedMwrbm[x][y][z] = curMwrbm[x][y][z];
					}
				}
			}
			GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT = new RBMClientWeightMatixForPredict(
					this.currentData.getKthRBM(),
					System.currentTimeMillis(),
					new HashMap<String, RBMMovieInfo>(this.currentData.getMovieHashMap()),
					trainedMwrbm
					);

			log.info("********Get client weight matrix for prediction..");
			rbmSoftmax.printMatrix(this.numMovies+1, GlobalVariables.RBM_SIZE_HIDDEN_UNITS+1, GlobalVariables.RBM_SIZE_SOFTMAX, "clientWeightMatrix_RBM");
		} else if (GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getKthRBM() > this.currentData.getKthRBM()) {
			log.info("Didn't update the client weight matix for prediction, "
					+ "cuz it took long time for training the weight matirx for the "+this.currentData.getKthRBM()+"-th RBM");
		} else {
			log.error("updateClientWeightMatixForPrediction: error happened!!");
		}
	}

	private void showTrainingTestingData() {
		// For test
		log.info("\n>>>>>>>>mmmmmmmKthRbm "+  this.currentData.getKthRBM());

		log.info(">>>>>>>>DataQueueSize "+GlobalVariables.RBM_DATA_QUEUE.size());
		for (RBMDataQueueElementInfo item : GlobalVariables.RBM_DATA_QUEUE) {
			log.info(">>>>>>>>"+item);
		}

		log.info(">>>>>>>>numMovies "+ this.currentData.getMovieHashMap().size());
		Iterator<Entry<String, RBMMovieInfo>> itMovie = this.currentData.getMovieHashMap().entrySet().iterator();
		while (itMovie.hasNext()) {
			Map.Entry<String, RBMMovieInfo> pairs = (Map.Entry<String, RBMMovieInfo>)itMovie.next();
			log.info(">>>>>>>>"+pairs.getKey() + " = " + pairs.getValue());
//			itMovie.remove(); // avoids a ConcurrentModificationException
		}
		log.info(">>>>>>>>numUsersForTrain "+ this.currentData.getUserHashMapTrain().size());
		Iterator<Entry<String, RBMUserInfo>> itUserTrain = this.currentData.getUserHashMapTrain().entrySet().iterator();
		while (itUserTrain.hasNext()) {
			Map.Entry<String, RBMUserInfo> pairs = (Map.Entry<String, RBMUserInfo>)itUserTrain.next();
			log.info(">>>>>>>>"+pairs.getKey() + " = " + pairs.getValue());
//			itUserTrain.remove(); // avoids a ConcurrentModificationException
		}
		log.info(">>>>>>>>numUsersForTest "+ this.currentData.getUserHashMapTest().size());
		Iterator<Entry<String, RBMUserInfo>> itUserTest = this.currentData.getUserHashMapTest().entrySet().iterator();
		while (itUserTest.hasNext()) {
			Map.Entry<String, RBMUserInfo> pairs = (Map.Entry<String, RBMUserInfo>)itUserTest.next();
			log.info(">>>>>>>>"+pairs.getKey() + " = " + pairs.getValue());
//			itUserTest.remove(); // avoids a ConcurrentModificationException
		}
	}

	public static void main(String[] argv) throws IOException, ParseException, MovieDbException {

		InitializeWCR initWCR = new InitializeWCR();
		initWCR.getWiseCrowdRecConfigInfo();
		initWCR.themoviedbOrgInitial();
		initWCR.getFreebaseInfo();

		HashMap<String, RBMMovieInfo> movie = new HashMap<String, RBMMovieInfo>();
		HashMap<String, RBMUserInfo> userTrain = new HashMap<String, RBMUserInfo>();
		HashMap<String, RBMUserInfo> userTest = new HashMap<String, RBMUserInfo>();
		HashMap<Integer,Integer> rate;


		movie.put("The Weekend", new RBMMovieInfo(
				0, "/m/0bd5kxs", 1
				));
		movie.put("Superman Returns", new RBMMovieInfo(
				1, "/m/044g_k", 2
				));

		rate = new HashMap<Integer,Integer>();
		rate.put(0, 1);
		userTrain.put("121", new RBMUserInfo(
				0, new HashMap<Integer,Integer>(rate)
				));

		rate = new HashMap<Integer,Integer>();
		rate.put(0, 1);
		rate.put(1, 4);
		userTrain.put("111", new RBMUserInfo(
				0, new HashMap<Integer,Integer>(rate)
				));

		rate = new HashMap<Integer,Integer>();
		rate.put(1, 1);
		userTrain.put("11", new RBMUserInfo(
				0, new HashMap<Integer,Integer>(rate)
				));

		rate = new HashMap<Integer,Integer>();
		rate.put(1, 3);
		userTest.put("113", new RBMUserInfo(
				0, new HashMap<Integer,Integer>(rate)
				));

		rate = new HashMap<Integer,Integer>();
		rate.put(0, 4);
		userTest.put("116", new RBMUserInfo(
				0, new HashMap<Integer,Integer>(rate)
				));

		RBMDataQueueElementInfo curData = new RBMDataQueueElementInfo (
				0,
				new HashMap<String, RBMMovieInfo>(movie),
				new HashMap<String, RBMUserInfo>(userTrain),
				new HashMap<String, RBMUserInfo>(userTest)
				);

		ThreadRBMTrainingKMins t = new ThreadRBMTrainingKMins("testThread",
				new RBMDataQueueElementInfo(
						curData.getKthRBM(),
						new HashMap<String, RBMMovieInfo>(curData.getMovieHashMap()),
						new HashMap<String, RBMUserInfo>(curData.getUserHashMapTrain()),
						new HashMap<String, RBMUserInfo>(curData.getUserHashMapTest()))
				);
		t.showTrainingTestingData();
		t.showMoviesGenresToClient_currentRBM();
	}
}

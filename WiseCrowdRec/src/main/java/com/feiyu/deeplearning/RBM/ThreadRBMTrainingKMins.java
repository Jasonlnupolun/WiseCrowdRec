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
import org.json.simple.parser.ParseException;

import com.feiyu.springmvc.model.RBMClientWeightMatixForPredict;
import com.feiyu.springmvc.model.RBMDataQueueElementInfo;
import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.springmvc.model.RBMUserInfo;
import com.feiyu.springmvc.model.Tuple;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;
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
	private String RMSEfileName;
	private BufferedWriter bw;

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
			this.printTrainingTestingData();

			// Train RBM 
			this.trainRBM();

			Thread.sleep(1);
		} catch (InterruptedException e) {
			log.info(this.threadName+" is interrupted at " + System.currentTimeMillis());
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	private void createFileForRMSE() throws IOException {
		// create a file for collecting the RMSE of Epochs from 1 to numEpochs
		this.RMSEfileName = "RMSEByEpochs_TrainedRBMModel+"+this.currentData.getKthRBM();
		File file = new File("/Library/Tomcat/logs/"+RMSEfileName+".txt");
		//		File file = new File("src/main/resources/RBM/"+RMSEfileName+".txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		bw = new BufferedWriter(fw);

	}

	private void trainRBM() throws ParseException, IOException {
		this.createFileForRMSE();

		// do not show Data & trained Weight Matrix to Client 
		// when Epoch for RBM is from 1 to GlobalVariables.RBM_NUM_EPOCHS-1
		if (GlobalVariables.RBM_DRAW_CHART) {
			for (int i=1; i<GlobalVariables.RBM_NUM_EPOCHS; i++) {
				this.trainRBMWithCertainEpoch(i, false);
			}
		} 

		// show Data & trained Weight Matrix to Client
		// when Epoch for RBM is GlobalVariables.RBM_NUM_EPOCHS
		this.trainRBMWithCertainEpoch(GlobalVariables.RBM_NUM_EPOCHS, true);

		bw.close();
		log.info("Saved rmse-by-epoch to /Library/Tomcat/logs/"+RMSEfileName+".txt!!");
		//		log.info("Saved rmse-by-epoch to src/main/resources/"+RMSEfileName+".txt!!");
	}

	private void trainRBMWithCertainEpoch(int epochs, boolean showDataNWeightMatrix2Client) throws IOException, ParseException {			
		RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax = new RestrictedBoltzmannMachinesWithSoftmax(
				this.numMovies, GlobalVariables.RBM_SIZE_SOFTMAX, GlobalVariables.RBM_SIZE_HIDDEN_UNITS, 
				GlobalVariables.RBM_LEARNING_RATE, epochs, bw
				);
		// Train RBM
		this.trainOrTestRBM(rbmSoftmax, true);

		if (showDataNWeightMatrix2Client) {
			// 1) updata the Client Weight Matrix for prediction
			this.updateClientWeightMatixForPrediction(rbmSoftmax);
			// Another thread: 
			// 2) show movies & genres to client 
			this.showData2Client();
		}

		// Test RBM
		this.trainOrTestRBM(rbmSoftmax, false);

		// Show test results in RMSE
		// rbmSoftmax.getTrainedWeightMatrix_RBM();
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

	private void updateClientWeightMatixForPrediction(RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax) {	
		double[][][] trainedMwrbm = new double[this.numMovies+1][GlobalVariables.RBM_SIZE_HIDDEN_UNITS+1][GlobalVariables.RBM_SIZE_SOFTMAX];

		if (!this.successfullyTrainedThisRBM ) {
			log.info("Didn't update the client weight matix for prediction, "
					+"cuz this newly weight matrix didn't trained curretly or it's null!!");
			trainedMwrbm = null;
		} else {
			log.info("********Get client weight matrix for prediction..");
			double[][][] curMwrbm = rbmSoftmax.getTrainedWeightMatrix_RBM();
			for (int x=0; x<this.numMovies+1; x++) {
				for (int y=0; y<GlobalVariables.RBM_SIZE_HIDDEN_UNITS+1; y++) {
					String str = "";
					for (int z=0; z<GlobalVariables.RBM_SIZE_SOFTMAX; z++) {
						trainedMwrbm[x][y][z] = curMwrbm[x][y][z];
						str += " "+ String.valueOf(trainedMwrbm[x][y][z]);
					}
					log.info(str+" softmax "); 
				}
				log.info(" layer "); 
			}
			//			rbmSoftmax.printMatrix(this.numMovies+1, GlobalVariables.RBM_SIZE_HIDDEN_UNITS+1, GlobalVariables.RBM_SIZE_SOFTMAX, "clientWeightMatrix_RBM");
		}

		GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT = new RBMClientWeightMatixForPredict(
				this.currentData.getKthRBM(),
				System.currentTimeMillis(),
				new HashMap<String, RBMMovieInfo>(this.currentData.getMovieHashMap()),
				trainedMwrbm,
				this.successfullyTrainedThisRBM
				);
	}

	private void showData2Client() {
		String trainRBMsName= "ThreadRBMShowDataToClientPMins"; 
		Runnable showData2ClientRunnable= new ThreadRBMShowDataToClientPMins(trainRBMsName);
		Thread showData2ClientThread = new Thread(showData2ClientRunnable);

		log.info("Starting "+ trainRBMsName +" at time "+System.currentTimeMillis());
		showData2ClientThread.start();

		log.info(trainRBMsName+ " ends at "+System.currentTimeMillis());	
	}

	private void printTrainingTestingData() {
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
		t.printTrainingTestingData();
	}
}

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

import com.feiyu.spark.SparkTwitterStreaming;
import com.feiyu.springmvc.model.RBMClientWeightMatixForPredict;
import com.feiyu.springmvc.model.RBMDataQueueElementInfo;
import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.springmvc.model.RBMUserInfo;
import com.feiyu.springmvc.model.Tuple;
import com.feiyu.utils.GlobalVariables;

/**
 * @author feiyu
 */

public class ThreadRBMTrainingKMins  implements Runnable {
	private static Logger log = Logger.getLogger(SparkTwitterStreaming.class.getName());
	private String threadName;
	private int sizeSoftmax; 
	private int sizeHiddenUnits;
	private double learningRate;
	private int numEpochs;
	private boolean drawChart;
	private RBMDataQueueElementInfo currentData;
	private int numMovies; // move list size changes in each RBM Model 
	private boolean successfullyTrainedThisRBM;

	public ThreadRBMTrainingKMins(
			String threadName, int sizeSoftmax, int sizeHiddenUnits,
			double learningRate, int numEpochs, boolean drawChart, RBMDataQueueElementInfo currentData) {
		this.threadName = threadName;
		this.sizeSoftmax = sizeSoftmax;
		this.sizeHiddenUnits = sizeHiddenUnits; 
		this.learningRate =learningRate;
		this.numEpochs = numEpochs;
		this.drawChart = drawChart; 
		this.currentData = new RBMDataQueueElementInfo(
				currentData.getKthRBM(),
				new HashMap<String, RBMMovieInfo>(currentData.getMovieHashMap()),
				new HashMap<String, RBMUserInfo>(currentData.getUserHashMapTrain()),
				new HashMap<String, RBMUserInfo>(currentData.getUserHashMapTest()));

		this.numMovies = currentData.getMovieHashMap().size();
		this.successfullyTrainedThisRBM = false;
		log.info("Creating " +  this.threadName + " at " +System.currentTimeMillis());
		this.showTrainingTestingData(currentData);
	}

	public void run() {
		try {
			this.trainRBM();
			Thread.sleep(1);
		} catch (InterruptedException e) {
			log.info(this.threadName+" is interrupted at " + System.currentTimeMillis());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void trainRBM() throws IOException {
		// create a file for collecting the RMSE of Epochs from 1 to numEpochs
		String RMSEfileName = "RMSEByEpochs_TrainedRBMModel+"+this.currentData.getKthRBM();
		File file = new File("src/main/resources/RBM/"+RMSEfileName+".txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		// Train Model
		if (this.drawChart) {
			for (int i=1; i<=this.numEpochs; i++) {
				this.trainRBMWithCertainEpoch(i, bw);
			}
			bw.close();
			log.info("Saved rmse-by-epoch to src/main/resources/"+RMSEfileName+".txt!!");
		} else {
			this.trainRBMWithCertainEpoch(this.numEpochs, bw);
		}
	}

	private void trainRBMWithCertainEpoch(int epochs, BufferedWriter bw) throws IOException {			
		RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax = new RestrictedBoltzmannMachinesWithSoftmax(
				this.numMovies, this.sizeSoftmax, this.sizeHiddenUnits, this.learningRate, epochs, bw
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
			itUser.remove(); // avoids a ConcurrentModificationException

			ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices = new ArrayList<Tuple<Integer,Integer>>();
			Iterator<Entry<Integer,Integer>> itMovie = pairs.getValue().getRatedMovies().entrySet().iterator();
			while (itMovie.hasNext()) {
				Map.Entry<Integer,Integer> movieRating = (Map.Entry<Integer,Integer>)itMovie.next();
				log.debug(" ++++++++ "+movieRating.getKey() + " = " + movieRating.getValue());
				itMovie.remove(); // avoids a ConcurrentModificationException

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
		if (!this.successfullyTrainedThisRBM ) {
			log.debug("Didn't update the client weight matix for prediction, "
					+"cuz this newly weight matrix didn't trained curretly or it's null!!");
		} else if (GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT == null 
				|| GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getKthRBM() < this.currentData.getKthRBM()) {
			double[][][] trainedMwrbm = new double[this.numMovies+1][this.sizeHiddenUnits+1][this.sizeSoftmax];
			double[][][] curMwrbm = rbmSoftmax.getTrainedWeightMatrix_RBM();
			for (int x=0; x<this.numMovies+1; x++) {
				for (int y=0; y<this.sizeHiddenUnits+1; y++) {
					for (int z=0; z<this.sizeSoftmax; z++) {
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
		} else if (GlobalVariables.RBM_CLIENT_WEIGHTMATIX_FOR_PREDICT.getKthRBM() > this.currentData.getKthRBM()) {
			log.debug("Didn't update the client weight matix for prediction, "
					+ "cuz it took long time for training the weight matirx for the "+this.currentData.getKthRBM()+"-th RBM");
		} else {
			log.error("updateClientWeightMatixForPrediction: error happened!!");
		}
		log.info("********Get client weight matrix for prediction..");
		rbmSoftmax.printMatrix(this.numMovies+1, this.sizeHiddenUnits+1, this.sizeSoftmax, "clientWeightMatrix_RBM");
	}

	private void showTrainingTestingData(RBMDataQueueElementInfo currentData) {
		// For test
		log.info("\n>>>>>>>>mmmmmmmKthRbm "+ currentData.getKthRBM());

		log.info(">>>>>>>>DataQueueSize "+GlobalVariables.RBM_DATA_QUEUE.size());
		for (RBMDataQueueElementInfo item : GlobalVariables.RBM_DATA_QUEUE) {
			log.info(">>>>>>>>"+item);
		}

		log.info(">>>>>>>>numMovies "+ currentData.getMovieHashMap().size());
		Iterator<Entry<String, RBMMovieInfo>> itMovie = currentData.getMovieHashMap().entrySet().iterator();
		while (itMovie.hasNext()) {
			Map.Entry<String, RBMMovieInfo> pairs = (Map.Entry<String, RBMMovieInfo>)itMovie.next();
			log.info(">>>>>>>>"+pairs.getKey() + " = " + pairs.getValue());
			itMovie.remove(); // avoids a ConcurrentModificationException
		}
		log.info(">>>>>>>>numUsersForTrain "+ currentData.getUserHashMapTrain().size());
		Iterator<Entry<String, RBMUserInfo>> itUserTrain = currentData.getUserHashMapTrain().entrySet().iterator();
		while (itUserTrain.hasNext()) {
			Map.Entry<String, RBMUserInfo> pairs = (Map.Entry<String, RBMUserInfo>)itUserTrain.next();
			log.info(">>>>>>>>"+pairs.getKey() + " = " + pairs.getValue());
			itUserTrain.remove(); // avoids a ConcurrentModificationException
		}
		log.info(">>>>>>>>numUsersForTest "+ currentData.getUserHashMapTest().size());
		Iterator<Entry<String, RBMUserInfo>> itUserTest = currentData.getUserHashMapTrain().entrySet().iterator();
		while (itUserTest.hasNext()) {
			Map.Entry<String, RBMUserInfo> pairs = (Map.Entry<String, RBMUserInfo>)itUserTest.next();
			log.info(">>>>>>>>"+pairs.getKey() + " = " + pairs.getValue());
			itUserTest.remove(); // avoids a ConcurrentModificationException
		}
	}
}

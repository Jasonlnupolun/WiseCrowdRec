package com.feiyu.deeplearning.RBM;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.feiyu.spark.SparkTwitterStreaming;
import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.utils.GlobalVariables;

/**
 * @author feiyu
 */

public class GetTrainingTestingDataCurrentKMins {
	private static Logger log = Logger.getLogger(SparkTwitterStreaming.class.getName());

	public GetTrainingTestingDataCurrentKMins() {
		GlobalVariables.RBM_MOVIE_HASHMAP= new HashMap<String, RBMMovieInfo>();
		GlobalVariables.RBM_MOVIE_LIST= new ArrayList<String>();
		GlobalVariables.RBM_MOVIE_MAX_IDX = -1;
	}

	public void startRBMDataCollection(long durationTrain, long durationTest) {
		// Collecting Training and Testing Data, as well as push each train/test user-ratedMovies-List into DataQueue(FIFO)
		this.startCollectTrainingData(durationTrain);
		this.startCollectTestingData(durationTest);
	}	

	private void startCollectTrainingData(long duration) {
		log.info("\n------------>startCollectTrainingData");
		String trainingDataThreadName = "RBMTrainingDataCollectionThread"; 
		Runnable collectTrainingData = new ThreadRBMDataCollectionTrainXMins(trainingDataThreadName);
		Thread collectTrainingDataThread = new Thread(collectTrainingData);

		log.info("Starting "+ trainingDataThreadName+" at "+System.currentTimeMillis());
		collectTrainingDataThread.start();

		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			//			e.printStackTrace();
			log.info("startRBMDataCollection() sleep thread is interrupted at "+ System.currentTimeMillis());
		}

		collectTrainingDataThread.interrupt();
	}

	private void startCollectTestingData(long duration) {
		log.info("\n------------>startCollectTestingData");
		String testingDataThreadName = "RBMTestingDataCollectionThread"; 
		Runnable collectTestingData = new ThreadRBMDataCollectionTestYMins(testingDataThreadName);
		Thread collectTestingDataThread = new Thread(collectTestingData);

		log.info("Starting "+ testingDataThreadName +" at "+System.currentTimeMillis());
		collectTestingDataThread.start();

		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			//					e.printStackTrace();
			log.info("startRBMDataCollection() sleep thread is interrupted at "+System.currentTimeMillis());
		}

		collectTestingDataThread.interrupt();
	}

	public static void main(String[] argv) {
		GetTrainingTestingDataCurrentKMins getData = new GetTrainingTestingDataCurrentKMins();
		getData.startRBMDataCollection(8000, 2000);
	}
}

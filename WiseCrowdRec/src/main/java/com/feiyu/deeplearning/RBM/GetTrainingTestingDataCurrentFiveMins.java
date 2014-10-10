package com.feiyu.deeplearning.RBM;

import java.util.ArrayList;
import java.util.HashMap;

import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.springmvc.model.RBMUserInfo;
import com.feiyu.utils.GlobalVariables;

/**
 * @author feiyu
 */

public class GetTrainingTestingDataCurrentFiveMins {
	
	public GetTrainingTestingDataCurrentFiveMins() {
		GlobalVariables.RBM_USER_HASHMAP = new HashMap<String, RBMUserInfo>();
		GlobalVariables.RBM_MOVIE_HASHMAP= new HashMap<String, RBMMovieInfo>();
		GlobalVariables.RBM_USER_LIST= new ArrayList<String>();
		GlobalVariables.RBM_MOVIE_LIST= new ArrayList<String>();
		GlobalVariables.RBM_USER_MAX_IDX = -1;
		GlobalVariables.RBM_MOVIE_MAX_IDX = -1;
	}
	
	public void startRBMDataCollection(long durationTrain, long durationTest) {
		this.startCollectTrainingData(durationTrain);
		this.startCollectTestingData(durationTest);
	}	
	
	private void startCollectTrainingData(long duration) {
		System.out.println("\n------------>startCollectTrainingData");
		String trainingDataThreadName = "RBMTrainingDataCollectionThread"; 
		Runnable collectTrainingData = new RBMTrainingDataCollectionThread(trainingDataThreadName);
		Thread collectTrainingDataThread = new Thread(collectTrainingData);

		System.out.println("Starting "+ trainingDataThreadName+" at "+System.currentTimeMillis());
		collectTrainingDataThread.start();

		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			//			e.printStackTrace();
			System.out.println("startRBMDataCollection() sleep thread is interrupted at "+ System.currentTimeMillis());
		}

		collectTrainingDataThread.interrupt();
	}

	private void startCollectTestingData(long duration) {
		System.out.println("\n------------>startCollectTestingData");
		String testingDataThreadName = "RBMTestingDataCollectionThread"; 
		Runnable collectTestingData = new RBMTestingDataCollectionThread(testingDataThreadName);
		Thread collectTestingDataThread = new Thread(collectTestingData);

		System.out.println("Starting "+ testingDataThreadName +" at "+System.currentTimeMillis());
		collectTestingDataThread.start();
		
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			//					e.printStackTrace();
			System.out.println("startRBMDataCollection() sleep thread is interrupted at "+System.currentTimeMillis());
		}
		
		collectTestingDataThread.interrupt();
	}

	public static void main(String[] argv) {
		GetTrainingTestingDataCurrentFiveMins getData = new GetTrainingTestingDataCurrentFiveMins();
		getData.startRBMDataCollection(8000, 2000);
	}
}
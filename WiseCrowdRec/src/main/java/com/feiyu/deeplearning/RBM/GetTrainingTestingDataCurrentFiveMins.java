package com.feiyu.deeplearning.RBM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.springmvc.model.RBMUserInfo;
import com.feiyu.springmvc.model.RBMUserMovieRatingTriple;

public class GetTrainingTestingDataCurrentFiveMins {
	private HashMap<String, RBMUserInfo> userHashMap = new HashMap<String, RBMUserInfo>();
	private HashMap<String, RBMMovieInfo> movieHashMap = new HashMap<String, RBMMovieInfo>();
	private ArrayList<String> userList = new ArrayList<String>();
	private ArrayList<String> movieList = new ArrayList<String>();
	private int userMaxIdx = 0;
	private int movieMaxIdx = 0;
	private Queue<RBMUserMovieRatingTriple> traingDataQueue = new LinkedList<RBMUserMovieRatingTriple>();
	private Queue<RBMUserMovieRatingTriple> testingDataQueue = new LinkedList<RBMUserMovieRatingTriple>();
	
	
	private void startCollectTrainingData(long duration) {
		System.out.println("\n------------>startCollectTrainingData");
		String TrainingDataThreadName = "RBMTrainingDataCollectionThread"; 
		Runnable collectTrainingData = new RBMTrainingDataCollectionThread(TrainingDataThreadName);
		Thread collectTrainingDataThread = new Thread(collectTrainingData);

		System.out.println("Starting "+ TrainingDataThreadName+" at time "+System.currentTimeMillis());
		collectTrainingDataThread.start();

		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			//			e.printStackTrace();
			System.out.println("startRBMDataCollection() sleep thread is interrupted at "+ System.currentTimeMillis());
		}

		System.out.println("end "+System.currentTimeMillis());
		collectTrainingDataThread.interrupt();
	}

	private void startCollectTestingData(long duration) {
		System.out.println("\n------------>startCollectTestingData");
		String TestingDataThreadName = "RBMTestingDataCollectionThread"; 
		Runnable collectTestingData = new RBMTestingDataCollectionThread(TestingDataThreadName);
		Thread collectTestingDataThread = new Thread(collectTestingData);

		System.out.println("Starting thread "+ TestingDataThreadName +" at time "+System.currentTimeMillis());
		collectTestingDataThread.start();
		
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			//					e.printStackTrace();
			System.out.println("startRBMDataCollection() sleep thread is interrupted at "+System.currentTimeMillis());
		}
		
		System.out.println("end "+System.currentTimeMillis());
		collectTestingDataThread.interrupt();
	}

	public void startRBMDataCollection(long durationTrain, long durationTest) {
		this.startCollectTrainingData(durationTrain);
		this.startCollectTestingData(durationTest);
	}	

	public static void main(String[] argv) {
		GetTrainingTestingDataCurrentFiveMins getData = new GetTrainingTestingDataCurrentFiveMins();
		getData.startRBMDataCollection(8000, 2000);

	}
}

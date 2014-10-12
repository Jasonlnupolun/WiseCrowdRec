package com.feiyu.deeplearning.RBM;

import java.util.HashMap;
import org.apache.log4j.Logger;

import com.feiyu.spark.SparkTwitterStreaming;
import com.feiyu.springmvc.model.RBMDataQueueElementInfo;
import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.springmvc.model.RBMUserInfo;
import com.feiyu.utils.GlobalVariables;

/**
 * @author feiyu
 */

public class ThreadRBMWholeProcess implements Runnable {
	private static Logger log = Logger.getLogger(SparkTwitterStreaming.class.getName());
	private String threadName;
	private long eachTrainDuration;
	private long eachTestDuration;
	private long dataCollectionDuration;
	private int sizeSoftmax;
	private int sizeHiddenUnits;
	private double learningRate;
	private int numEpochs;
	private boolean drawChart;

	public ThreadRBMWholeProcess(
			String threadName, long eachTrainDuration, long eachTestDuration, long dataCollectionDuration,
			int sizeSoftmax, int sizeHiddenUnits, double learningRate, int numEpochs, boolean drawChart) {
		this.threadName = threadName;
		this.eachTrainDuration = eachTrainDuration;
		this.eachTestDuration = eachTestDuration;
		this.dataCollectionDuration = dataCollectionDuration;
		this.sizeSoftmax = sizeSoftmax;
		this.sizeHiddenUnits = sizeHiddenUnits;
		this.learningRate = learningRate;
		this.numEpochs = numEpochs;
		this.drawChart = drawChart;
		log.info("Creating " +  this.threadName + " at time " +System.currentTimeMillis());
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		try {
			while (System.currentTimeMillis() - startTime < dataCollectionDuration) {
				// Data Collection
				GetTrainingTestingDataCurrentKMins getData = new GetTrainingTestingDataCurrentKMins();
				getData.startRBMDataCollection(this.eachTrainDuration, this.eachTestDuration);


				// Training RBMs
				log.info("\n------------>Start train RBMs");
				String trainRBMsName= "ThreadRBMTrainingKMins"; 
				RBMDataQueueElementInfo pollElement = GlobalVariables.RBM_DATA_QUEUE.poll();
				Runnable trainRBMsRunnable= new ThreadRBMTrainingKMins(
						trainRBMsName, this.sizeSoftmax, this.sizeHiddenUnits,
						this.learningRate, this.numEpochs, this.drawChart, 
						new RBMDataQueueElementInfo(
								GlobalVariables.KTH_RBM,
								new HashMap<String, RBMMovieInfo>(GlobalVariables.RBM_MOVIE_HASHMAP),
								new HashMap<String, RBMUserInfo>(pollElement.getUserHashMapTrain()),
								new HashMap<String, RBMUserInfo>(GlobalVariables.RBM_USER_HASHMAP))
						);
				Thread trainRBMsThread = new Thread(trainRBMsRunnable);

				log.info("Starting "+ trainRBMsName +" at time "+System.currentTimeMillis());
				trainRBMsThread.start();

				log.info(trainRBMsName+ " ends at "+System.currentTimeMillis());	
			}

			log.info(threadName+ " Rabbitmq is interrupted at " + System.currentTimeMillis());
			Thread.sleep(1);
		} catch (InterruptedException e) {
			log.info(this.threadName+" is interrupted at " + System.currentTimeMillis());
		}
	}
}

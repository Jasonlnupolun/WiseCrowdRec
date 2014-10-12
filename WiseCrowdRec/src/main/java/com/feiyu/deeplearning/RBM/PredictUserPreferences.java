package com.feiyu.deeplearning.RBM;

import org.apache.log4j.Logger;

import com.feiyu.spark.SparkTwitterStreaming;
import com.feiyu.utils.GlobalVariables;

/**
 * @author feiyu
 */

public class PredictUserPreferences {
	private static Logger log = Logger.getLogger(SparkTwitterStreaming.class.getName());
	private long overhead;
	private long eachTrainDuration;
	private long eachTestDuration;
	private long dataCollectionDuration;
	private int sizeSoftmax;
	private int sizeHiddenUnits;
	private double learningRate;
	private int numEpochs;
	private boolean drawChart;

	public PredictUserPreferences(long overhead,long eachTrainDuration, long eachTestDuration, long dataCollectionDuration,
			int sizeSoftmax, int sizeHiddenUnits, double learningRate, int numEpochs, boolean drawChart) {
		this.overhead = overhead;
		this.eachTrainDuration = eachTrainDuration;
		this.eachTestDuration = eachTestDuration;
		this.dataCollectionDuration = dataCollectionDuration;
		this.sizeSoftmax = sizeSoftmax;
		this.sizeHiddenUnits = sizeHiddenUnits;
		this.learningRate = learningRate;
		this.numEpochs = numEpochs;
		this.drawChart = drawChart;

		GlobalVariables.KTH_RBM = 0;
	}

	public void start() {
		log.info("\n------------>Start Collecting Data");
		String rbmWholeProcessThreadName = "rbmWholeProcessThread"; 
		Runnable rbmWholeProcessRunnable = new ThreadRBMWholeProcess(
				rbmWholeProcessThreadName, this.eachTrainDuration, this.eachTestDuration, this.dataCollectionDuration,
				this.sizeSoftmax, this.sizeHiddenUnits, this.learningRate, this.numEpochs, this.drawChart);
		Thread rbmWholeProcessThread = new Thread(rbmWholeProcessRunnable);

		log.info("Starting "+ rbmWholeProcessThreadName +" at time "+System.currentTimeMillis());
		rbmWholeProcessThread.start();

		// run collecting data within duration this.dataCollectionDuration + this.overhead
		// contains multiple trainingDataCollection+testingDataCollection cycles
		try {
			Thread.sleep(this.dataCollectionDuration + this.overhead);
		} catch (InterruptedException e) {
			log.info("PredictUserPreferences is interrupted at " + System.currentTimeMillis());
		}
		rbmWholeProcessThread.interrupt();
		log.info(rbmWholeProcessThreadName + " ends at "+System.currentTimeMillis());
	}

	public static void main(String[] argv) {
		long overhead = 10000;
		long dataCollectionDuration = 30*1000; 
		long eachTrainDuration = 8000; 
		long eachTestDuration = 2000; 
		int sizeSoftmax = 5; // Sentiment(5-point scale/5-way softmax): "Very negative(0)", "Negative(1)", "Neutral(2)", "Positive(3)", "Very positive(4)"
		int sizeHiddenUnits = 6; // http://en.wikipedia.org/wiki/List_of_genres
		double learningRate = 0.1;
		int numEpochs = 1;
		boolean drawChart = false; // true

		final PredictUserPreferences predictUserPref = new PredictUserPreferences(
				overhead, eachTrainDuration, eachTestDuration, dataCollectionDuration,
				sizeSoftmax, sizeHiddenUnits, learningRate, numEpochs, drawChart);

		Thread predictUserPrefThread = new Thread () {
			public void run () {
				predictUserPref.start();
			}
		};
		predictUserPrefThread.start();
	}
}

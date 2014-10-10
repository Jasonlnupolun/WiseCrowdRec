package com.feiyu.deeplearning.RBM;
/**
 * 
 * @author feiyu
 *
 */

public class PredictUserPreferences {
	long dataCollectionDuration; 
	long trianingDataCollectionDurationEach; 
	long testingDataCollectionDurationEach; 
	long overhead;
	
	public PredictUserPreferences(long dataCollectionDuration, long trianingDataCollectionDurationEach,
			long testingDataCollectionDurationEach, long overhead) {
		this.dataCollectionDuration = dataCollectionDuration; 
		this.trianingDataCollectionDurationEach = trianingDataCollectionDurationEach; 
		this.testingDataCollectionDurationEach = testingDataCollectionDurationEach; 
		this.overhead = overhead;
	}
	
	public void startCollectingData() {
		System.out.println("\n------------>Start Collecting Data");
		String dataCollectionThreadName = "RBMDataCollectionThread"; 
		Runnable collectingData = new RBMDataCollectionThread(dataCollectionThreadName, this.trianingDataCollectionDurationEach, 
				this.testingDataCollectionDurationEach, this.dataCollectionDuration);
		Thread collectingDataThread = new Thread(collectingData);

		System.out.println("Starting "+ dataCollectionThreadName +" at time "+System.currentTimeMillis());
		collectingDataThread.start();

		// run collecting data within duration this.dataCollectionDuration + this.overhead
		// contains multiple trainingDataCollection+testingDataCollection cycles
		try {
			Thread.sleep(this.dataCollectionDuration + this.overhead);
		} catch (InterruptedException e) {
			System.out.println("startCollectingData() is interrupted at " + System.currentTimeMillis());
		}
		collectingDataThread.interrupt();
		System.out.println(collectingDataThread+ " ends at "+System.currentTimeMillis());
	}
	
	public void wholeProcess_RBM() {
	}
	
	
	public static void main(String[] argv) {
		long dataCollectionDuration = 30*1000; 
		long trianingDataCollectionDurationEach = 8000; 
		long testingDataCollectionDurationEach = 2000; 
		long overhead = 10000;
		
		final PredictUserPreferences predictUserPref = new PredictUserPreferences(dataCollectionDuration, trianingDataCollectionDurationEach,
				testingDataCollectionDurationEach, overhead);
	
		// Collecting Data
		Thread collectingDataThread = new Thread () {
			public void run () {
				predictUserPref.startCollectingData();
			}
		};
		collectingDataThread.start();
		
		// 
	}
}

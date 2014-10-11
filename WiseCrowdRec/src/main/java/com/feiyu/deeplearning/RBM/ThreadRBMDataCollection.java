package com.feiyu.deeplearning.RBM;

/**
 * @author feiyu
 */

public class ThreadRBMDataCollection implements Runnable {
	private String threadName;
	private long eachTrainDuration;
	private long eachTestDuration;
	private long dataCollectionDuration;

	public ThreadRBMDataCollection(String threadName, long eachTrainDuration, long eachTestDuration, long dataCollectionDuration) {
		this.threadName = threadName;
		this.eachTrainDuration = eachTrainDuration;
		this.eachTestDuration = eachTestDuration;
		this.dataCollectionDuration = dataCollectionDuration;
		System.out.println("Creating " +  this.threadName + " at time " +System.currentTimeMillis());
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		try {
			while (System.currentTimeMillis() - startTime < dataCollectionDuration) {
				GetTrainingTestingDataCurrentKMins getData = new GetTrainingTestingDataCurrentKMins();
				getData.startRBMDataCollection(this.eachTrainDuration, this.eachTestDuration);
			}
			Thread.sleep(1);
		} catch (InterruptedException e) {
			System.out.println(this.threadName+" is interrupted at " + System.currentTimeMillis());
			//			e.printStackTrace();
		}
	}
}

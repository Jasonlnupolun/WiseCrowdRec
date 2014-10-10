package com.feiyu.deeplearning.RBM;

import com.feiyu.utils.GlobalVariables;

/**
 * 
 * @author feiyu
 *
 */

public class RBMDataCollectionThread implements Runnable {
	private String threadName;
	private long eachTrainDuration;
	private long eachTestDuration;
	private long dataCollectionDuration;
	private GetTrainingTestingDataCurrentFiveMins getData;

	public RBMDataCollectionThread(String threadName, long eachTrainDuration, long eachTestDuration, long dataCollectionDuration) {
		this.threadName = threadName;
		this.eachTrainDuration = eachTrainDuration;
		this.eachTestDuration = eachTestDuration;
		this.dataCollectionDuration = dataCollectionDuration;
		this.getData = new GetTrainingTestingDataCurrentFiveMins();
		System.out.println("Creating " +  this.threadName + " at time " +System.currentTimeMillis());
	}

	public void run() {
		long startTime = System.currentTimeMillis();
		try {
			while (System.currentTimeMillis() - startTime < dataCollectionDuration) {
				getData.startRBMDataCollection(this.eachTrainDuration, this.eachTestDuration);
				
				int nUsers = GlobalVariables.RBM_USER_LIST.size();
				int nMovies = GlobalVariables.RBM_MOVIE_LIST.size();
				System.out.println(nUsers+" "+nMovies);
				
				for (int i=0; i< nUsers ; i++) {
					System.out.print(GlobalVariables.RBM_USER_LIST.get(i)+"><");
				}

				System.out.println();
				for (int i=0; i< nMovies ; i++) {
					System.out.print(GlobalVariables.RBM_MOVIE_LIST.get(i)+"><");
				}
			}
			Thread.sleep(1);
		} catch (InterruptedException e) {
			System.out.println(this.threadName+" is interrupted at " + System.currentTimeMillis());
//			e.printStackTrace();
		}
	}
}

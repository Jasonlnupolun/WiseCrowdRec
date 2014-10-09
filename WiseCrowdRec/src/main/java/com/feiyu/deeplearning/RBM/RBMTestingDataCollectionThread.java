package com.feiyu.deeplearning.RBM;

public class RBMTestingDataCollectionThread implements Runnable {
	private String threadName;

	public RBMTestingDataCollectionThread(String threadName) {
		this.threadName = threadName;
		System.out.println("Creating " +  this.threadName + " at time " +System.currentTimeMillis());
	}

	public void run() {
		try {
			RBMRabbitMQServerSide rabbitmqServer = new RBMRabbitMQServerSide();
			rabbitmqServer.rbmRabbitMQServerSide(this.threadName); 
			Thread.sleep(1);
		} catch (InterruptedException e) {
			System.out.println(this.threadName+" is interrupted at time " + System.currentTimeMillis());
//			e.printStackTrace();
		}
		System.out.println(this.threadName + "is ended at time " + System.currentTimeMillis());
	}
}
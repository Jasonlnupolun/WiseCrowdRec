package com.feiyu.deeplearning.RBM;

import org.json.simple.parser.ParseException;
/**
 * 
 * @author feiyu
 *
 */

public class RBMTestingDataCollectionThread implements Runnable {
	private String threadName;

	public RBMTestingDataCollectionThread(String threadName) {
		this.threadName = threadName;
		System.out.println("Creating " +  this.threadName + " at " +System.currentTimeMillis());
	}

	public void run() {
		try {
			RBMRabbitMQServerSide rabbitmqServer = new RBMRabbitMQServerSide();
			rabbitmqServer.rbmRabbitMQServerSide(this.threadName); 
			Thread.sleep(1);
		} catch (InterruptedException e) {
			System.out.println(this.threadName+" is interrupted at " + System.currentTimeMillis());
//			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
package com.feiyu.deeplearning.RBM;
/**
 * @author feiyu
 */

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

public class ThreadRBMDataCollectionTrainXMins implements Runnable {
	private static Logger log = Logger.getLogger(ThreadRBMDataCollectionTrainXMins.class.getName());
	private String threadName;

	public ThreadRBMDataCollectionTrainXMins(String threadName) {
		this.threadName = threadName;
		log.info("Creating " +  this.threadName + " at " +System.currentTimeMillis());
	}

	public void run() {
		try {
			RBMRabbitMQServerSide rabbitmqServer = new RBMRabbitMQServerSide();
			rabbitmqServer.rbmRabbitMQServerSide(this.threadName, true); 
			Thread.sleep(1);
		} catch (InterruptedException e) {
			log.info(this.threadName+" is interrupted at " + System.currentTimeMillis());
			//			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
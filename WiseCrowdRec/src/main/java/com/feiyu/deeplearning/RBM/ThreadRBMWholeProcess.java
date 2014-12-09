package com.feiyu.deeplearning.RBM;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.feiyu.springmvc.model.RBMDataQueueElementInfo;
import com.feiyu.springmvc.model.RBMMovieInfo;
import com.feiyu.springmvc.model.RBMUserInfo;
import com.feiyu.utils.GlobalVariables;

/**
 * @author feiyu
 */

public class ThreadRBMWholeProcess implements Runnable {
  private static Logger log = Logger.getLogger(ThreadRBMWholeProcess.class.getName());
  private String threadName;

  public ThreadRBMWholeProcess( String threadName) {
    this.threadName = threadName;
    log.info("Creating " +  this.threadName + " at time " +System.currentTimeMillis());
  }

  public void run() {
    //		long startTime = System.currentTimeMillis();
    try {
      //			while (System.currentTimeMillis() - startTime < GlobalVariables.RBM_DATA_COLLECTION_DURATION) {
      while (true) {
        // Data Collection: Training data and Testing data
        GetTrainingTestingDataCurrentKMins getData = new GetTrainingTestingDataCurrentKMins();
        getData.startRBMDataCollection();

        // Training and Testing RBMs
        log.info("\n------------>Start train RBMs");
        String trainRBMsName= "ThreadRBMTrainingKMins"; 
        RBMDataQueueElementInfo pollElement = GlobalVariables.RBM_DATA_QUEUE.poll();
        Runnable trainRBMsRunnable= new ThreadRBMTrainingKMins(
          trainRBMsName,
          new RBMDataQueueElementInfo(
            GlobalVariables.KTH_RBM,
            new HashMap<String, RBMMovieInfo>(GlobalVariables.RBM_MOVIE_HASHMAP),
            new HashMap<String, RBMUserInfo>(pollElement.getUserHashMapTrain()),
            new HashMap<String, RBMUserInfo>(GlobalVariables.RBM_USER_HASHMAP),
            new ArrayList<String>(GlobalVariables.RBM_MOVIE_LIST))	
            );
        Thread trainRBMsThread = new Thread(trainRBMsRunnable);
        log.info("Starting "+ trainRBMsName +" at time "+System.currentTimeMillis());
        trainRBMsThread.start();

        log.info(trainRBMsName+ " ends at "+System.currentTimeMillis());	
        Thread.sleep(1);
      }
    } catch (InterruptedException e) {
      log.info(this.threadName+ " Rabbitmq is interrupted at " + System.currentTimeMillis());
    }
  }
}

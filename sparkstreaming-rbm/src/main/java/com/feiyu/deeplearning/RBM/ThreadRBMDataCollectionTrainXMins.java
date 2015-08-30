package com.feiyu.deeplearning.RBM;
/**
 * @author Fei Yu (@faustineinsun)
 */

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

public class ThreadRBMDataCollectionTrainXMins implements Runnable {
  private static Logger log = Logger.getLogger(ThreadRBMDataCollectionTrainXMins.class.getName());
  private String threadName;

  public ThreadRBMDataCollectionTrainXMins(String threadName) {
    this.threadName = threadName;
    log.debug("Creating " +  this.threadName + " at " +System.currentTimeMillis());
  }

  public void run() {
    try {
      RabbitMQServerSideStoreTriple2RBM rabbitmqServer = new RabbitMQServerSideStoreTriple2RBM();
      rabbitmqServer.rbmRabbitMQServerSide(this.threadName, true); 
      Thread.sleep(1);
    } catch (InterruptedException e) {
      log.debug(this.threadName+" is interrupted at " + System.currentTimeMillis());
      //e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
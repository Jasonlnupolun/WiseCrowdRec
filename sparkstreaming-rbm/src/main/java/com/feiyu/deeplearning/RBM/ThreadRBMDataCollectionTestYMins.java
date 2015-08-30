package com.feiyu.deeplearning.RBM;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;
/**
 * @author Fei Yu (@faustineinsun)
 */

public class ThreadRBMDataCollectionTestYMins implements Runnable {
  private static Logger log = Logger.getLogger(ThreadRBMDataCollectionTestYMins.class.getName());
  private String threadName;

  public ThreadRBMDataCollectionTestYMins(String threadName) {
    this.threadName = threadName;
    log.debug("Creating " +  this.threadName + " at " +System.currentTimeMillis());
  }

  public void run() {
    try {
      RabbitMQServerSideStoreTriple2RBM rabbitmqServer = new RabbitMQServerSideStoreTriple2RBM();
      rabbitmqServer.rbmRabbitMQServerSide(this.threadName, false); 
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
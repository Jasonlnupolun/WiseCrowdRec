package com.feiyu.deeplearning.RBM;
/**
 * @author Fei Yu (@faustineinsun)
 */

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.feiyu.classes.RBMMovieInfo;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;

public class GetTrainingTestingDataCurrentKMins {
  private static Logger log = Logger.getLogger(GetTrainingTestingDataCurrentKMins.class.getName());

  public GetTrainingTestingDataCurrentKMins() {
    GlobalVariables.RBM_MOVIE_HASHMAP= new HashMap<String, RBMMovieInfo>();
    GlobalVariables.RBM_MOVIE_LIST= new ArrayList<String>();
    GlobalVariables.RBM_MOVIE_MAX_IDX = -1;
  }

  public void startRBMDataCollection() {
    // Collecting Training and Testing Data, as well as push each train/test user-ratedMovies-List into DataQueue(FIFO)
    this.startCollectTrainingData();
    this.startCollectTestingData();
  }	

  private void startCollectTrainingData() {
    log.debug("\n------------>startCollectTrainingData");
    String trainingDataThreadName = "RBMTrainingDataCollectionThread"; 
    Runnable collectTrainingData = new ThreadRBMDataCollectionTrainXMins(trainingDataThreadName);
    GlobalVariables.RBM_COLLECT_TRAINING_DATA_THREAD = new Thread(collectTrainingData);

    log.debug("Starting "+ trainingDataThreadName+" at "+System.currentTimeMillis());
    GlobalVariables.RBM_COLLECT_TRAINING_DATA_THREAD.start();

    //try {
    while(true) {
      //Thread.sleep(1);
      if (GlobalVariables.RBM_COLLECT_TRAINING_DATA_THREAD.isInterrupted()) {
        log.debug("%%%%%% stop collecting training data and start collecting testing data");
        break;
      }
    }
    /*
    } catch (InterruptedException e) {
    	log.debug("startCollectTrainingData() is interrupted");
    }
     * 
     */
  }

  private void startCollectTestingData() {
    log.debug("\n------------>startCollectTestingData");
    String testingDataThreadName = "RBMTestingDataCollectionThread"; 
    Runnable collectTestingData = new ThreadRBMDataCollectionTestYMins(testingDataThreadName);
    GlobalVariables.RBM_COLLECT_TESTING_DATA_THREAD = new Thread(collectTestingData);

    log.debug("Starting "+ testingDataThreadName +" at "+System.currentTimeMillis());
    GlobalVariables.RBM_COLLECT_TESTING_DATA_THREAD.start();
    //try {
    while(true) {
      //Thread.sleep(1);
      if (GlobalVariables.RBM_COLLECT_TESTING_DATA_THREAD.isInterrupted()) {
        log.debug("%%%%%% stop collecting testing data and start collecting training data");
        break;
      }
    }
    /*
    } catch (InterruptedException e) {
    	log.debug("startCollectTestingData() is interrupted");
    }
     * 
     */
  }

  public static void main(String[] argv) {
    InitializeWCR iniWCR = new InitializeWCR();
    iniWCR.initializeRBM();

    GetTrainingTestingDataCurrentKMins getData = new GetTrainingTestingDataCurrentKMins();
    getData.startRBMDataCollection();
  }
}

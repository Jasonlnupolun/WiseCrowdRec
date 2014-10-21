package com.feiyu.deeplearning.RBM;

import org.apache.log4j.Logger;

import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;

/**
 * @author feiyu
 */

public class RBMDataCollectionModelTrainingTesting {
	private static Logger log = Logger.getLogger(RBMDataCollectionModelTrainingTesting.class.getName());

	public RBMDataCollectionModelTrainingTesting() {
		GlobalVariables.KTH_RBM = 0;
	}

	public void start() {
		Thread rbmDataCollectionModelTrainingTestingThread = new Thread () {
			public void run () {
				GlobalVariables.RBM_DATA_CLC_MDL_TRN_TST.startRBMWholeProcess();
			}
		};
		rbmDataCollectionModelTrainingTestingThread.start();
	}

	private void startRBMWholeProcess() {
		log.info("\n------------>Start Collecting Data");
		String rbmWholeProcessThreadName = "rbmWholeProcessThread"; 
		Runnable rbmWholeProcessRunnable = new ThreadRBMWholeProcess(rbmWholeProcessThreadName);
		Thread rbmWholeProcessThread = new Thread(rbmWholeProcessRunnable);

		log.info("Starting "+ rbmWholeProcessThreadName +" at time "+System.currentTimeMillis());
		rbmWholeProcessThread.start();

		// run collecting data within duration this.dataCollectionDuration + this.overhead
		// contains multiple trainingDataCollection+testingDataCollection cycles
		//		try {
		//			Thread.sleep(GlobalVariables.RBM_DATA_COLLECTION_DURATION + GlobalVariables.RBM_OVERHEAD);
		//		} catch (InterruptedException e) {
		//			log.info("PredictUserPreferences is interrupted at " + System.currentTimeMillis());
		//		}
		//		rbmWholeProcessThread.interrupt();
		//		log.info(rbmWholeProcessThreadName + " ends at "+System.currentTimeMillis());
	}

	public static void main(String[] argv) throws Exception {
		//		GlobalVariables.RBM_OVERHEAD = 10000;
		//		GlobalVariables.RBM_DATA_COLLECTION_DURATION = 30*1000; 
		//		GlobalVariables.RBM_EACH_TRAIN_DURATION = 8000; 
		//		GlobalVariables.RBM_EACH_TEST_DURATION = 2000; 
		//		GlobalVariables.RBM_SIZE_SOFTMAX = 5; // Sentiment(5-point scale/5-way softmax): "Very negative(0)", "Negative(1)", "Neutral(2)", "Positive(3)", "Very positive(4)"
		//		GlobalVariables.RBM_SIZE_HIDDEN_UNITS = 6; // http://en.wikipedia.org/wiki/List_of_genres
		//		GlobalVariables.RBM_LEARNING_RATE = 0.1;
		//		GlobalVariables.RBM_NUM_EPOCHS = 1;
		//		GlobalVariables.RBM_DRAW_CHART = false; // true

		InitializeWCR initWCR = new InitializeWCR();
		initWCR.getWiseCrowdRecConfigInfo();
		//		initWCR.coreNLPInitial();
		//		initWCR.calaisNLPInitial();
		initWCR.twitterInitDyna();
		initWCR.elasticsearchInitial();

		initWCR.initializeRBM();
		initWCR.getFreebaseInfo();
		initWCR.themoviedbOrgInitial();
		initWCR.rabbitmqInit();

		GlobalVariables.RBM_DATA_CLC_MDL_TRN_TST.start();
	}
}

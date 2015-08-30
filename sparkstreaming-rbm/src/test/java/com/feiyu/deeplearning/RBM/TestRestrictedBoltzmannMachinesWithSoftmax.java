package com.feiyu.deeplearning.RBM;
/**
 * @author feiyu
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.feiyu.classes.Tuple;

public class TestRestrictedBoltzmannMachinesWithSoftmax {
  int numMovies = 6;
  int sizeSoftmax = 2; //rating from 0 to 10
  int sizeHiddenUnits = 2;
  double learningRate = 0.1;
  int numEpochs = 5;
  boolean drawChart = true; // true

  @Test
  public void testRBMModel() throws IOException {

    // create a file for collecting the RMSE of Epochs from 1 to numEpochs
    String RMSEfileName = "RMSEByEpochs_TrainedRBMModel";
    File file = new File("src/main/resources/RBM/"+RMSEfileName+".txt");
    if (!file.exists()) {
      file.createNewFile();
    }
    FileWriter fw = new FileWriter(file.getAbsoluteFile());
    BufferedWriter bw = new BufferedWriter(fw);

    // Train Model
    if (drawChart) {
      for (int i=1; i<=numEpochs; i++) {
        unitTest_WithCertainEpoch(i, bw);
      }
      bw.close();
      System.out.println("Saved rmse-by-epoch to src/main/resources/"+RMSEfileName+".txt!!");
    } else {
      unitTest_WithCertainEpoch(numEpochs, bw);
    }
  }

  ////////////////////// For Data Creation	
  public ArrayList<Tuple<Integer,Integer>> insertTraningData_OneUser(int a, int b, int c, int d, int e, int f) {
    ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices = new ArrayList<Tuple<Integer,Integer>>();
    ratedMoviesIndices.add(new Tuple<Integer, Integer>(1,a));
    ratedMoviesIndices.add(new Tuple<Integer, Integer>(2,b));
    ratedMoviesIndices.add(new Tuple<Integer, Integer>(3,c));
    ratedMoviesIndices.add(new Tuple<Integer, Integer>(4,d));
    ratedMoviesIndices.add(new Tuple<Integer, Integer>(5,e));
    ratedMoviesIndices.add(new Tuple<Integer, Integer>(6,f));
    return ratedMoviesIndices;
  }

  public void unitTest_WithCertainEpoch(int epochs, BufferedWriter bw) throws IOException {			
    RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax = new RestrictedBoltzmannMachinesWithSoftmax(
      numMovies, sizeSoftmax, sizeHiddenUnits, learningRate, epochs, bw
        );

    rbmSoftmax.trainRBMWeightMatrix(insertTraningData_OneUser(1,1,1,0,0,0));
    rbmSoftmax.trainRBMWeightMatrix(insertTraningData_OneUser(1,0,1,0,0,0));
    rbmSoftmax.trainRBMWeightMatrix(insertTraningData_OneUser(1,1,1,0,0,0));
    rbmSoftmax.trainRBMWeightMatrix(insertTraningData_OneUser(0,0,1,1,1,0));
    rbmSoftmax.trainRBMWeightMatrix(insertTraningData_OneUser(0,0,1,1,0,0));
    rbmSoftmax.trainRBMWeightMatrix(insertTraningData_OneUser(0,0,1,1,1,0));

    rbmSoftmax.getTrainedWeightMatrix_RBM();

    rbmSoftmax.predictUserPreference_VisibleToHiddenToVisible(insertTraningData_OneUser(0,0,0,1,1,0), false);
    rbmSoftmax.predictUserPreference_VisibleToHiddenToVisible(insertTraningData_OneUser(1,1,1,0,0,0), false);
    rbmSoftmax.predictUserPreference_VisibleToHiddenToVisible(insertTraningData_OneUser(1,0,1,0,0,0), false);
    rbmSoftmax.predictUserPreference_VisibleToHiddenToVisible(insertTraningData_OneUser(0,0,1,1,1,0), false);
    rbmSoftmax.predictUserPreference_VisibleToHiddenToVisible(insertTraningData_OneUser(0,0,1,1,0,0), false);

    rbmSoftmax.getTrainedWeightMatrix_RBM();
    rbmSoftmax.getRMSEOfRBMModel();
  }

}

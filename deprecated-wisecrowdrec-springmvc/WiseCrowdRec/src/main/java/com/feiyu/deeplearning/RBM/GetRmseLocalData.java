package com.feiyu.deeplearning.RBM;
/**
 * @author feiyu
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.feiyu.springmvc.model.Tuple;
import org.apache.log4j.Logger;

public class GetRmseLocalData {
  private static Logger log = Logger.getLogger(GetRmseLocalData.class.getName());
  int numMovies = 1682;
  int sizeSoftmax = 6; //6 0~5 rating from 0 to 10
  int sizeHiddenUnits = 5;
  double learningRate = 0.8;
  int numEpochs = 1;
  boolean drawChart = true; // true false
  BufferedWriter bw;

  public void start() throws IOException {
    // create a file for collecting the RMSE of Epochs from 1 to numEpochs
    String RMSEfileName = "RMSEByEpochs_TrainedRBMModel";
    File file = new File("/Library/Tomcat/logs/"+RMSEfileName+".txt");
    if (!file.exists()) {
      file.createNewFile();
    }
    FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
    bw = new BufferedWriter(fw);

    // Train Model
    if (drawChart) {
      for (int i=1; i<=numEpochs; i++) {
        unitTest_WithCertainEpoch(i);
      }
    } else {
      unitTest_WithCertainEpoch(numEpochs);
    }
    bw.close();
    System.out.println("Saved rmse-by-epoch to src/main/resources/"+RMSEfileName+".txt!!");
  }


  ////////////////////// For Data Creation	
  public void unitTest_WithCertainEpoch(int epochs) throws IOException {			
    RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax = new RestrictedBoltzmannMachinesWithSoftmax(
      numMovies, sizeSoftmax, sizeHiddenUnits, learningRate, epochs, bw
        );

    ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices = new ArrayList<Tuple<Integer,Integer>>();

    BufferedReader br = null;
    String line = "", preUser = "";
    String[] curLine;
    String inputFile = "src/main/resources/u1.base";
    int numUserForTrain = (int)(943*0.8), numUsers = 0;

    try {
      br = new BufferedReader(new FileReader(inputFile));
      while ((line = br.readLine()) != null) {
        curLine = line.split("\t");
        String user = curLine[0];
        String movie = curLine[1];
        String rating = curLine[2];
        if (!user.equals(preUser)) {
          if (numUsers>0 && numUsers < numUserForTrain) {
            log.info("train user " + ""+preUser+": "+ratedMoviesIndices.size());
            rbmSoftmax.trainRBMWeightMatrix(ratedMoviesIndices);
          } else if (numUsers>0 && numUsers >= numUserForTrain) {
            log.info("train user " + ""+preUser+": "+ratedMoviesIndices.size());
            rbmSoftmax.predictUserPreference_VisibleToHiddenToVisible(ratedMoviesIndices, false);
          }
          ratedMoviesIndices = new ArrayList<Tuple<Integer,Integer>>();
          numUsers++;
        }
        //				log.info("numUsers: "+numUsers+ ",preUser: "+preUser+",user: "+user+",movie: "+movie+",rating: "+rating + "-->"+ line);
        ratedMoviesIndices.add(new Tuple<Integer, Integer>(Integer.valueOf(movie),Integer.valueOf(rating)));
        preUser = user;
      }
      log.info("Finished reading train.txt at epoch:"+epochs);
      rbmSoftmax.getTrainedWeightMatrix_RBM();
      rbmSoftmax.getRMSEOfRBMModel();
      br.close();
    } catch (FileNotFoundException e) {
      System.out.println("FileNotFoundException :"+e.getMessage());
    } catch (IOException e) {
      System.out.println("IOException :"+e.getMessage());
    }
    System.out.println("Done!");	

  }

  public static void main(String[] argv) throws IOException {
    GetRmseLocalData	getRE = new GetRmseLocalData();
    getRE.start();
  }
}

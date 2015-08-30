package com.feiyu.starter;

/**
 * @author Fei Yu (@faustineinsun)
 */

import java.io.IOException;

import com.feiyu.utils.GlobalVariables;

public class Main {
  public static void main(String[] argv) throws IOException {
    RunInitSetups runInitSetups = new RunInitSetups();
    runInitSetups.start();
    
    //GlobalVariables.JEDIS_API.flushAll();

    GlobalVariables.SPARK_TWT_STREAMING.startSpark("movie");
    //GlobalVariables.RBM_DATA_CLC_MDL_TRN_TST.start();
    
    //GlobalVariables.JEDIS_API.close();
  }
}

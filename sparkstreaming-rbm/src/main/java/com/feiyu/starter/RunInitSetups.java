package com.feiyu.starter;
/**
 * @author Fei Yu (@faustineinsun)
 */

import javax.servlet.http.HttpServlet;

import com.feiyu.socketio.SocketIOHandler;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;

public class RunInitSetups extends HttpServlet {

  private static final long serialVersionUID = -1685928905690566889L;

  public void start() {
    InitializeWCR initWcr = new InitializeWCR();

    try {
      initWcr.getWiseCrowdRecConfigInfo();
      initWcr.twitterInitDyna();
      initWcr.coreNLPInitial();
      initWcr.calaisNLPInitial();
      initWcr.themoviedbOrgInitial();
      initWcr.rabbitmqInit();
      initWcr.initializeRBM();
      initWcr.getFreebaseInfo();
      initWcr.signInWithTwitterGetAppOauth();

      GlobalVariables.SPARK_TWT_STREAMING.sparkInit();

      Thread SocketIOHandlerThread = new Thread () {
        public void run () {
          try {
            SocketIOHandler io = new SocketIOHandler();
            io.start();
          } catch (Exception e) {
            e.printStackTrace();
          } 
        }
      };
      SocketIOHandlerThread.start();

      /**
       Thread LikeOrNotWSHandlerThread = new Thread () {
        public void run () {
          try {
            LikeOrNotWSHandler.start();
          } catch (Exception e) {
            e.printStackTrace();
          } 
        }
      };
      LikeOrNotWSHandlerThread.start();
       */

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

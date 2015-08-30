package com.feiyu.websocket;
/**
 * 
 * @author feiyu
 *
 */

public class StartWebSocket {

  public void startWebSocketWithUserID(final String userID) {

    Thread StarMovieCategorySubGraphWebSocketHandlerThread = new Thread () {
      public void run () {
        try {
          StarMovieCategorySubGraphWebSocketHandler.start(userID);
        } catch (Exception e) {
          e.printStackTrace();
        } 
      }
    };

    StarMovieCategorySubGraphWebSocketHandlerThread.start();

    System.out.println("---------------startServerSideWebSocketWithUserID---------------"+userID);
  }
}

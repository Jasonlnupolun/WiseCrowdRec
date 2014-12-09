package com.feiyu.websocket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * @author feiyu
 */

public class LikeOrNotWSHandler {
  public static void start() throws Exception {
    Server server = new Server(7777);
    ServletContextHandler contextHandler = new ServletContextHandler(server, "/", true, false);
    contextHandler.addServlet(LikeOrNotWSServlet.class, "/likeornot");
    server.start();
    server.join();
  }

  public static void main(String[] argv) throws Exception {
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
  }
}

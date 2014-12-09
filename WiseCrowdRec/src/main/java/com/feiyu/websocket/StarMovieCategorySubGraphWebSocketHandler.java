package com.feiyu.websocket;
/**
 * @author feiyu
 */

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class StarMovieCategorySubGraphWebSocketHandler {
  public static void start(String userID) throws Exception {
    Server server = new Server(9988);
    ServletContextHandler contextHandler = new ServletContextHandler(server, "/", true, false);
    contextHandler.addServlet(StarMovieCategorySubGraphWebSocketServlet.class, "/smcsubgraphws/"+userID);
    server.start();
    server.join();
  }
}

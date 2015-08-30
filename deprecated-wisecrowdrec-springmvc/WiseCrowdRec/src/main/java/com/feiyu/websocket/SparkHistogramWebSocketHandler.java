package com.feiyu.websocket;
/**
 * @author feiyu
 */

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;

public class SparkHistogramWebSocketHandler {

  public static void start() throws Exception {
    Server server = new Server(8889);
    ServletContextHandler contextHandler = new ServletContextHandler(server, "/", true, false);
    contextHandler.addServlet(SparkHistogramWebSocketServlet.class, "/sparkchartws");
    server.start();
    server.join();
  }
}
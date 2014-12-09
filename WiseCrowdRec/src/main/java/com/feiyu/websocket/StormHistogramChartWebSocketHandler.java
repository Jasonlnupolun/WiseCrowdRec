package com.feiyu.websocket;
/**
 * @author feiyu
 */

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class StormHistogramChartWebSocketHandler {
  public static void start() throws Exception {
    Server server = new Server(9998);
    ServletContextHandler contextHandler = new ServletContextHandler(server, "/", true, false);
    contextHandler.addServlet(StormHistogramChartWebSocketServlet.class, "/stormchartws");
    server.start();
    server.join();
  }
}

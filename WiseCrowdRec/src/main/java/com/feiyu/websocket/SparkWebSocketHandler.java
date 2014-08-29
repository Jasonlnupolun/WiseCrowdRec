package com.feiyu.websocket;
/**
 * @author feiyu
 */

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;

public class SparkWebSocketHandler {

	public static void start() throws Exception {
		Server server = new Server(8899);
		ServletContextHandler contextHandler = new ServletContextHandler(server, "/", true, false);
		contextHandler.addServlet(SparkWebSocketServlet.class, "/sparkws");
		server.start();
		server.join();
	}
}
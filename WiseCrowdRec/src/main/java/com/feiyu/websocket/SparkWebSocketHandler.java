package com.feiyu.websocket;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;

public class SparkWebSocketHandler {

	public static void start(String userID) throws Exception {
		Server server = new Server(8899);
		ServletContextHandler contextHandler = new ServletContextHandler(server, "/", true, false);
		contextHandler.addServlet(SparkWebSocketServlet.class, "/sparkws/"+userID);
		server.start();
		server.join();
	}
}
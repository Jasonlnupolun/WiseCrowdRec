package com.feiyu.websocket;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class StarMovieCategorySubGraphWebSocketHandler {
	public static void start() throws Exception {
		Server server = new Server(9988);
		ServletContextHandler contextHandler = new ServletContextHandler(server, "/", true, false);
		contextHandler.addServlet(StarMovieCategorySubGraphWebSocketServlet.class, "/smcsubgraphws");
		server.start();
		server.join();
	}
	
	public static void main(String[] args) throws Exception {
		start();
	}
}

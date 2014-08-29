package com.feiyu.websocket;
/**
 * @author feiyu
 */

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class StarMovieCategorySubGraphWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 430684348581326100L;

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String s) {
		return new StarMovieCategorySubGraphWebSocket();
	}
}
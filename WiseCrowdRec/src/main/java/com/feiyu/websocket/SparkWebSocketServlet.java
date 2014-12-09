package com.feiyu.websocket;
/**
 * @author feiyu
 */

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class SparkWebSocketServlet extends WebSocketServlet {
  private static final long serialVersionUID = -1569886292535228770L;

  @Override
  public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String s) {
    return new SparkWebSocket();
  }
}

package com.feiyu.websocket;
/**
 * @author feiyu
 */

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class SparkHistogramWebSocketServlet extends WebSocketServlet {
  private static final long serialVersionUID = 4661120203832761973L;

  @Override
  public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String s) {
    return new SparkHistogramWebSocket();
  }
}

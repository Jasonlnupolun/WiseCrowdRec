package com.feiyu.websocket;
/**
 * @author feiyu
 */

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class StormHistogramChartWebSocketServlet extends WebSocketServlet {
  private static final long serialVersionUID = -7041170965301015573L;

  @Override
  public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String s) {
    return new StormHistogramChartWebSocket();
  }
}

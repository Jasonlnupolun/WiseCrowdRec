package com.feiyu.websocket;

import javax.servlet.http.HttpServletRequest;
/**
 * @author feiyu
 */

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

public class LikeOrNotWSServlet  extends WebSocketServlet {
  private static final long serialVersionUID = 1205762777331654469L;

  @Override
  public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String s) {
    return new LikeOrNotWS();
  }
}

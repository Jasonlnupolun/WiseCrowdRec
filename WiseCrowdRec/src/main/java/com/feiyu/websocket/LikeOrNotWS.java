package com.feiyu.websocket;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.feiyu.utils.GlobalVariables;

/**
 * @author feiyu
 */

public class LikeOrNotWS implements WebSocket.OnTextMessage {
  private static Logger log = Logger.getLogger(LikeOrNotWS.class.getName());
  JSONParser parser = new JSONParser();
  String fullname;
  String entity;

  @Override
  public void onOpen(Connection arg0) {
    log.info("LikeOrNowWS server is open! ");

  }

  @Override
  public void onMessage(String s) {
    log.info("LikeOrNowWS server got an message: this client dislikes "+ s);
    JSONObject jsonObject;
    try {
      jsonObject = (JSONObject)parser.parse(s);
      fullname = (String) jsonObject.get("fullname");
      entity = (String) jsonObject.get("entity");
      log.debug("~~"+fullname+"~~~"+entity);

      if (entity.equals("genre")) {
        GlobalVariables.RBM_CLIENT_DISLIKED_GENRES.add(fullname);
      } else if (entity.equals("movie")) {
        GlobalVariables.RBM_CLIENT_DISLIKED_MOVIES.add(fullname);
      }

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onClose(int arg0, String arg1) {
    log.info("LikeOrNowWS server is closed!");
  }

}

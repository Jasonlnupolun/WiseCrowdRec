package com.feiyu.websocket;

import org.eclipse.jetty.websocket.WebSocket;
/**
 * @author feiyu
 */

public class LikeOrNotWS implements WebSocket.OnTextMessage {

	@Override
	public void onOpen(Connection arg0) {
		System.out.println("LikeOrNowWS server is open! ");
		
	}

	@Override
	public void onMessage(String s) {
		System.out.println("LikeOrNowWS server got an message: this client dislikes "+ s);
	}

	@Override
	public void onClose(int arg0, String arg1) {
		System.out.println("LikeOrNowWS server is closed!");
	}

}

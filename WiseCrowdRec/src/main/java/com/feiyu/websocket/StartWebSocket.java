package com.feiyu.websocket;

public class StartWebSocket {
	
	public void startWebSocketWithUserID(final String userID) {
		Thread SparkWebSocketHandlerThread = new Thread () {
			public void run () {
				try {
					SparkWebSocketHandler.start(userID);//Open Spark server side websocket
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		};

		Thread StarMovieCategorySubGraphWebSocketHandlerThread = new Thread () {
			public void run () {
				try {
					StarMovieCategorySubGraphWebSocketHandler.start(userID);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		};
		
		SparkWebSocketHandlerThread.start();
		StarMovieCategorySubGraphWebSocketHandlerThread.start();

		System.out.println("---------------startServerSideWebSocketWithUserID---------------"+userID);
	}
}

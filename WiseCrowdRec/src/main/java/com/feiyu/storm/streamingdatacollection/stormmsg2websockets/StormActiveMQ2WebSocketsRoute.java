/**
 * reference:
 * http://camel.apache.org/twitter-websocket-example.html
 * 
 * http://blog.avisi.nl/2013/07/01/tools-for-building-a-real-time-analytics-platform/
 * Apache Storm's outputs are written to an ActiveMQ queue, 
 * Apache Camel get info from this ActiveMQ queue and send these outputs to WebSockets
 * 
 * http://java.dzone.com/articles/open-source-integration-apache
 */
package com.feiyu.storm.streamingdatacollection.stormmsg2websockets;

import org.apache.camel.builder.RouteBuilder;

public class StormActiveMQ2WebSocketsRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("activemq:backtype.storm.contrib.example.queue").to("websocket:wcrstorm?sendToAll=true");//websocket://localhost:9292/wcrStorm?sendToAll=true
		// StreamCaching is not in use. If using streams then its recommended to enable stream caching. 
		// See more details at http://camel.apache.org/stream-caching.html
	}
}

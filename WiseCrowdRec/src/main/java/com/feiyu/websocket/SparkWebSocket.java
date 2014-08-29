package com.feiyu.websocket;
/**
 * @author feiyu
 */

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket;

import com.feiyu.utils.GlobalVariables;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class SparkWebSocket implements WebSocket.OnTextMessage{
	private final static String QUEUE_NAME = GlobalVariables.RABBITMQ_QUEUE_NAME_SPARK;
//	private org.eclipse.jetty.websocket.WebSocket.Connection jettyWSconnection;

	@Override
	public void onOpen(org.eclipse.jetty.websocket.WebSocket.Connection jettyWSconnection) {
//		this.jettyWSconnection = jettyWSconnection;

		// RabbitMQ
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			com.rabbitmq.client.Connection rabbitmqConnection;
			rabbitmqConnection = factory.newConnection();
			Channel channel = rabbitmqConnection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			System.out.println(" [*] SparkWebSocket server is waiting for messages. To exit press CTRL+C");

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_NAME, true, consumer);
			while (true) {
				QueueingConsumer.Delivery delivery = null;
				try {
					delivery = consumer.nextDelivery();
				} catch (ShutdownSignalException | ConsumerCancelledException
						| InterruptedException e) {
					e.printStackTrace();
				}
				String message = new String(delivery.getBody());
				System.out.println(" [...x...] SparkWebSocket server received '" + message.replaceAll("\\s+","") + "'");

				try {
					jettyWSconnection.sendMessage(message.replaceAll("\\s+",""));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}// RabbitMQ end
	}

	@Override
	public void onClose(int i, String s) {
		System.out.println("SparkWebSocket server is closed");
	}

	@Override
	public void onMessage(String s) {
		System.out.println("SparkWebSocket server got message: " + s);
//		try {
//			jettyWSconnection.sendMessage("SparkWebSocket server got " + s);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}

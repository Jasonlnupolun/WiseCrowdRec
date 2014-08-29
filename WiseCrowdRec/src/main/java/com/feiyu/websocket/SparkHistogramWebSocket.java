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

public class SparkHistogramWebSocket implements WebSocket.OnTextMessage{
	private final static String QUEUE_NAME = GlobalVariables.RABBITMQ_QUEUE_NAME_SPARKHISTOGRAMCHART;
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
			System.out.println(" [*] SparkHistogramWebSocket server is waiting for messages. To exit press CTRL+C");

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
				System.out.println(" [...x...] SparkHistogramWebSocket server received '" + message + "'");
				try {
					jettyWSconnection.sendMessage(message);
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
		System.out.println("SparkHistogramWebSocket server is closed");
	}

	@Override
	public void onMessage(String s) {
		System.out.println("SparkHistogramWebSocket server got message: " + s);
//		try {
//			jettyWSconnection.sendMessage("SparkHistogramWebSocket server got " + s);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
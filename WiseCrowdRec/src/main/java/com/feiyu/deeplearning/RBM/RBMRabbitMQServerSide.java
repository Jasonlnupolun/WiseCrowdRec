package com.feiyu.deeplearning.RBM;

import java.io.IOException;

import com.feiyu.utils.GlobalVariables;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RBMRabbitMQServerSide {
	private final static String QUEUE_NAME = GlobalVariables.RABBITMQ_QUEUE_NAME_RBMDATACOLLECTION;
	
	public void rbmRabbitMQServerSide(String threadName) {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection rabbitmqConnection;
			rabbitmqConnection = factory.newConnection();
			Channel channel = rabbitmqConnection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			System.out.println(" [*] "+ threadName +" server is waiting for training/testing data (user-movie-rating triple). To exit press CTRL+C");

			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_NAME, true, consumer);
			
			while (true) {
				QueueingConsumer.Delivery delivery = null;
				try {
					delivery = consumer.nextDelivery();
					String message = new String(delivery.getBody());
					System.out.println(" [...x...] "+ threadName +"server received '" + message.replaceAll("\\s+","") + "'");
				} catch (ShutdownSignalException | ConsumerCancelledException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					System.out.println(threadName+ " Rabbitmq is interrupted " + System.currentTimeMillis());
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
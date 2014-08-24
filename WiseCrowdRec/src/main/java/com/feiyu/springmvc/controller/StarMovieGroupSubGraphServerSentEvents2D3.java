package com.feiyu.springmvc.controller;
/**
 * @author feiyu
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.feiyu.utils.GlobalVariables;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

@WebServlet("/StarMovieGroupSubGraphServerSentEvents2D3")
public class StarMovieGroupSubGraphServerSentEvents2D3 extends HttpServlet implements ActionListener  {
	private static final long serialVersionUID = -2489334504273883211L;
	private final static String QUEUE_NAME = GlobalVariables.RABBITMQ_QUEUE_NAME_SMGSUBGRAPH;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		response.setContentType("text/event-stream;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");

		PrintWriter out = response.getWriter();

		// RabbitMQ
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] StarMovieGroupSubGraphServerSentEvents2D3: waiting for messages. To exit press CTRL+C");

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
			System.out.println(" [...x...] StarMovieGroupSubGraphServerSentEvents2D3 received: '" + message + "'");
			
			out.print("id: " + "smgSubGraphSSE2D3" + "\n");
			out.print("data: " + message + "\n\n");
			out.flush();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}
}

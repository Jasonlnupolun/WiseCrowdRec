/**
 * referenc http://www.html5rocks.com/en/tutorials/eventsource/basics/
 * just for testing, not my own, need to change this later
 */
package com.feiyu.springmvc.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/ServerSentEventsD3")
public class ServerSentEventsD3 extends HttpServlet implements ActionListener  {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/event-stream;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");

		PrintWriter out = response.getWriter();

		while (true) {
//			out.print("id: 500\n"); //Math.random()*width, y: Math.random()*height
//			out.print("data: 300\n\n");
//			out.flush();
			out.print("id: " + "EntityName" + "\n");
			out.print("data: " + new Date().toString() + "\n\n");
			out.flush();
//			out.print("id: " + "ServerTime" + "\n");
//			out.print("data: " + new Date().toString() + "\n\n");
//			out.flush();
			// out.close(); //Do not close the writer!
			try {
				Thread.currentThread();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

}
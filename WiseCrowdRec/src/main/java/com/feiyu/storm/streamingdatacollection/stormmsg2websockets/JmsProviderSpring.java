package com.feiyu.storm.streamingdatacollection.stormmsg2websockets;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import backtype.storm.contrib.jms.JmsProvider;

public class JmsProviderSpring implements JmsProvider {
	private static final long serialVersionUID = 7643754177565597421L;
	private ConnectionFactory _connectionFactory;
    private Destination _destination;
    
	public JmsProviderSpring (ConnectionFactory connectionFactory, Destination destination) {
		_connectionFactory = connectionFactory;
		_destination = destination;
	}

	@Override
	public ConnectionFactory connectionFactory() throws Exception {
		return _connectionFactory;
	}

	@Override
	public Destination destination() throws Exception {
		return _destination;
	}
}

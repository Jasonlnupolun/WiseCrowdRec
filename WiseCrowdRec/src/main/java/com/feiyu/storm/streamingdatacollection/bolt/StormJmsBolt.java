package com.feiyu.storm.streamingdatacollection.bolt;
/**
 * Reference https://github.com/ptgoetz/storm-jms/blob/a64d9365b4c04cbaf7e865e7eb21b4b9ca1d8939/examples/src/main/java/backtype/storm/contrib/jms/example/ExampleJmsTopology.java
 * @author feiyu
 */

import java.io.IOException;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiyu.elasticsearch.SerializeBeans2JSON;
import com.feiyu.springmvc.model.MovieWithCount;
import com.feiyu.storm.streamingdatacollection.stormmsg2websockets.JmsProviderSpring;

import backtype.storm.contrib.jms.JmsMessageProducer;
import backtype.storm.contrib.jms.JmsProvider;
import backtype.storm.contrib.jms.bolt.JmsBolt;
import backtype.storm.tuple.Tuple;

public class StormJmsBolt implements java.io.Serializable {
	private static Logger log= Logger.getLogger(ForTestGetMovieDataBolt.class.getName());
	private static final long serialVersionUID = 8795229050766399663L;
	SerializeBeans2JSON sb2json = new SerializeBeans2JSON();
	MovieWithCount movieWithCount = new MovieWithCount();

	public JmsBolt jmsBolt() {
		JmsBolt jmsBolt = new JmsBolt();

		// set Jms Provider
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("SpringApplicationContext.xml");
		JmsProvider jmsProvider = new JmsProviderSpring(
				(ConnectionFactory)applicationContext.getBean("jmsConnectionFactory"),
				(Destination)applicationContext.getBean("notificationQueue"));
		jmsBolt.setJmsProvider(jmsProvider);

		// set Jms Message Producer
		JmsMessageProducer jmsMessageProducer = new JmsMessageProducer() {
			private static final long serialVersionUID = 7895970552658437305L;
			@Override
			public Message toMessage(Session session, Tuple tuple) throws JMSException {
				movieWithCount = (MovieWithCount) tuple.getValueByField("movieWithCount");
				String jsonMsg = null; // @@@ note json message can be null
				try {
					jsonMsg = sb2json.serializeBeans2JSON(movieWithCount);
				} catch (IOException e) {
					e.printStackTrace();
				}
				log.debug("jsonMsg----=======+++++++-> "+ jsonMsg);
				return session.createTextMessage(jsonMsg);//@@@ lots of options
			}
		};
		jmsBolt.setJmsMessageProducer(jmsMessageProducer);

		return jmsBolt;
	}
}

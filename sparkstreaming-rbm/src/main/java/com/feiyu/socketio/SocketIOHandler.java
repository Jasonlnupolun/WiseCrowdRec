package com.feiyu.socketio;

/**
 * @author Fei Yu (@faustineinsun)
 * http://stackoverflow.com/questions/8742511/sending-a-message-to-all-browsers-using-java-based-socket-io-client-for-netty
 * https://github.com/mrniko/netty-socketio/issues/87
 */

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.feiyu.utils.GlobalVariables;

import java.io.IOException;

//import org.json.simple.parser.ParseException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class SocketIOHandler {
  static SocketIOServer server;

  private static void rabbitMQEventListener(String QUEUE_NAME, String eventIdentifier) {
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      com.rabbitmq.client.Connection rabbitmqConnection;
      rabbitmqConnection = factory.newConnection();
      Channel channel = rabbitmqConnection.createChannel();
      channel.queueDeclare(QUEUE_NAME, false, false, false, null);
      System.out.println(" [*] "+QUEUE_NAME+" server is waiting for messages. To exit press CTRL+C");

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
        System.out.println(" [...x...] "+QUEUE_NAME+" server received '" + message + "'");

        server.getBroadcastOperations().sendEvent(eventIdentifier, message);

      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  private void runMultiThreads() {
    Thread PanelTweetThread = new Thread () {
      public void run () {
        try {
          SocketIOHandler.rabbitMQEventListener(GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_TWEET, "paneltweetsi");
        } catch (Exception e) {
          e.printStackTrace();
        } 
      }
    };
    PanelTweetThread.start();

    Thread PanelExtractedPersonNameThread = new Thread () {
      public void run () {
        try {
          SocketIOHandler.rabbitMQEventListener(GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_PERSONNAME, "panelpersonnamesi");
        } catch (Exception e) {
          e.printStackTrace();
        } 
      }
    };
    PanelExtractedPersonNameThread.start();

    Thread PanelSparkTopKHistogramThread = new Thread () {
      public void run () {
        try {
          SocketIOHandler.rabbitMQEventListener(GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_PSN_HISTOGRAM, "paneltopkpersonsi");
        } catch (Exception e) {
          e.printStackTrace();
        } 
      }
    };
    PanelSparkTopKHistogramThread.start();

    Thread PanelRelatedMovieThread = new Thread () {
      public void run () {
        try {
          SocketIOHandler.rabbitMQEventListener(GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_MOVIE, "panelrelatedmoviesi");
        } catch (Exception e) {
          e.printStackTrace();
        } 
      }
    };
    PanelRelatedMovieThread.start();

    Thread PanelRecThread = new Thread () {
      public void run () {
        try {
          SocketIOHandler.rabbitMQEventListener(GlobalVariables.RABBITMQ_QUEUE_NAME_PANEL_REC, "panelrecsi");
        } catch (Exception e) {
          e.printStackTrace();
        } 
      }
    };
    PanelRecThread.start();

    Thread ShowStarMoviesCategoriesGraphThread = new Thread () {
      public void run () {
        try {
          SocketIOHandler.rabbitMQEventListener(GlobalVariables.RABBITMQ_QUEUE_NAME_SMCSUBGRAPH, "showsmcgraphsi");
        } catch (Exception e) {
          e.printStackTrace();
        } 
      }
    };
    ShowStarMoviesCategoriesGraphThread.start();
  }

  public void start() {
    Configuration config = new Configuration();
    //config.setHostname("146.148.72.155");
    //config.setHostname("127.0.0.1");
    config.setHostname("0.0.0.0");
    config.setPort(9888);
    server = new SocketIOServer(config);

    server.addConnectListener(new ConnectListener() {
      @Override
      public void onConnect(SocketIOClient client) {
        System.out.println("is connected "+client.getSessionId());
        
        /*
        String amgGraphJson = GlobalVariables.JEDIS_OPS.getLatestActorMoviesGenreGraphJson();
        if (amgGraphJson != null) {
          try {
            GlobalVariables.RABBITMQ_CHANNEL.basicPublish("", GlobalVariables.RABBITMQ_QUEUE_NAME_SMCSUBGRAPH, null, amgGraphJson.getBytes());
          } catch (IOException e) {
            System.out.println("GlobalVariables.RABBITMQ_QUEUE_NAME_SMCSUBGRAPH publish error!");
          }
        }
        */
      }
    });

    server.addDisconnectListener(new DisconnectListener() {
      @Override
      public void onDisconnect(SocketIOClient client) {
        System.out.println("is disconnected "+client.getSessionId());
      }
    });

    server.start();

    this.runMultiThreads();

    //server.stop();
  }
  public static void main(String[] argv) {
    SocketIOHandler io = new SocketIOHandler();
    io.start();
  }
}

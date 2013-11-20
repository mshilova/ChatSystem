package edu.ucsd.cse110.client;

import java.net.URISyntaxException;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;

public class ChatClientApplication {

	private static ActiveMQConnection connection;
	private static ChatClient client;

	/*
	 * This inner class is used to make sure we clean up when the client closes
	 */
	static private class CloseHook extends Thread {
		ActiveMQConnection connection;
		private CloseHook(ActiveMQConnection connection) {
			this.connection = connection;
		}
		
		public static Thread registerCloseHook(ActiveMQConnection connection) {
			Thread ret = new CloseHook(connection);
			Runtime.getRuntime().addShutdownHook(ret);
			return ret;
		}
		
		public void run() {
			try {
				System.out.println("Closing ActiveMQ connection");
				connection.close();
			} catch (JMSException e) {
				/* 
				 * This means that the connection was already closed or got 
				 * some error while closing. Given that we are closing the
				 * client we can safely ignore this.
				*/
			}
		}
	}

	
	/*
	 * This method wires the client class to the messaging platform
	 * Notice that ChatClient does not depend on ActiveMQ (the concrete 
	 * communication platform we use) but just in the standard JMS interface.
	 */
	private static ChatClient wireClient() throws JMSException, URISyntaxException {
		connection = ActiveMQConnection.makeConnection(
				/*currentUser, currentPassword,*/ Constants.ACTIVEMQ_URL);
        connection.start();
        CloseHook.registerCloseHook(connection);
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // for producing messages
        MessageProducer producer =  session.createProducer(null);
        // for consuming messages
        Queue incomingQueue = session.createTemporaryQueue();
        MessageConsumer consumer = session.createConsumer(incomingQueue);
        
        // topic for client to publish broadcast messages and subscribe
        TopicSession topicSession = connection.createTopicSession(
        		false, TopicSession.AUTO_ACKNOWLEDGE);
        Topic broadcastTopic = topicSession.createTopic(Constants.BROADCAST);
        TopicPublisher publisher = topicSession.createPublisher(broadcastTopic);
        TopicSubscriber subscriber = topicSession.createSubscriber(broadcastTopic);
        
        return new ChatClient(incomingQueue,session,producer,consumer,
        		topicSession,publisher,subscriber);
	}
	
	/**
	 * Terminal output instructions on how to format input commands
	 */
	public static void printHelp() {
		System.out.println("# Type 'listOnlineUsers' to list all online users.");
		System.out.println("# Type 'listChatRooms' to list all chat rooms.");
		System.out.println("# Type 'createChatRoom' followed by the name of the chat room to create a chat room.");
		System.out.println("# Type 'broadcast' followed by your message to broadcast to all online users.");
		System.out.println("# Type 'send' followed by a user-name and then your message to send that message to that user.");
		System.out.println("# Type 'send' followed by multiple user-names "
				+ "separated by commas and without spaces, followed by your "
				+ "message, to send your message to multiple users.");
		System.out.println("# Type 'exit' to close the program.");
	}
	
	
	public static void main(String[] args) {
		
		try {
			client = wireClient();
			System.out.println("ChatClient wired.");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			client.processUserInput();	
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println( "An internal error occurred and the system has crashed. Please log in again." );
			client.sendServer( Constants.LOGOFF, client.getUser() );
			System.exit(0);
		}
	
	}
	
	
	
	
}

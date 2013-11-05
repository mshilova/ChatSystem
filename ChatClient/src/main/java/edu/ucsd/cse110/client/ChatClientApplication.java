package edu.ucsd.cse110.client;

import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.Set;

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
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQQueue;

public class ChatClientApplication {

	private static ActiveMQConnection connection;
	private static User user = new User();
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
		connection = 
				ActiveMQConnection.makeConnection(
						user.userName,//Constants.USERNAME,
						user.password,//Constants.PASSWORD,
						Constants.ACTIVEMQ_URL);
        connection.start();
        CloseHook.registerCloseHook(connection);
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // queue for client to receive messages from server
        // queue name will be the user name
        Queue incomingQueue = session.createQueue("max");//Constants.USERNAME);
        // queue for client to send messages to server
        Queue destQueue = session.createQueue(Constants.SERVERQUEUE);
        MessageProducer producer =  session.createProducer(null);
        MessageConsumer consumer = session.createConsumer(incomingQueue);
        
        TopicSession topicSession = connection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
        // topic for client to publish broadcast messages
        Topic broadcastTopic = topicSession.createTopic(Constants.BROADCAST);
        TopicPublisher publisher = topicSession.createPublisher(broadcastTopic);
        TopicSubscriber subscriber = topicSession.createSubscriber(broadcastTopic);
        
        return new ChatClient(session,producer,consumer,incomingQueue,destQueue,topicSession,publisher,subscriber,broadcastTopic);
	}
	
	public static void main(String[] args) {
		try {
			user.setInfo();
			ChatClient client = wireClient();
	        System.out.println("ChatClient wired.");
	        // TODO GUI: add buttons to display options, such as
	        // Make Chat Rooms: YES/NO, Public / Private
	        
	        // e.g. type "Broadcast Hello" to send "Hello" to all online users.
	        System.out.println("Begin your message with 'Broadcast' to send it to all online users.");
	        System.out.println("Otherwise, send a message to a specific user by"
	        		+ " entering the recipient's user name followed by the message");
	        System.out.println("Type 'exit' to exit.");
	        
	        Scanner input = new Scanner(System.in);
	        String inputMessage;
	        System.out.println("Enter a message:");
	        while(true) {
	        	
	        	inputMessage = input.nextLine();
	        
	        	if(inputMessage.equalsIgnoreCase("exit")) {
	        		input.close();
	        		System.exit(0);
	        	} else if(inputMessage.startsWith("Broadcast") || inputMessage.startsWith("broadcast")) {
	        		// broadcast message
	        		client.send("Broadcast", inputMessage.substring(inputMessage.indexOf(" ")+1));
//	        		System.exit(0);

	        	} else {
	        		// TODO get all online users
	        	
	        		String toUser = inputMessage.substring(0, inputMessage.indexOf(" "));
	        		// check that the user you want to send to is a valid user
	        		DestinationSource destSource = connection.getDestinationSource();
	        		Set<ActiveMQQueue> queueList = destSource.getQueues();
	        		boolean validUser = false;
	        		for(ActiveMQQueue queue : queueList) {
	        			if(queue.getQueueName().equals(toUser)) {
	        				validUser = true;
	        				client.send(toUser,
	        						inputMessage.substring(inputMessage.indexOf(" ")+1));
	        				break;
		        		}
		     		}
	        		if(!validUser) {
	        			System.out.println("User " + toUser + " is not online or does not exist.");
	        		}
 	     			
//		        	System.exit(0);
	        	}
	        	
	        }
	    } catch (JMSException e) {
	    	// TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (URISyntaxException e) {
	    	// TODO Auto-generated catch block
	        e.printStackTrace();
		}
	}
}

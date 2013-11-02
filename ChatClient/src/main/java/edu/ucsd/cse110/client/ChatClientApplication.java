package edu.ucsd.cse110.client;

import java.net.URISyntaxException;
import java.util.Scanner;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;

public class ChatClientApplication {

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
		ActiveMQConnection connection = 
				ActiveMQConnection.makeConnection(
				/*Constants.USERNAME, Constants.PASSWORD,*/ Constants.ACTIVEMQ_URL);
        connection.start();
        CloseHook.registerCloseHook(connection);
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // queue for client to receive messages from server
        Queue incomingQueue = session.createQueue(Constants.QUEUENAME);
        // queue for client to send messages to server
        Queue destQueue = session.createQueue(Constants.SERVERQUEUE);
        // creating MessageConsumer to consumer messages from incomingQueue
        MessageConsumer consumer = session.createConsumer(incomingQueue);
        MessageProducer producer = session.createProducer(destQueue);
        return new ChatClient(consumer,producer,session);
	}
	
	public static void main(String[] args) {
		try {
			
			/* 
			 * We have some other function wire the ChatClient 
			 * to the communication platform
			 */
			ChatClient client = wireClient();
	        System.out.println("ChatClient wired.");
	        // TODO add a buttons to display options, such as
	        // Broadcast: YES, NO
	        // Make Chat Rooms: YES/NO, Public / Private
	        System.out.println("Broadcast message? 'Yes' or 'No'");
	        Scanner input = new Scanner(System.in);
	        String messageType = input.nextLine();
	        
	        // TODO if its not a broadcast
	        if(messageType.equalsIgnoreCase("No")) {
	        	// TODO get where to send the message
	        	
	        	input.close();
	        }
	        else if(messageType.equalsIgnoreCase("Yes")) {
	        	//System.out.println("enter the message: ");
	        	// TODO input of messages from the key-board
	        	client.send("Broadcast","Hello World");
				System.out.println("Message Sent!");
				input.close();
		        System.exit(0);
	        }else {
	        	System.out.println("Invalid option, please try again later");
	        	input.close();
	        	System.exit(-1);
	        }
			/* 
			 * Now we can happily send messages around
			 */
//			client.send("Hello World");
//			System.out.println("Message Sent!");	
//	        System.exit(0);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

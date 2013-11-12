package edu.ucsd.cse110.client;

import java.net.URISyntaxException;
import java.util.Scanner;

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
		Scanner input = new Scanner(System.in);
		
		System.out.println("Would you like to use the GUI? (yes/no)");
		if(input.nextLine().equalsIgnoreCase("yes")) {
			/*
			 * TODO GUI stuff
			 */
		}
		
        String currentUser = null;
        String currentPassword = null;
        
        boolean answered = false;
        do {
	        System.out.println("Existing user? (yes/no)");
	        String existingReply = input.nextLine();
	        if(answered = existingReply.equalsIgnoreCase("yes")) {
	        	 // verify what the user input as user-name and password
	            do {
	            	
	    			System.out.print("User-name: ");
	    			currentUser = input.nextLine();
	    			System.out.print("Password: ");
	    			currentPassword = input.nextLine();
	    			
	    			client.verifyUser(currentUser, currentPassword);
	    			
	    			// wait for a response from the server
	    			try{
	    				Thread.sleep(1000);
	            	} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	
	            	if(client.verified) continue;
	            	System.out.println("Log in error. Please try again.");
	            	
	            } while(!client.verified);
	      
	            System.out.println("Log in successful. " + "Welcome " + currentUser + ".");
	            client.setUser(currentUser);
	        	
	        } else if(answered = existingReply.equalsIgnoreCase("no")) {
	        	do {
		        	System.out.println("Registering a new user.");
		        	System.out.print ("Please provide a user-name: ");
		        	currentUser = input.nextLine();
		        	System.out.print("Please provide a password: ");
		        	currentPassword = input.nextLine();
		        	
		        	client.registerUser(currentUser, currentPassword);
	        	
	    			// wait for a response from the server
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(client.registered) continue;
					System.out.println("Registration error. Please try again.");
					
	        	} while(!client.registered);
	        	
	        	System.out.println("Registration successful. " + "Welcome " + currentUser + ".");
	            client.setUser(currentUser);
	            
	        } else {
	        	System.out.println("Invalid input. Please enter 'yes' or 'no'.");
	        }
        } while(!answered);
		
		System.out.println("# Type 'help' for the list of available commands.");
		String inputMessage;
		
		while(true) {
			System.out.print("Input: ");
			inputMessage = input.nextLine();
			
			if(inputMessage.startsWith("help")) {
				// display the help message
				printHelp();
				
			} else if(inputMessage.startsWith("exit")) {
				// go off-line
				client.sendServer(Constants.LOGOFF, client.getUser());
				input.close();
				
			} else if(inputMessage.startsWith("listOnlineUsers")) {
				// list all online users
				client.listOnlineUsers();
				
			} else if(inputMessage.startsWith("listChatRooms")) {
				// list all chat rooms
				client.listChatRooms();
				
			} else if(inputMessage.startsWith("broadcast")) {
				// broadcast the message
				inputMessage = inputMessage.substring("broadcast".length()+1);
				client.broadcast(inputMessage);
				
			} else if(inputMessage.startsWith("createChatRoom")) {
				// create a chat-room
				inputMessage = inputMessage.substring("chatRoom".length()+1);
				inputMessage = inputMessage.substring(0,inputMessage.indexOf(" "));
				client.createChatRoom(inputMessage);
			
			} else if(inputMessage.startsWith("send")) {
				// send a message to a specific user
				inputMessage = inputMessage.substring("send".length()+1);
				String userList = inputMessage.substring(0,inputMessage.indexOf(" "));
				String[] mailingList = userList.split(",");
				for(String recipient : mailingList) {
					client.send(recipient,
							inputMessage.substring(inputMessage.indexOf(" ")+1));
				}
				
			} else {
				// invalid input, display input instructions again
				System.out.println("Client did not recognize your input. Please try again.");
				System.out.println("# Type 'help' for the list of commands");
			}
		}
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
	
}

package edu.ucsd.cse110.client;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

public class ChatClient implements MessageListener {
	
	private Map<String,Destination> onlineUsers; // map of all online users, updated through observer pattern
	private String currentUser;	// used to identify this user when sending
	
	public boolean verified = false;
	public boolean registered = false;
	private Queue incomingQueue;
	private Session session;
	private MessageConsumer consumer;
	private MessageProducer producer;
	private TopicSession topicSession;
	private TopicPublisher publisher;
	private TopicSubscriber subscriber;
	
	public ChatClient(
			Queue incomingQueue, 
			Session session,
			MessageProducer producer, 
			MessageConsumer consumer,
			TopicSession topicSession, 
			TopicPublisher publisher, 
			TopicSubscriber subscriber) {
		super();
		this.incomingQueue = incomingQueue;
		this.session = session;
		this.producer = producer;
		this.consumer = consumer;
		this.topicSession = topicSession;
		this.publisher = publisher;
		this.subscriber = subscriber;
		onlineUsers = new HashMap<String,Destination>();
		currentUser = null;
		try {
			this.consumer.setMessageListener(this);
			this.subscriber.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param username	the user-name of the currently logged on user
	 */
	public void setUser(String username) {
		currentUser = username;
	}
	
	public String getUser(){
		return this.currentUser;
	}
	
	/**
	 * Broadcast a message to all users
	 * @param inputMessage	the message to broadcast
	 */
	public void broadcast(String inputMessage) {
	    try {
		Message message = topicSession.createTextMessage(inputMessage);
		message.setJMSType(currentUser);
		message.setJMSReplyTo(incomingQueue);
		publisher.publish(message);
		System.out.println("Message broadcasted.");
	    } catch (JMSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	
	
	/**
	 * 
	 */
	public void sendServer(String jmsType, String inputMessage) {
		try {
			Message message = session.createTextMessage(inputMessage);
			message.setJMSType(jmsType);
			message.setJMSReplyTo(incomingQueue);
			producer.send(session.createQueue(Constants.SERVERQUEUE), message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Send a message to a specific user
	 * @param username		the user-name of the message recipient
	 * @param inputMessage	the message to send to that user
	 */
	public void send(String username, String inputMessage) {
	    try {
		// retrieve the address associated with the recipient's user-name
		if(onlineUsers.containsKey(username)) {
			// TODO Auto-generated catch block
		    Message message = session.createTextMessage(inputMessage);
		    message.setJMSType(currentUser);
		    Destination dest = onlineUsers.get(username);
		    producer.send(dest, message);
		    System.out.println("Message sent to " + username + ".");
		} else {
		    System.out.println(username + " is not online.");
		}	
	    } catch (JMSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	
	
	/**
	 * @param username	what the user entered as his/her user-name
	 * @param password	what the user entered as his/her password
	 */
	public void verifyUser(String username, String password) {
	    sendServer(Constants.VERIFYUSER, username + " " + password);
	}
	
	
	/**
	 * @param username	what the user entered to be his new user-name
	 * @param password	what the user entered to be his new password
	 */
	public void registerUser(String username, String password) {
		sendServer(Constants.REGISTERUSER, username + " " + password);
	}
	
	
	/**
	 * Contact the server to request a list of all users who are online
	 */
	public void listOnlineUsers() {
	    for(String key : onlineUsers.keySet()) {
		System.out.println(key);
	    }
	}
	
	
	/**
	 * Contact the server and print to request a list of all chat rooms
	 */
	public void listChatRooms() {
		Message message;
		try {
			message = session.createTextMessage(incomingQueue.toString());
			message.setJMSType(Constants.LISTCHATROOMS);
			message.setJMSReplyTo(this.incomingQueue);
			producer.send(session.createQueue(Constants.SERVERQUEUE), message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Create a new chat-room
	 * @param name	the name of the chat-room
	 */
	public void createChatRoom(String name) {
		// TODO tell server to create a chat room
	}
	
	
	/**
	 * @param chatRoom	the name of the chat room the user is trying to leave
	 */
	public void leaveChatRoom(String chatRoom) {
		// TODO leave the chat room user is in
	}
	
	
	/**
	 * What to do when this chat client receives a message
	 * @param message	the message received
	 */
	@SuppressWarnings("unchecked")
	public void onMessage(Message message) {
	    try {
		String type = message.getJMSType();
		
		if(Constants.ONLINEUSERS.equals(message.getJMSType()) ){
		    onlineUsers = (HashMap<String, Destination>) (( (ObjectMessage) message ).getObject());

		}else if (type.equals(Constants.VERIFYUSER)){
		    verified = message.getBooleanProperty(Constants.RESPONSE);
		    
		}else if (type.equals(Constants.REGISTERUSER)){
		    registered = message.getBooleanProperty(Constants.RESPONSE);
		    
		//}else if (type.equals(Constants.LOGOFF)){
			//if(message.getBooleanProperty(Constants.RESPONSE)){
				//System.exit(0);
			//}
	    }else{
		    System.out.println("\nFrom " + type + ": " + 
			    		((TextMessage)message).getText());
		    System.out.print("Input: ");
		}
	    }catch(JMSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	
}



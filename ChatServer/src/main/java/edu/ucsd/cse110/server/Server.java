package edu.ucsd.cse110.server;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;



import javax.jms.TextMessage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class Server {

	protected LoginManager manager;
	private Authenticator authenticator;
	private String messageType;
	private MessageProcessor processor;
	
	/**
	 * Constructor
	 */
	public Server() {
	    	manager = LoginManager.getInstance();
	    	authenticator = Authenticator.getInstance();
	    	processor = new MessageProcessor();
	    	messageType = null;
	}
	
	
	/**
	 * This method is called when the server receives a message from a client
	 * producer
	 * @param message	the message received
	 * @throws Exception if the JMSType of the message is unknown
	 */
	public void receive(Message message) throws Exception {
		System.out.println("Server received a message.");
		boolean update = false;
		
		try {
		    messageType = message.getJMSType();
		
		    if(messageType.equals(Constants.VERIFYUSER)) {
		    	update = manager.logInUser(message);
		    	send(message.getJMSReplyTo(), update , Constants.VERIFYUSER);
		    } else if(messageType.equals(Constants.REGISTERUSER)) {
		    	update = authenticator.registerUser(message);
		    	send(message.getJMSReplyTo(), update, Constants.REGISTERUSER);
		    	if(update) {
			    	update = manager.logInUser(message);
			    	send(message.getJMSReplyTo(), update, Constants.VERIFYUSER);
		    	}
		    } else if(messageType.equals(Constants.CREATECHATROOM)) {    	
		    	String roomName = processor.oneArg(message);
		    	update = !ChatRoomManager.chatRoomExists( roomName );
		    	send( message.getJMSReplyTo() , update, Constants.CREATECHATROOM ); 		  
		    	if(update){
		    		ChatRoomManager.addChatRoom(new ChatRoom( roomName )); 
		    		update = false;
		    	}
		    } else if ( Constants.ACCEPTEDINVITE.equals( messageType ) ) {
		    	String roomAndUser[] = processor.twoArgs(message);
		    	update = ChatRoomManager.chatRoomExists(roomAndUser[0]);
		    	send( message.getJMSReplyTo(), update, Constants.ACCEPTEDINVITE );
		    	if(update){
		    		ChatRoomManager.addUserToRoom(roomAndUser[1], roomAndUser[0]);
		    		update = false;
		    	}    
		    } else if(messageType.equals(Constants.LOGOFF)) {
		    	update = manager.logOffUser(message);
		    	System.out.println(update);
		    } else {
			throw new Exception("Server received a message " 
					   +"with unrecognized jms type.");
		    }
		}catch(JMSException e){ e.printStackTrace(); }
		
		if(update){
		    Map<String, Destination> userMap = manager.getAllOnlineUsers();
		    Map<String, Destination> toSend = manager.getAllOnlineUsers();
		    for(String key : userMap.keySet()){
		    	send(userMap.get(key), toSend, Constants.ONLINEUSERS);
		    }
		}
	}
	

	/**
	 * Sends to the client a reply to a previously received request
	 * @param jmsType
	 * @param message
	 */

	public void send(Destination recipient, final String message, final String type) {
		
		JmsTemplate jmsTemplate = ChatServerApplication.context.getBean(JmsTemplate.class);
		
		MessageCreator messageCreator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message ret = session.createTextMessage(message);
				ret.setJMSType(type);
				return ret;
			}
		};
		try {		
			jmsTemplate.send(((Queue)recipient).getQueueName(), messageCreator);
			System.out.println("Server sent a response.");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Sends to the client a reply to a previously received request
	 * @param jmsType
	 * @param message
	 */

	public void send(Destination recipient, final boolean success, final String type) {
			
		JmsTemplate jmsTemplate = ChatServerApplication.context.getBean(JmsTemplate.class);
		MessageCreator messageCreator = new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					Message ret = session.createTextMessage();
					ret.setJMSType(type);
					ret.setBooleanProperty(Constants.RESPONSE, success);
					return ret;
				}
		};
		try {
			jmsTemplate.send(((Queue)recipient).getQueueName(), messageCreator);
			System.out.println("Server sent a response.");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	public void send(Destination recipient, final Map<String, Destination> onlineUsers, final String type) {
		
		JmsTemplate jmsTemplate = ChatServerApplication.context.getBean(JmsTemplate.class);	
		MessageCreator messageCreator = new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage objectMessage = session.createObjectMessage((Serializable) onlineUsers );
					objectMessage.setJMSType(type);
					return objectMessage;
				}
		};
		try {
			jmsTemplate.send(((Queue)recipient).getQueueName(), messageCreator);
			System.out.println("Server sent a response.");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
}

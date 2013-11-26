package edu.ucsd.cse110.server;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class Server {

	private LoginManager loginManager;
	private ChatRoomManager chatRoomManager;
	private Authenticator authenticator;
	private String messageType;
	
	/**
	 * Constructor
	 */
	public Server() {
	    loginManager = LoginManager.getInstance();
	    authenticator = Authenticator.getInstance();
	    chatRoomManager = ChatRoomManager.getInstance();
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
		
		    switch(messageType){
		  
		    	case Constants.VERIFYUSER:
		    		update = loginManager.addItem(message);
		    		System.out.println( "Verification: " + update );
		    		send(message.getJMSReplyTo(), update , Constants.VERIFYUSER);
		    		break;
		    		
		    	case Constants.REGISTERUSER:
		    		update = authenticator.registerUser(message);
		    		send(message.getJMSReplyTo(), update, Constants.REGISTERUSER); 
		    		update = loginManager.addItem(message);
		    		send(message.getJMSReplyTo(), update, Constants.VERIFYUSER);
		    		break;
		    		
		    	case Constants.CREATECHATROOM:
		    		update = chatRoomManager.addItem(message);
		    		send(message.getJMSReplyTo(), update, Constants.CREATECHATROOM);
		    		break;
		    		
		    	case Constants.ACCEPTEDINVITE:
		    		updateChatRoom(chatRoomManager.addUser(message));
		    		break;
		    		
		    	case Constants.LEAVECHATROOM:  
		    		updateChatRoom(chatRoomManager.removeUser(message));
		    		chatRoomManager.update();
		    		break;
		    		
		    	case Constants.LOGOFF:
		    		update = loginManager.removeItem(message);
		    		break;
		    		
		    	default:
		    		throw new Exception("Server received a message " 
		    						   +"with unrecognized jms type.");	    
		    }
		}catch(JMSException e){ e.printStackTrace(); }
		
		updateOnlineUsers( update );
		
	}
	
	
	public void updateChatRoom(ChatRoom room){
		if(room == null)
			return;	
		
		Map<String, Destination> recipients = room.getAllUsers();
		
		for(String user : recipients.keySet()){
			send(recipients.get(user), room, Constants.CHATROOMUPDATE);
		}
		
	}
	
	
	public void updateOnlineUsers(boolean update){
		if(update){
	
			Map<String, Destination> userMap = (Map<String, Destination>) loginManager.getAllItems();
	    
			for(String key : loginManager.getAllItems().keySet()){
				send(userMap.get(key), userMap, Constants.ONLINEUSERS);
			}
		}
	}
	/**
	 * Sends to the client a reply to a previously received request
	 * @param jmsType
	 * @param message
	 */

	public void send(Destination recipient, final ChatRoom room, final String type) {
		
		JmsTemplate jmsTemplate = ChatServerApplication.context.getBean(JmsTemplate.class);
		
		MessageCreator messageCreator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message ret = session.createObjectMessage(room);
				ret.setJMSType(type);
				return ret;
			}
		};
		try {		
			jmsTemplate.send(((Queue)recipient).getQueueName(), messageCreator);
			System.out.println("Server sent a response.1");
		} catch (JMSException e) { e.printStackTrace(); }
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
			System.out.println("Server sent a response.2");
		} catch (JMSException e) { e.printStackTrace(); }
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
			System.out.println("Server sent a response.3");
		} catch (JMSException e) { e.printStackTrace(); }
	}	
}

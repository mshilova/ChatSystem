package edu.ucsd.cse110.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class Server implements IServer{

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
		    		updateChatRooms( chatRoomManager.getAllItems() );
		    		
		    		break;
		    		
		    	case Constants.INVITATION:
		    		if ( validInvitation( message ) )
		    			sendInvite( message );
		    		else
		    			userIsAlreadyInRoom( message );
		    		break;
		    		
		    	case Constants.ACCEPTEDINVITE:
		    		updateChatRoom(chatRoomManager.addUser(message));
		    		break;
		    	
		    	case Constants.USERSINCHATROOM:
		    		ChatRoom room = chatRoomManager.getRoom( message );
		    		send( message.getJMSReplyTo(), room.getAllUsers(), Constants.USERSINCHATROOM );
		    		break;
		    		
		    	case Constants.LEAVECHATROOM:  
		    		updateChatRoom(chatRoomManager.removeUser(message));
		    		chatRoomManager.update();
		    		updateChatRooms( chatRoomManager.getAllItems() ); 
		    		
		    		break;
		    		
		    	case Constants.LOGOFF:
		    		update = loginManager.removeItem(message);
		    		break;
		    		
		    	case Constants.UPDATEALLCHATROOMS:
		    		updateChatRoomsForUser( chatRoomManager.getAllItems(), ((TextMessage)message).getText() );
		    		break;
		    		
		    	default:
		    		throw new Exception("Server received a message " 
		    						   +"with unrecognized jms type.");	    
		    }
		}catch(JMSException e){ e.printStackTrace();}
		
		updateOnlineUsers( update );
		
		if ( update ) 
    		updateChatRooms( chatRoomManager.getAllItems() );
		
	}
	
	
	public void sendInvite( Message message ) throws JMSException {
		
		String userAndRoom[] = ((TextMessage) message).getText().split( " " );
		Map<String, Destination> recipients = loginManager.getAllItems();
		Destination dest = recipients.get( userAndRoom[0] );
		String user = null; //the user who is sending the invite
		Set<String> onlineUsers = recipients.keySet();
		Destination sentFrom = message.getJMSReplyTo();
		
		 for ( String key : onlineUsers ) {
		        if (sentFrom.equals(recipients.get( key ))) 
		            user = key;
		        
		 }
		 
		String userRoom = user + " " + userAndRoom[1];
		
		sendInvite( dest, Constants.INVITATION, userRoom );
		
	}
	
	
	public void sendInvite( Destination recipient, final String type, final String message ) {
		
		JmsTemplate jmsTemplate = ChatServerApplication.context.getBean(JmsTemplate.class);
		
		MessageCreator messageCreator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				TextMessage ret = session.createTextMessage();
				ret.setText( message );
				ret.setJMSType(type);
				ret.setBooleanProperty( Constants.RESPONSE, true );
				return ret;
			}
		};
	
		try {
			jmsTemplate.send(((Queue)recipient).getQueueName(), messageCreator);
			System.out.println("Server sent a response.0");
		} catch (JMSException e) { e.printStackTrace(); }
	}
	
	
	public void userIsAlreadyInRoom( Message message ) throws JMSException {
		
		send( message.getJMSReplyTo(), false, Constants.INVITATION );

	}
	
	
	public boolean validInvitation( Message message ) throws JMSException {
		
		String userAndRoom[] = ((TextMessage) message).getText().split( " " );
		ChatRoom room = chatRoomManager.getRoom( userAndRoom[1] );
		
		System.out.println( userAndRoom[0] );
		System.out.println( room.containsUser(userAndRoom[0]));
		System.out.println( "Got this far, should be Kacy and true");
		
		if ( room.containsUser( userAndRoom[0] ) )
			return false;
		
		return true;
		
	}
	
	public boolean updateChatRoom(ChatRoom room){
		if(room == null)
			return false;	
		
		Map<String, Destination> recipients = room.getAllUsers();
		
		for(String user : recipients.keySet()){
			send(recipients.get(user), room, Constants.CHATROOMUPDATE);
		}
		return true;
	}
	
	
	public boolean updateChatRooms( ArrayList<String> listOfRooms ){
		
		Map<String, Destination> userMap = (Map<String, Destination>) loginManager.getAllItems();
	    
		for(String key : loginManager.getAllItems().keySet()){
			send(userMap.get(key), listOfRooms, Constants.UPDATEALLCHATROOMS );
		}
		return true;
		
	}
	
	
	public void updateChatRoomsForUser( ArrayList<String> listOfRooms, String user ) {
		Map<String, Destination> userMap = (Map<String, Destination>) loginManager.getAllItems();
		
		send(userMap.get(user), listOfRooms, Constants.UPDATEALLCHATROOMS);
	}
	
	
	public boolean updateOnlineUsers(boolean update){
		if(update){
	
			Map<String, Destination> userMap = (Map<String, Destination>) loginManager.getAllItems();
	    
			for(String key : loginManager.getAllItems().keySet()){
				send(userMap.get(key), userMap, Constants.ONLINEUSERS);
			}
			return true;
		}
		return false;
	}
	
	
	
	
	/**
	 * Sends to the client a reply to a previously received request
	 * @param jmsType
	 * @param message
	 */

	public boolean send(Destination recipient, final ChatRoom room, final String type) {
		
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
		return true;
	}
		
	
	
	/**
	 * Sends to the client a reply to a previously received request
	 * @param jmsType
	 * @param message
	 */

	public boolean send(Destination recipient, final boolean success, final String type) {
			
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
		return true;
	}
	
	

	public void send(Destination recipient, final ArrayList<String> chatRooms, final String type) {
		
		JmsTemplate jmsTemplate = ChatServerApplication.context.getBean(JmsTemplate.class);	
		MessageCreator messageCreator = new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage objectMessage = session.createObjectMessage((Serializable) chatRooms );
					objectMessage.setJMSType(type);
					return objectMessage;
				}
		};
		try {
			jmsTemplate.send(((Queue)recipient).getQueueName(), messageCreator);
			System.out.println("Server sent a response.3");
		} catch (JMSException e) { e.printStackTrace(); }
	}	
	
	
	public boolean send(Destination recipient, final Map<String, Destination> onlineUsers, final String type) {
		
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
			System.out.println("Server sent a response.4");
		} catch (JMSException e) { e.printStackTrace(); }
		return true;
	}


	public LoginManager getLoginManager() {
		return loginManager;
	}


	public ChatRoomManager getChatRoomManager() {
		return chatRoomManager;
	}


	public Authenticator getAuthenticator() {
		return authenticator;
	}


	public String getMessageType() {
		return messageType;
	}	
}

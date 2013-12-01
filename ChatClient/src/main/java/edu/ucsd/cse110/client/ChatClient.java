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

import edu.ucsd.cse110.server.ChatRoom;

public class ChatClient implements MessageListener {
	
	/*
	 * 
	 * REFACTORED VARIABLES
	 * 
	 */
	private Map<String,Destination> onlineUsers; // map of all online users, updated through observer pattern
	private User user;
	private InputProcessor processor;
	private ChatClientGUI gui;
	private boolean usingGui;
	
	/*
	 * 
	 * 
	 *  EXISTING VARIABLES
	 * 
	 * 
	 */
	
	private Queue incomingQueue;
	private Session session;
	private MessageConsumer consumer;
	private MessageProducer producer;

	private ChatCommander chatCommander;

	
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
		this.processor = new InputProcessor();
		chatCommander = new ChatCommander( this, topicSession );
		chatCommander.addPublisher( publisher );
		chatCommander.addSubscriber( subscriber );
		onlineUsers = new HashMap<String,Destination>();
		user = new User();
		try {
			consumer.setMessageListener(this);
			subscriber.setMessageListener(this); 
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	
	public void run() throws JMSException {
		if(!useGui()) {
			userLogon();
			processor.processUserCommands( this );
		}
	}
	
	
	public Queue getQueue() {
		return incomingQueue;
	}
	
	public Session getSession() {
		return session;
	}
	
	public MessageProducer getProducer() {
		return producer;
	}
	
	
	/* 
	 * Starts the GUI, when the function returns we just exit because we used 
	 * the GUI for input. We don't want to start the text input after exiting 
	 * GUI mode.
	 */
	private boolean useGui() {	// yesNoPrompt is in InputProcessor
		if(processor.yesNoPrompt("Would you like to use the GUI? (yes/no)")){
			usingGui = true;
		    gui = new ChatClientGUI(this);
		    gui.start();
		    return true;
		}
		return false;
	}

	/**
	 *  Register a new user method 
	 */
	private String[] registerUser() {
		String[] responses = new String[2];
		  
		while( ! user.getVerified() ){
			System.out.println("Registering a new user.");
			responses = processor.twoPrompt("Please provide a username: ",
    	    								"Please provide a password: ");			  
			/* check for bad input.*/
			if( null == responses[0] || null == responses[1] || 
				responses[0].length() < Constants.MINFIELDLENGTH || 
			    responses[1].length() < Constants.MINFIELDLENGTH ){
				  System.out.println("Invalid Username or Password.");
				  continue;
			}
			  
			registerUser( responses[0], responses[1] );
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
        
        	if( ! user.getVerified() )    
        		System.out.println("Registration error. Please try again.");
          } 
          System.out.println("Registration successful. ");
          return responses;
	  }

	/**
	 *  Logon existing user method. its almost 20 lines. ALMOST.
	 */
	private void userLogon(){
		
		String[] responses = new String[2];  //1st argument is the username, 2nd is the password
		boolean existing;
	    existing = processor.yesNoPrompt("Existing user? (yes/no)");
	        
	    if(!existing){
	    	responses = registerUser();
	    }
	    else
	    while( ! user.getVerified() ){
	        responses = processor.twoPrompt("Username: ", "Password: ");
			
	        /* check for bad input.*/
			if( null == responses[0] || null == responses[1] || 
				responses[0].length() < Constants.MINFIELDLENGTH || 
			    responses[1].length() < Constants.MINFIELDLENGTH ){
				  System.out.println("Invalid Username or Password.");
				  continue;
			}
	        verifyUser( responses[0], responses[1] );        
	        try{ Thread.sleep(1000); } catch(InterruptedException e){ e.printStackTrace(); }

	        if( ! user.getVerified() ) 
	        	System.out.println("Log in error. Please try again.");	                  
	    }    
	    user.setUsername(responses[0]);
	    user.setPassword(responses[1]);
	 }
	
	
	
	
	public ChatCommander getChatCommander() {
		return chatCommander;
	}

	
	
	/**
	 * @param User user - A USER OBJECT
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * 
	 * @return blasphemous user object
	 */
	public User getUser(){
		return this.user;
	}
	

	
	
	public void sendServer(String jmsType, String inputMessage) {
		
		/**
		 * TODO removed this and added line to input processor.
		 * 		so that the right room name is added to the client
		 * 		room list.
		 */
		//if ( Constants.CREATECHATROOM.equals( jmsType ) )
		//	chatCommander.add( inputMessage );
		
		try {
			Message message = session.createTextMessage(inputMessage);
			message.setJMSType(jmsType);
			message.setJMSReplyTo(incomingQueue);
			producer.send(session.createQueue(Constants.SERVERQUEUE), message);
		} catch (JMSException e) {
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
			    Message message = session.createTextMessage(inputMessage);
			    message.setJMSType(Constants.MESSAGE);
			    message.setStringProperty("SENDER", username);
			    producer.send(onlineUsers.get(username), message);
			    System.out.println("Message sent to " + username + ".");
			} else {
			    System.out.println(username + " is not online.");
			}	
	    } catch (JMSException e) { e.printStackTrace(); }
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
	public Map<String,Destination> getOnlineUsers() {
		return onlineUsers;
	}
	
	/**
	 * Contact the server to request a list of all users who are online
	 * Print out that list of online users
	 */
	public void listOnlineUsers() {
		
	    for(String key : onlineUsers.keySet()) {
	    	System.out.println(key);
	    }
	}
	
	
	/**
	 * Check if a specific user in online
	 * @param user	user-name of user to check if he/she is online
	 * @return	boolean true if the user is online, false otherwise
	 */
	public boolean userOnline( String user ) {	
		if ( onlineUsers.containsKey( user ) )
			return true;	
		return false;
	}
	
	
	/**
	 * 
	 * @param user
	 * @return	the address of the user
	 */
	public Destination getDestination( String user ) {	
		return onlineUsers.get( user );	
	}

	
	/**
	 * What to do when this chat client receives a message
	 * @param message	the message received
	 */
	@SuppressWarnings("unchecked")
	public void onMessage(Message message) {
	    try {
	    	switch (message.getJMSType()){
	    	
	    	case Constants.ONLINEUSERS:
	    		onlineUsers = (HashMap<String, Destination>) (( (ObjectMessage) message ).getObject());
	    		break;
	    		
	    	case Constants.VERIFYUSER:
	    		user.setVerified( message.getBooleanProperty(Constants.RESPONSE) );
	    		break;
	    		
	    	case Constants.REGISTERUSER:
	    		user.setVerified( message.getBooleanProperty(Constants.RESPONSE) );
	    		break;	    	
	    		
	    	case Constants.CREATECHATROOM:
	    		if ( message.getBooleanProperty( Constants.RESPONSE ) ) {
	    			chatCommander.setupChatRoomTopic(); // last room name added to list will be created
	    			if(usingGui) {
						gui.getPanelWest().addChatRoom(chatCommander.chatRooms.get(chatCommander.chatRooms.size() - 1));
		    		}
	    		} else {
	    			chatCommander.removeLastRoomAdded();
	    			System.err.println( "Sorry, that room name already exists or is invalid.  Please choose a different name." );
	    		}
	    		break;
	    		
	    	case Constants.CHATROOMUPDATE:
	    		//TODO set the updated chatroom object somewhere
	    		// TODO import ChatRoom
	    		ChatRoom room = (ChatRoom) ((ObjectMessage) message).getObject();
	    		break;
	    		
	    	case Constants.INVITATION:
	    		String invite[] = ((TextMessage) message).getText().split( " " );
	    		if(usingGui) {	
	    			gui.getPanelWest().addChatRoomInvite(invite[1]);		
	    		} else {
	    		 
		    		System.out.println( "You've received an invitation from " + invite[0] + " to join the chat room: " + invite[1] );
		    		System.out.println( "Would you like to accept? Enter 'accept chatRoomName'" );
		    	}
	    		chatCommander.addPendingInvitation( invite[1] );  // invite[1] is the room name    
	    		break;
	    	
	    	case Constants.MESSAGE:
				if(usingGui) {
					gui.updateTextArea(message.getStringProperty("SENDER"), ((TextMessage)message).getText());
	    		} else {
	    			System.out.println("From " + message.getStringProperty("SENDER") + ": " + ((TextMessage)message).getText());
				}
				break;
			
	    	case Constants.ROOMMESSAGE:
				if(usingGui) {
					// TODO update text area of chat room
//					gui.updateTextArea(message.getStringProperty("SENDER"), ((TextMessage)message).getText());
	    		} else {
	    			System.out.println("In " + message.getStringProperty("ROOM")
	    					+ ": From " + message.getStringProperty("SENDER")
	    					+ ": " + ((TextMessage)message).getText());
				}
				break;
				
	    	case Constants.USERSINCHATROOM:
	    		if ( usingGui ) {
	    			// TODO Masha and Nobel will do something with this - Kacy
	    		} else {
		    		Map usersInChatRoom = (Map<String, Destination>) (( (ObjectMessage) message ).getObject());
		    		chatCommander.listUsersInChatRoom( usersInChatRoom );
	    		}
	    		break;
	    		
			default:
				throw new JMSException("Unrecognized JMSType");
	    	}
	    	
	    } catch(JMSException e) { e.printStackTrace(); }
	}

}

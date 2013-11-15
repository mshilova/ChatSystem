package edu.ucsd.cse110.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
import javax.jms.Topic;
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
	private ArrayList<TopicPublisher> publisherList = new ArrayList<TopicPublisher>();
	private ArrayList<TopicSubscriber> subscriberList = new ArrayList<TopicSubscriber>();
	private ArrayList<String> chatRooms = new ArrayList<String>();
	
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
		this.publisherList.add( publisher );
		this.subscriberList.add( subscriber );
		onlineUsers = new HashMap<String,Destination>();
		currentUser = null;
		try {
			this.consumer.setMessageListener(this);
			this.subscriberList.get( 0 ).setMessageListener(this); //setting broadcast's subscriber
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
		publisherList.get( 0 ).publish(message);  // using broadcast's publisher 
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
		
		if ( Constants.CREATECHATROOM.equals( jmsType ) )
			addToChatRoomList( inputMessage );
		
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
	
	public void addToChatRoomList( String name ) {
		chatRooms.add( name );
	}
	
	public void publishMessageToChatRoom( String room, String message ) throws JMSException {
		
		for ( TopicPublisher publisher : publisherList ) {
			if ( publisher.getTopic().getTopicName().equals( room ) ) {
				TextMessage text = topicSession.createTextMessage( message );
				text.setJMSType( currentUser );
				text.setJMSReplyTo( incomingQueue );
				publisher.publish( text );
				return;
			}
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
	
	public void sendInvitation( String user, String room ) throws JMSException {
		
		TextMessage message = session.createTextMessage( this.currentUser + " " + room );
		message.setJMSType( Constants.INVITATION );
		producer.send( getDestination( user ), message );
		System.out.println( "Invitation sent." );
		
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
	
	public boolean userOnline( String user ) {
		
		if ( onlineUsers.containsKey( user ) )
			return true;
		
		return false;
		
	}
	
	public Destination getDestination( String user ) {
		
		return onlineUsers.get( user );
		
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
	
	
	/* Checks if input starts with a chat room name and then returns that name, 
	 * else return null */
	
	public  boolean chatRoomEntered( String name ) {
		
		for ( String room : chatRooms ) {
			if ( room.equals( name ) ) 
				return true;
	 	}
		
		return false;
		
	}
	
	
		
	
	/**
	 * @param chatRoom	the name of the chat room the user is trying to leave
	 */
	public void leaveChatRoom(String chatRoom) {
		// TODO leave the chat room user is in
	}
	
	
	/* Creates the topic representing the chat room 
	 * and sets up the publisher and subscriber 
	 */
	
	public void setupChatRoomTopic( String room ) {
		
		try {
			Topic chatRoom = topicSession.createTopic( room );
			TopicSubscriber subscriber = topicSession.createSubscriber( chatRoom );
			TopicPublisher publisher = topicSession.createPublisher( chatRoom );
			
			subscriber.setMessageListener( this );
			this.subscriberList.add( subscriber );
			this.publisherList.add( publisher );
			
			System.out.println( "You are now connected to chat room: " + room );
			
		} catch (JMSException e) {
			e.printStackTrace();
			System.err.println( "There was a problem setting up the chat room" );
		}
		
	}
	
	public void inviteToChatRoom( String room, String user ) {
		
		if ( null == room  ) {
			System.err.println( "Please specify a room name." );
			return;
		}
		
		if ( null == user ) {
			System.err.println( "Please specify a user to invite." );
			return;
		}
		
		if ( room.contains( " " ) ) {
			System.err.println( "Room name may not contain a space character." );
			return;
		}
		
		if ( subscribedToChatRoom( room ) )
			sendServer( Constants.INVITATION, room + ' ' + user  ); // space character is a dilimeter
		else 
			return;
		
	}
	
	public boolean subscribedToChatRoom( String room ) {
		
		for ( String chatRoom : this.chatRooms ) {
			if ( chatRoom.equals( room ) )
				return true;
		}
		
		return false; //user is not in the room
		
	}	
	
	public void acceptInvite( String chatRoom ) throws JMSException {
		
		setupChatRoomTopic( chatRoom );
		chatRooms.add( chatRoom );
		
		sendServer( Constants.ACCEPTEDINVITE, this.currentUser + " " + chatRoom ); // necessary to add username to ChatRoom's list of users
		
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
	    } else if ( Constants.CREATECHATROOM.equals( type ) ) {
	    	//substring is used here because the actual chat room name was appended to the type in the server
	    	if ( message.getBooleanProperty( Constants.RESPONSE ) ) {
	    		setupChatRoomTopic( chatRooms.get( chatRooms.size() - 1 ) ); // last room name added will be created
	    	}
	    	else
	    		System.err.println( "Sorry, that room name already exists or is invalid.  Please choose a different name." );
	    }
	    else if ( Constants.INVITATION.equals( type ) ) {
	    	String invite[] = ((TextMessage) message).getText().split( " " ); 
	    	System.out.println( "You've received an invitation from " + invite[0] + " to join the chat room: " + invite[1] );
	    	System.out.println( "Would you like to accept? Enter 'yes' or 'no'" );
	    	
	    	Scanner input = new Scanner( System.in );
	    	String answer = input.nextLine();
	    	input.close();
	    	
	    	if ( "yes".equalsIgnoreCase( answer ) ) {
	    		acceptInvite( invite[1] );  // invite[1] is the room name 
	    	}	    	
	    	
	    	
	    }
			
		else{
		    System.out.println("\nFrom " + type + ": " + 
			    		((TextMessage)message).getText());
		    System.out.print("Input: ");
		}
	    }catch(JMSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	
	  public void processUserInput() throws JMSException {
		    
		  
	       String currentUser = null;
	         String currentPassword = null;
	          
	         Scanner input = new Scanner(System.in);
	         
	         if(!useGui(input)) {
	         boolean answered = false;
	         
	         do {
	            System.out.println("Existing user? (yes/no)");
	            String existingReply = input.nextLine();
	            if( answered = existingReply.equalsIgnoreCase("yes") ) {
	               // verify what the user input as user-name and password
	                do {
	                  
	              System.out.print("User-name: ");
	              currentUser = input.nextLine();
	              System.out.print("Password: ");
	              currentPassword = input.nextLine();
	              
	              this.verifyUser( currentUser, currentPassword );
	              
	              // wait for a response from the server
	              try{
	                Thread.sleep(1000);
	              } catch (InterruptedException e) {
	              // TODO Auto-generated catch block
	              e.printStackTrace();
	            }
	                  
	                  if( this.verified ) 
	                    continue;
	                  
	                  System.out.println("Log in error. Please try again.");
	                  
	                } while( !this.verified );
	                
	          
	                System.out.println("Log in successful. " + "Welcome " + currentUser + ".");
	                this.setUser( currentUser );
	            }
	            else if ( answered = existingReply.equalsIgnoreCase( "no" ) )
	              this.registerUser( input, existingReply, currentPassword );
	            else
	              System.out.println("Invalid input. Please enter 'yes' or 'no'.");
	        
	         } while ( ! answered );
	            
	         this.processUserCommands( input );
	     }
	           
	  }
	  
	         
	  public void processUserCommands( Scanner input ) throws JMSException {
		  
	    System.out.println("# Type 'help' for the list of available commands.");
	    String inputMessage;

	    while(true) {
	      System.out.print("Input: ");
	      inputMessage = input.nextLine();
	      
	      if(inputMessage.startsWith("help")) {
	        // display the help message
	        ChatClientApplication.printHelp();
	        
	      } else if(inputMessage.startsWith("exit")) {
	        // go off-line
				this.sendServer( Constants.LOGOFF, this.getUser() );
				input.close();
				System.exit(0);
	        
	      } else if(inputMessage.startsWith("listOnlineUsers")) {
	        // list all online users
	        this.listOnlineUsers();
	        
	      } else if(inputMessage.startsWith("listChatRooms")) {
	        // list all chat rooms
	        this.listChatRooms();
	        
	      } else if(inputMessage.startsWith("broadcast")) {
	        // broadcast the message
	        inputMessage = inputMessage.substring("broadcast".length()+1);
	        this.broadcast(inputMessage);
	        
	      } else if(inputMessage.startsWith("createChatRoom")) {
	    	  
	    	  if ( inputMessage.length() <= "createChatRoom ".length() ||
	    		   inputMessage.substring( "createChatRoom ".length() ).contains( " " ) ) {
	    		  System.err.println( "Invalid room name. Please enter another name for your chat room."
	    		  		              + "The name may not contain spaces." );
	    		  continue;
	    	  }
	        // create a chat-room
	    	  String room = "";
	    	  room = inputMessage.substring("createChatRoom".length()+1);
	    	  if ( room.length() < 1) {
	        	System.err.println( "Please give the chat room a name, for example: createChatRoom BossRoom" );
	        	continue;
	    	  }
	    	
	    	  this.chatRooms.add( room ); // room will be removed if invalid
	    	
	    	  // ChatRoom and ChatRoomManager logic handled in server, but topic is actually made in ChatClient
	  		  sendServer( Constants.CREATECHATROOM, room ); 

	      
	      } else if(inputMessage.startsWith("send")) {
	        // send a message to a specific user
	        inputMessage = inputMessage.substring("send".length()+1);
	        String userList = inputMessage.substring(0,inputMessage.indexOf(" "));
	        String[] mailingList = userList.split(",");
	        for(String recipient : mailingList) {
	          this.send(recipient,
	              inputMessage.substring(inputMessage.indexOf(" ")+1));
	        }
	        
	      } else if ( chatRoomEntered( inputMessage.substring( 0, inputMessage.indexOf(" ") ) ) ) {
	    	  String roomName = inputMessage.substring( 0, inputMessage.indexOf(" ") );
	    	  publishMessageToChatRoom( roomName, inputMessage.substring( roomName.length() + 1 ) ); 
	      }
	    	  
	      else if ( inputMessage.startsWith( "invite " ) ) {
	    	  
	    	  String invitation[] = inputMessage.split( " " );
	    	  if ( invitation.length != 3 ) {
	    		  System.err.println( "Wrong number of arguments. Must be: invite chatRoom username" );
	    		  continue;
	    	  }
	    	  if ( ! chatRoomEntered( invitation[1] ) ) {
	    		  System.err.println( "You entered a chat room name that does not exist or that you are not subscribed to." );
	    		  continue;
	    	  }
	    	  if ( ! userOnline( invitation[2] )  ) {
	    		  System.out.println( "That user is not online or does not exist." );
	    		  continue;
	    	  }
	    	  
	    	  sendInvitation( invitation[1], invitation[2] ); // passing in the username and the room name
	    	  
	      }
	      
	      else {
	        // invalid input, display input instructions again
	        System.out.println("Client did not recognize your input. Please try again.");
	        System.out.println("# Type 'help' for the list of commands");
	      }
	    }
	  }
	  
	  public void registerUser( Scanner input, String existingReply, String currentPassword ) {
	    
	            do {
	              System.out.println( "Registering a new user." );
	              System.out.print ("Please provide a user-name: ");
	              currentUser = input.nextLine();
	              System.out.print("Please provide a password: ");
	              currentPassword = input.nextLine();
	              
	              this.registerUser(currentUser, currentPassword);
	            
	            // wait for a response from the server
	          try {
	            Thread.sleep(1000);
	          } catch (InterruptedException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	          }
	          
	          if( this.registered ) 
	            continue;
	          
	          System.out.println("Registration error. Please try again.");
	          
	            } while( ! this.registered );
	            
	            System.out.println("Registration successful. " + "Welcome " + currentUser + ".");
	              this.setUser(currentUser);
	              
	  }
	  

	  public boolean useGui( Scanner input ) {
	    
	    String answer = "";
	    
	    System.out.println("Would you like to use the GUI? (yes/no)");
	    
	    while ( true )
	      if( ( answer = input.nextLine() ).equalsIgnoreCase("yes") ) {
	        
	    	ChatClientGUI gui = new ChatClientGUI(this);
	    	gui.start();
	        return true;
	      } 
	      else if ( answer.equalsIgnoreCase( "no" ) ) 
	        return false;
	      
	        
	    
	  
	  }
	
}



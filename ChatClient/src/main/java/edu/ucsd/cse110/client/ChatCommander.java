package edu.ucsd.cse110.client;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

public class ChatCommander {
	
	private ArrayList<String> chatRooms = new ArrayList<String>();  //list of rooms the user is subscribed to
	private ArrayList<String> allChatRooms = new ArrayList<String>();
	private ArrayList<String> pendingInvitations = new ArrayList<String>();
	private ArrayList<TopicPublisher> publisherList = new ArrayList<TopicPublisher>();
	private ArrayList<TopicSubscriber> subscriberList = new ArrayList<TopicSubscriber>();
	private TopicSession topicSession;
	private ChatClient client;


	
	public ChatCommander( ChatClient client, TopicSession session ) {
		this.client = client;
		this.topicSession = session;
	}
	
 	public ArrayList<String> getChatRooms(){
 		return chatRooms;
 	}
	
	public ChatClient getClient(){
		return client;
	}
	
	
	/**
	 * Broadcast a message to all users
	 * @param inputMessage	the message to broadcast
	 */
	public boolean broadcast(String inputMessage) {		
		
		if ( "".equals( inputMessage ) ) {
			System.out.println( "You must enter a message to broadcast." );
			return false;
		}
		
		boolean justSpaces = true;
		
		for ( int i = 0; i < inputMessage.length(); ++i ) 
			if ( inputMessage.charAt( i ) == ' '  )
				continue;
			else {
				justSpaces = false;
				break;
			}

		if ( justSpaces ) {
			System.out.println( "You must enter a message to broadcast." );
			return false;
		}
			
		
	    try {
	    	Message message = topicSession.createTextMessage(inputMessage);
	    	message.setJMSType(Constants.MESSAGE);
	    	message.setJMSReplyTo( client.getQueue() );
	    	message.setStringProperty("SENDER", client.getUser().getUsername());
	    	publisherList.get( 0 ).publish(message);  // using broadcast's publisher 
	    	System.out.println("Message broadcasted.");
	    } catch (JMSException e) {
	   		e.printStackTrace();
	   	}
	    
	    return true;
	    
	}
	
	
	public boolean addPendingInvitation( String room ) {
		
		if ( pendingInvitations.contains( room ) )
			return false;
	 
		pendingInvitations.add( room );
		return true;
	}
	
	
	/**
	 * 
	 * @param room	name of the chat room
	 */
	public void createChatRoom(String room) {
		chatRooms.add( room );
		client.sendServer( Constants.CREATECHATROOM, client.getUser().getUsername()+" "+room );
	}
	
	
	/**
	 * @param chatRoom	the name of the chat room the user is trying to leave
	 */
	public boolean leaveChatRoom(String chatRoom) throws JMSException {

	    if ( ! chatRoomEntered( chatRoom ) ) {
	      System.out.println( "You're not in that chat room." );
	      return false;
	    }

	    chatRooms.remove( chatRoom );

	    for ( int i = 0; i < publisherList.size(); ++i )
	      if ( chatRoom.equals( publisherList.get(i).getTopic().getTopicName() ) ) {
	        publisherList.get(i).close();
	        publisherList.remove(i);
	        break;
	      }

	    for ( int i = 0; i < subscriberList.size(); ++i )
	      if ( chatRoom.equals( subscriberList.get(i).getTopic().getTopicName() ) ) {
	        subscriberList.get(i).close();
	        subscriberList.remove(i);
	        break;
	      }

	    System.out.println( "You have left the chat room: " + chatRoom );

	    String userAndRoom = client.getUser().getUsername() + " " + chatRoom;
	    client.sendServer( Constants.LEAVECHATROOM, userAndRoom );

	    return true;

	  }


	public void add( String room ) {
		chatRooms.add( room );
	}
	
	public String removeLastRoomAdded() {
		
		String toReturn = chatRooms.get( chatRooms.size() - 1 );
		chatRooms.remove( chatRooms.size() - 1 );
		
		return toReturn;
	}
	
	
	/* Creates the topic representing the chat room 
	 * and sets up the publisher and subscriber 
	 */
	
	public boolean setupChatRoomTopic( String room ) {
				
		try {
			Topic chatRoom = topicSession.createTopic( room );
			TopicSubscriber subscriber = topicSession.createSubscriber( chatRoom );
			TopicPublisher publisher = topicSession.createPublisher( chatRoom );
			
			subscriber.setMessageListener( client );
			this.subscriberList.add( subscriber );
			this.publisherList.add( publisher );
			
			System.out.println( "You are now connected to chat room: " + room );
			
		} catch (JMSException e) {
			e.printStackTrace();
			System.out.println( "There was a problem setting up the chat room" );
			return false;
		}
		
		return true;
		
	}
	
	public String setupChatRoomTopic() {
		
		String room = chatRooms.get( chatRooms.size() - 1 );
		setupChatRoomTopic( room );
		
		return room;
	
	}
	
	public boolean addSubscriber( TopicSubscriber subscriber ) {	
		return subscriberList.add( subscriber );
	}
	
	public boolean addPublisher( TopicPublisher publisher ) {
		return publisherList.add( publisher );
	}

	
	public boolean inviteToChatRoom( String user, String room ) {
		
		if ( null == room  ) {
			System.out.println( "Please specify a room name." );
			return false;
		}
		
		if ( null == user ) {
			System.out.println( "Please specify a user to invite." );
			return false;
		}
		
		if ( ! client.userOnline( user ) ) {
			System.out.println( "That user is not online." );
			return false;
		}
		
		if ( room.contains( " " ) ) {
			System.out.println( "Room name may not contain a space character." );
			return false;
		}
		
		if ( subscribedToChatRoom( room ) )
			client.sendServer( Constants.INVITATION, user + " " + room ); // space character is a dilimeter
		else {
			System.out.println( "You're not in that chat room." );
			return false;
		}
		
		return true;
		
	}


	public boolean acceptInvite( String inputMessage ) throws JMSException {
		
		String acceptRoom[] = null;
		String chatRoom = null;
		
		if( inputMessage.equals("accept") && 1 == pendingInvitations.size() ) 
			chatRoom = pendingInvitations.get( 0 );
		
		else if( inputMessage.contains(" ") ){
			  acceptRoom = inputMessage.split(" ");
			  if( 2 == acceptRoom.length )
				  chatRoom = acceptRoom[1];
			  else {
				  System.out.println( "Wrong amout of arguments, use keyword 'accept' followed by a chat room name" );
				  return false;
			  }
		} 
		else {
			System.out.println( "Invalid input. Use keyword 'accept' followed by a chat room name." );
			return false;
		}
		
		if ( ! pendingInvitations.contains( chatRoom ) ) {
			System.out.println( "You don't have any pending invitations for the chat room: " + chatRoom );
			return false;
		}
		
		pendingInvitations.remove( chatRoom ); 
		setupChatRoomTopic( chatRoom );
		chatRooms.add( chatRoom );
		client.sendServer( Constants.ACCEPTEDINVITE, client.getUser().getUsername() + " " + chatRoom ); 
		
		return true;
		
	}



	/* This method checks if the user is in the chat room passed in 
	 * Checks if input starts with a chat room name and then returns that name, 
	 * else return false */
	
	public boolean chatRoomEntered(String inputMessage ) {
		
		String roomAndMessage[] = null;
		String name = null;
		
		if ( ! inputMessage.contains( " " ) ) // input must be roomName + " " + message
			name = inputMessage;
		else {
			roomAndMessage = inputMessage.split( " " );
			if ( 2 > roomAndMessage.length )
				return false;
			
			name = roomAndMessage[0];
		}
		
		for ( String room : chatRooms ) {
			if ( room.equals( name ) ) 
				return true;
	 	}
		
		return false;
			
	}
	
	public boolean chatRoomEntered( String room, boolean fromRequestUsersInChatRoom ) {
		
		if ( ! fromRequestUsersInChatRoom )
			return false;
		
		return allChatRooms.contains( room );
			
	}
	
	
	
	public boolean subscribedToChatRoom( String room ) {
		
		return chatRoomEntered( room );
		
	}	
	
	public void addToChatRoomList( String name ) {
		chatRooms.add( name );
	}
	
	public void publishMessageToChatRoom( String room, String message ) throws JMSException {
		for ( TopicPublisher publisher : publisherList ) {
			if ( publisher.getTopic().getTopicName().equals( room ) ) {
				TextMessage text = topicSession.createTextMessage( message );
				text.setJMSType(Constants.ROOMMESSAGE);
				text.setStringProperty("ROOM", room);
				text.setStringProperty("SENDER", client.getUser().getUsername());
				text.setJMSReplyTo( client.getQueue() );
				publisher.publish( text );
				return;
			}
		}
		
	}
	
	/**
	 * Contact the server and print to request a list of all chat rooms
	 */
	public void listChatRooms() {
			
		if ( 0 == chatRooms.size() )
			System.out.println( "You're not currently in any chat rooms." );
		
		for ( String room : chatRooms ) {
		  System.out.println( room );	
		}
		
	}
	
	public void updateAllChatRooms( ArrayList<String> allRooms ) {
		allChatRooms = allRooms;
	}
	
	
	public void listAllChatRooms() {
		
		System.out.println( "There are currently " + allChatRooms.size()  + " running chat rooms." );
		
		for ( String room : allChatRooms ) {
			System.out.println( room );
		}
	
	}
	
	
	public boolean requestUsersInChatRoom( String room ) {
		
		if ( ! chatRoomEntered( room, true ) ) {
			System.out.println( "Sorry, that chat room doesn't exist." );
			return false;
		}
		
		client.sendServer( Constants.USERSINCHATROOM, room );
		return true;
		
	}
	
	public void listUsersInChatRoom( Map<String, Destination> usersInChatRoom ) {
		
		Set<String> users = usersInChatRoom.keySet();
		
		for ( String user : users ) {
			System.out.println( user );
		}
		
		
	}
	
	public void leaveAllChatRooms() {
		for(int i=chatRooms.size()-1; i>=0; i--) {
			try {
				this.leaveChatRoom(chatRooms.get(i));
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public boolean chatRoomExists( String room ) {
		return allChatRooms.contains( room );
	}
	
	
	public boolean requestInvite( String room ) throws JMSException {
	
			if ( chatRoomEntered( room ) ) {
				System.out.println( "You're already in that chatroom." );
				return false;
			}
			
			if ( chatRoomExists( room ) ) {
				Topic chatRoom = topicSession.createTopic( room );
				TopicPublisher publisher = topicSession.createPublisher( chatRoom );
				String message = client.getUser().getUsername() + " would like to be invited to your chat room: " + room;
				TextMessage text = topicSession.createTextMessage( message );
				text.setJMSType(Constants.ROOMMESSAGE);
				text.setJMSReplyTo( client.getQueue() );
				text.setStringProperty( "ROOM", room );
				text.setStringProperty( "SENDER", client.getUser().getUsername() );
				publisher.send( text );
				
				return true;
			}
			else {
				System.out.println( "That chat room doesn't exist." );
				return false;
			}
				
				
		
	}

}
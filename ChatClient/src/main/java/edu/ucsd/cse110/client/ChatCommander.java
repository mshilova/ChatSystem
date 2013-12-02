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
	
	protected ArrayList<String> chatRooms = new ArrayList<String>();
	private ArrayList<String> pendingInvitations = new ArrayList<String>();
	private ArrayList<TopicPublisher> publisherList = new ArrayList<TopicPublisher>();
	private ArrayList<TopicSubscriber> subscriberList = new ArrayList<TopicSubscriber>();
	private TopicSession topicSession;
	private ChatClient client;


	
	public ChatCommander( ChatClient client, TopicSession session ) {
		this.client = client;
		this.topicSession = session;
	}
 	
	
	/**TODO refactor how the createchatroom gets a username
	 * 
	 * this was added to make the sendserver createchatroom 
	 * method send a user name w/ the room name.
	 *  
	 * @return
	 */
	public ChatClient getClient(){
		return client;
	}
	
	
	/**
	 * Broadcast a message to all users
	 * @param inputMessage	the message to broadcast
	 */
	public boolean broadcast(String inputMessage) {		
		
		if ( "".equals( inputMessage ) ) {
			System.out.println( "You must enter a message to broadcast" );
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
			System.out.println( "You must enter a message to broadcast" );
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
	    // TODO leave the chat room user is in

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
			System.out.println("accepted the invite, inside commander.");
			Topic chatRoom = topicSession.createTopic( room );
			TopicSubscriber subscriber = topicSession.createSubscriber( chatRoom );
			TopicPublisher publisher = topicSession.createPublisher( chatRoom );
			
			subscriber.setMessageListener( client );
			this.subscriberList.add( subscriber );
			this.publisherList.add( publisher );
			
			System.out.println( "You are now connected to chat room: " + room );
			
		} catch (JMSException e) {
			e.printStackTrace();
			System.err.println( "There was a problem setting up the chat room" );
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
	
	
	public boolean sendInvitation( String user, String room ) throws JMSException {
		
		if ( ! client.userOnline( user ) ) {
			System.out.println( "That user is not online." );
			return false;
		}
		
		System.out.println("USer " + user+ " room " + room);
		TextMessage message = client.getSession().createTextMessage( client.getUser().getUsername() + " " + room );
		message.setJMSType( Constants.INVITATION );
		client.getProducer().send( client.getDestination( user ), message );
		System.out.println( "Invitation sent." );
		
		return true;
		
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
			client.sendServer( Constants.INVITATION, user + " " + room ); // space character is a dilimeter
		else 
			return;
		
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
			  System.err.println( "Wrong amout of arguments, use keyword 'accept' followed by a chat room name" );
			  return false;
		  }
	} 
	else {
		System.err.println( "Invalid input. Use keyword 'accept' followed by a chat room name." );
		return false;
	}
	
	if ( ! pendingInvitations.contains( chatRoom ) ) {
		System.out.println( "You don't have any pending invitations for the chat room: " + chatRoom );
		return false;
	}
	
	pendingInvitations.remove( chatRoom ); 
	setupChatRoomTopic( chatRoom );
	chatRooms.add( chatRoom );
	System.out.println("accepted invite ");
	client.sendServer( Constants.ACCEPTEDINVITE, client.getUser().getUsername() + " " + chatRoom ); 
	
	return true;
	
}



/* Checks if input starts with a chat room name and then returns that name, 
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



public boolean subscribedToChatRoom( String room ) {
	
	return chatRoomEntered( room );
	
}	

public void addToChatRoomList( String name ) {
	chatRooms.add( name );
}

public void publishMessageToChatRoom( String room, String message ) throws JMSException {
	System.out.println("inside commander");
	for ( TopicPublisher publisher : publisherList ) {
		System.out.println("inside commander loop");
		if ( publisher.getTopic().getTopicName().equals( room ) ) {
			System.out.println("inside commander loop if");
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
	
	for ( String room : chatRooms ) {
	  System.out.println( room );	
	}
	
}

public boolean requestUsersInChatRoom( String room ) {
	
	if ( ! chatRoomEntered( room ) ) {
		System.out.println( "Sorry, you're not in that chat room." );
		return false;
	}
	
	client.sendServer( Constants.USERSINCHATROOM, room );
	System.out.println("request good");
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

}

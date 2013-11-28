package edu.ucsd.cse110.client;

import java.util.ArrayList;

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
	public void broadcast(String inputMessage) {		
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
	}
	
	public void setTopicSession( TopicSession session ) {
		topicSession = session;
	}
	
	
	public void addPendingInvitation( String room ) {
		pendingInvitations.add( room );
	}
	
	/**
	 * @param chatRoom	the name of the chat room the user is trying to leave
	 */
	public void leaveChatRoom(String chatRoom) {
		// TODO leave the chat room user is in
	}
	
	public void add( String room ) {
		chatRooms.add( room );
	}
	
	
	/* Creates the topic representing the chat room 
	 * and sets up the publisher and subscriber 
	 */
	
	public void setupChatRoomTopic( String room ) {
				
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
			System.err.println( "There was a problem setting up the chat room" );
		}
		
	}
	
	public void setupChatRoomTopic() {
		
		setupChatRoomTopic( chatRooms.get(chatRooms.size() - 1) );
	
	}
	
	public void addSubscriber( TopicSubscriber subscriber ) {
		
		subscriberList.add( subscriber );
	}
	
	public void addPublisher( TopicPublisher publisher ) {
		publisherList.add( publisher );
	}
	
	
	public void sendInvitation( String user, String room ) throws JMSException {
		System.out.println("In send invitation.");
		System.out.println("USer " + user+ " room " + room);
		TextMessage message = client.getSession().createTextMessage( client.getUser().getUsername() + " " + room );
		message.setJMSType( Constants.INVITATION );
		client.getProducer().send( client.getDestination( user ), message );
		System.out.println( "Invitation sent." );
		
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
	/*Message message;
	try {
		message = client.getSession().createTextMessage( client.getQueue().toString());
		message.setJMSType(Constants.LISTCHATROOMS);
		message.setJMSReplyTo( client.getQueue() );
		client.getProducer().send( client.getSession().createQueue(Constants.SERVERQUEUE), message);
	} catch (JMSException e) {
		e.printStackTrace();
	}*/
	
	for ( String room : chatRooms ) {
	  System.out.println( room );	
	}
	
}

}

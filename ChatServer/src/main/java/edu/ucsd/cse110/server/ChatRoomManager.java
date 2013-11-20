package edu.ucsd.cse110.server;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.TopicSession;




class ChatRoomManager {
	
	static ArrayList<ChatRoom> chatRoomList = new ArrayList<ChatRoom>();
	

	/* REFACTOR THIS SO THAT WE USE A HASHMAP INSTEAD */
public static void addUserToRoom( String user, String room ) {
	
	for ( ChatRoom chatRoom : chatRoomList ) {
		if ( chatRoom.getName().equals( room ))
			chatRoom.addUser( user );
	}
	
}
	
public static void addChatRoom( ChatRoom room ) {
	  chatRoomList.add( room );	
}

public static ArrayList<String> listOfChatRooms() {
	ArrayList<String> chatRoomNames = new ArrayList<String>();
	for( int i = 0; i < chatRoomList.size(); i++ ) {
		chatRoomNames.add(chatRoomList.get(i).getName());
	}
	return chatRoomNames;
}


public static boolean chatRoomExists( String name ) {
	
	for ( ChatRoom room : chatRoomList ) {
		if ( room.getName().equals( name ) ) 
			return true;
 	}
	
	return false;
	
}

/* MessageProducer - producer: the producer of the client sending the invitation */

public static boolean sendingInvitation( String sender, MessageProducer producer, String input ) {
	
	String roomName;
	String invite;
	String username;
	LoginManager manager = LoginManager.getInstance();
	
	for ( ChatRoom room : chatRoomList ) {
		if ( input.startsWith( roomName = room.getName() ) ) {
			
			invite = input.substring( roomName.length(), roomName.length() + "invite".length() );
			
			if ( "invite".equals( invite ) || 
				 "Invite".equals( invite ) ||
				 "INVITE".equals( invite ) ) {
				
				    username = input.substring( roomName.length() + " invite ".length() );
				    
					if ( manager.checkUserOnline( username ) ) {
						//sendInvitation( sender, room, username );
						return true;
					}
			}
			
			
			
		}
	
	}
	
	return false;
}



}


package edu.ucsd.cse110.server;

import java.util.ArrayList;


class ChatRoom {
	
	ArrayList<String> userList;
	String name;

	
	public ChatRoom( String name ) {
		
	  userList = new ArrayList<String>();
	  this.name = name;
      ChatRoomManager.addChatRoom( this );

	}
	
	
	public String getName() {
		return this.name;
	}
	
	
	boolean isEmpty() {
		return userList.isEmpty();
	}
	
	
	void addUser( String user ) {
	
		if ( ! userList.contains( user ) )
		userList.add( user );
	
	}
	
    ArrayList<String> whosInChatRoom() {
    	
    	ArrayList<String> listOfUsers = new ArrayList<String>();
    	
    	for ( int i = 0; i < userList.size(); ++i )
     	  listOfUsers.add( userList.get( i ) );
    	
    	return listOfUsers;
    
    }
    		
    
}
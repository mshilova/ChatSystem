package edu.ucsd.cse110.server;

import java.util.ArrayList;


public class ChatRoom {
	
	private ArrayList<String> userList;
	private String name;

	
	public ChatRoom( String name ) {	
		userList = new ArrayList<String>();
		this.name = name;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	
	public boolean isEmpty() {
		return userList.isEmpty();
	}
	
	
	public void addUser( String user ) {
	
		if ( ! userList.contains( user ) )
			userList.add( user );
	}
	
    public ArrayList<String> whosInChatRoom() {
    	
    	ArrayList<String> listOfUsers = new ArrayList<String>();
    	
    	for ( int i = 0; i < userList.size(); ++i )
     	  listOfUsers.add( userList.get( i ) );
    	
    	return listOfUsers; 
    }
    		
    
}
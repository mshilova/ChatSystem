package edu.ucsd.cse110.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;


public class ChatRoom implements Serializable{

	/* serial ID is used for object message. Default should work fine */
	private static final long serialVersionUID = 1L;
	private String name;
	private Map<String, Destination> roomUsers;
	
	
	/**
	 * 	Normal constructor
	 * @param name
	 */
	public ChatRoom(String name){
		if(name == null)
			throw new IllegalArgumentException();
		roomUsers = new HashMap<String, Destination>();
		this.name = name;
	}
	
	
	/**
	 * 	Copy constructor
	 * @param name
	 * @param roomUsrs
	 */
	public ChatRoom(ChatRoom room){
		if(room == null)
			throw new IllegalArgumentException();
		
		this.roomUsers = room.getAllUsers();
		this.name = room.getName();	
	}
	
	
	/**
	 * 
	 * @return map of all users in chatroom
	 */
	public Map<String, Destination> getAllUsers() {
	    Map <String, Destination> retMap = new HashMap<String,Destination>();
	    
	    retMap.putAll(roomUsers);
	    return retMap;
	}

	/**
	 * removes a user from the room
	 * @param user
	 * @return 
	 */
	public boolean removeUser(String user){
		    
		if(null == user)
			return false;	
		if(roomUsers.containsKey(user)){
			roomUsers.remove(user);
		    return true;
		}
		return false;
	}


	/**
	 * adds a user & their destination to the room
	 * @param user
	 * @param dest
	 * @return
	 */
	public boolean addUser(String user, Destination dest) {
		
		if(user == null || dest == null)
	    	return false;	
	    if(roomUsers.containsKey(user))
	    	return false;    
	    roomUsers.put(user, dest);
	
	    return true;

	}

	public Destination getUserDestination(String name){
		if(null == name)
			throw new IllegalArgumentException();
		return roomUsers.get(name);
	}
	/**
	 * checks if the rooms has a user with name user
	 * @param user
	 * @return
	 */
	public boolean containsUser(String user) {
		return roomUsers.containsKey(user);
	}


	/**
	 * removes all users from the room
	 */
	public void removeAllUsers() {
		roomUsers.clear();
	}

	/**
	 * gets the room name
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the room name
	 * @param name
	 */
	public void setName(String name) {
		if(name == null)
			throw new IllegalArgumentException();
		
		this.name = name;
	}


	/**
	 * returns the number of users in the room
	 * @return
	 */
	public int numUsers(){
		return roomUsers.size();
	}
}

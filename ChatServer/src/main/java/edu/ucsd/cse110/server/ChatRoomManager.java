
package edu.ucsd.cse110.server;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;


public class ChatRoomManager implements Manager{
		
	private static ChatRoomManager roomManager;
	private Map<String, ChatRoom> rooms;
	private MessageProcessor processor;
		
	private ChatRoomManager(){
		rooms = new HashMap<String, ChatRoom>();
		processor = new MessageProcessor();
	}
	
	public static ChatRoomManager getInstance(){
		if(roomManager == null)
			roomManager= new ChatRoomManager();
		return roomManager;
	}


	/* Returns a deep copy of the room with room name String room */
	public ChatRoom copyRoom(String room){
		return new ChatRoom(rooms.get(room));
	}	
	
	/* Returns reference to a room */
	public ChatRoom getRoom(String room){
		return rooms.get(room);
	}
	
	/**
	 * removes a room from the map of rooms
	 * 
	 */
	public boolean removeItem(Message message) {
		String name;
		
		try{
			name = processor.extractName(message);
			
			if(rooms.containsKey(name)){
				rooms.remove(name);
				return true;
			}
		}catch(JMSException e){ e.printStackTrace(); }
		
		return false;
	}
	
	/**
	 * removes a user from a specific room and returns a
	 * copy of the update ChatRoom object.
	 * @param message
	 * @return
	 */
	public ChatRoom removeUser(Message message){
		String[] userAndRoom;
		ChatRoom room;
		
		try{
			userAndRoom = processor.extractTwoArgs(message);
			
			if(userAndRoom == null)
				return null;
		
			if( rooms.containsKey(userAndRoom[1]) ){
				room = rooms.get(userAndRoom[1]);
				if( room.containsUser(userAndRoom[0]) ){
					room.removeUser(userAndRoom[0]);
					return new ChatRoom(room);
				}
			}
			
		}catch(JMSException e){ e.printStackTrace(); }
		
		return null;
	}

	/**
	 * adds a ChatRoom to the map of ChatRooms
	 */
	public boolean addItem(Message message) {
		String[] userAndRoom;
	
		try{
			userAndRoom = processor.extractTwoArgs(message);
			
			
			if(userAndRoom == null)
				return false;
		
			if(rooms.containsKey(userAndRoom[1]))
				return false;
			
			ChatRoom chatRoom = new ChatRoom(userAndRoom[1]);
			chatRoom.addUser(userAndRoom[0], message.getJMSReplyTo());	
			rooms.put(userAndRoom[1], chatRoom);
			return true;
			
		}catch(JMSException e){ e.printStackTrace(); }
		return false;
	}
	

	/**
	 * adds a user to a specific ChatRoom and returns a copy 
	 * of the updated ChatRoom object.
	 * @param message
	 * @return
	 */
	public ChatRoom addUser(Message message){
		String[] userAndRoom;
		ChatRoom room;
		
		try{
			userAndRoom = processor.extractTwoArgs(message);
			
			if(userAndRoom == null)
				return null;
		
			if( rooms.containsKey(userAndRoom[1]) ){
				room = rooms.get(userAndRoom[1]);
				if( !(room.containsUser(userAndRoom[0])) ){
					room.addUser(userAndRoom[0], message.getJMSReplyTo());
					return new ChatRoom(room);
				}
			}
			
		}catch(JMSException e){ e.printStackTrace(); }	
		return null;
	}
	
	/**
	 * Checks if a ChatRoom with name "item" already exists.
	 */
	public boolean containsItem(String item) {
		return rooms.containsKey(item);
	}

	/**
	 * removes all ChatRooms from the map of ChatRooms
	 */
	public void removeAllItems() {
		rooms.clear();		
	}

	/**
	 * Removes any ChatRoom with 0 users in the room
	 */
	public void update(){
		for(String room : rooms.keySet()){
			if( 0 == rooms.get(room).numUsers()){
				rooms.remove(room);
			}
		}
	}

	/**
	 * returns a copy of all the active ChatRooms 
	 */
	
	public Map<String, ? extends Object> getAllItems() {
		Map<String, ChatRoom> retMap = new HashMap<String, ChatRoom>();
		retMap.putAll(rooms);
		return retMap;
	}


}


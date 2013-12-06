package edu.ucsd.cse110.server;

import java.util.Map;

import javax.jms.Destination;
import javax.jms.Message;


public interface IServer {		
		
		public abstract void receive(Message message)throws Exception; 
		
		
		public boolean updateChatRoom(ChatRoom room);
		
		
		public boolean updateOnlineUsers(boolean update);

		public boolean send(Destination recipient, final ChatRoom room, final String type);

		public boolean send(Destination recipient, final boolean success, final String type);
		
		public boolean send(Destination recipient, final Map<String, Destination> onlineUsers, final String type);


}

package edu.ucsd.cse110.server;

import java.io.Serializable;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


public interface IServer {		
		
		public abstract void receive(Message message)throws Exception; 
		
		
		public boolean updateChatRoom(ChatRoom room);
		
		
		public boolean updateOnlineUsers(boolean update);

		public boolean send(Destination recipient, final ChatRoom room, final String type);

		public boolean send(Destination recipient, final boolean success, final String type);
		
		public boolean send(Destination recipient, final Map<String, Destination> onlineUsers, final String type);


}

package edu.ucsd.cse110.clientTest;


import static org.junit.Assert.*;

import java.net.URISyntaxException;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import edu.ucsd.cse110.client.*;

import org.apache.activemq.ActiveMQConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestInputProcessor {

	private static ActiveMQConnection connection;
	private static ChatClient client;
	private static User user;
	ChatCommander chatCommander;
	
	@Before
	public void setup()
	{
		user = new User();
		user.setUsername("Savuthy");
		user.setPassword("password");
		try
		{
			client = wireClient();
			client.setUser(user);
			client.verifyUser(user.getUsername(), user.getPassword());
			chatCommander = client.getChatCommander();
		}
		catch(Exception e)
		{}
	}
		
	@After
	public void tearDown() throws Exception {
		chatCommander.leaveAllChatRooms();
		client.sendServer( Constants.LOGOFF, client.getUser().getUsername() );
	}

	@Test
	public void testProcessListUsersInChatRoom() {
		
		InputProcessor processor = new InputProcessor();
		String inputMessage;
				
		try {
		    Thread.sleep(500);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		//Testing invalid argument
		inputMessage = "inChatRoom ";
		assertFalse(processor.processListUsersInChatRoom(chatCommander, inputMessage));
						
		//Testing invalid ChatRoom
		inputMessage = "inChatRoom inValidRoom";
		assertFalse(processor.processListUsersInChatRoom(chatCommander, inputMessage));

		//Testing successfully send processListUsersInChatRoom
		if (! chatCommander.isChatRoomExisted("myRoom"))
			chatCommander.createChatRoom("myRoom");
		
		try {
		    Thread.sleep(500);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		inputMessage = "inChatRoom myRoom";
		assertTrue(processor.processListUsersInChatRoom(chatCommander, inputMessage));
		
	}

	@Test
	public void testProcessLeaveChatRoom() {
		
		InputProcessor processor = new InputProcessor();
		
		String inputMessage;
				
		try {
			
			//Testing invalid argument
			inputMessage = "leave";
			assertFalse(processor.processLeaveChatRoom(chatCommander, inputMessage));
						
			//Testing invalid ChatRoom
			inputMessage = "leave inValidRoom";
			assertFalse(processor.processLeaveChatRoom(chatCommander, inputMessage));

			//Testing successfully send ProcessLeaveChatRoom
			if (! chatCommander.isChatRoomExisted("leaveRoom"))
				chatCommander.createChatRoom("leaveRoom");

			inputMessage = "leave leaveRoom";
			assertTrue(processor.processLeaveChatRoom(chatCommander, inputMessage));
			
		} catch (JMSException e) {
			e.printStackTrace();
		} 
		
	} 


	@Test
	public void testProcessInvitation() {

		InputProcessor processor = new InputProcessor();
		
		String inputMessage;
				
		try {

			//Testing invalid argument
			inputMessage = "invite";
			assertFalse(processor.processInvitation(client,chatCommander, inputMessage));
						
			//Testing invalid argument
			inputMessage = "invite myRoom";
			assertFalse(processor.processInvitation(client,chatCommander, inputMessage));
			
			//Testing invalid ChatRoom
			inputMessage = "invite inValidRoom Mike";
			assertFalse(processor.processInvitation(client,chatCommander, inputMessage));
			
			
			//Testing User not online
			if (! chatCommander.isChatRoomExisted("InvitationRoom"))
				chatCommander.createChatRoom("InvitationRoom");
			inputMessage = "invite InvitationRoom Mike";

			assertFalse(processor.processInvitation(client,chatCommander, inputMessage));

			//Connect Nobel for testing invitation
			User nobel = new User();
			nobel.setUsername("Nobel");
			nobel.setPassword("password");
		
			ChatClient clientNobel = wireClient();
			clientNobel.setUser(nobel);
			clientNobel.verifyUser(nobel.getUsername(), nobel.getPassword());
			ChatCommander nobelChatCommander = clientNobel.getChatCommander();
			
			//Testing successfully send ProcessInvitation
			if (! chatCommander.isChatRoomExisted("InvitationRoom1"))
				chatCommander.createChatRoom("InvitationRoom1");
			
			try {
			    Thread.sleep(500);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
			inputMessage = "invite InvitationRoom1 Nobel";
			assertTrue(processor.processInvitation(client,chatCommander, inputMessage));

			nobelChatCommander.leaveAllChatRooms();
			clientNobel.sendServer( Constants.LOGOFF, clientNobel.getUser().getUsername() );
			
			
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	} 

	
	@Test
	public void testProcessMessageForChatRoom() {
		
		InputProcessor processor = new InputProcessor();
		
		String inputMessage;
	
		try {
			
			if (! chatCommander.isChatRoomExisted("chatRoom"))
				chatCommander.createChatRoom("chatRoom");

			//Invalid Input
			inputMessage= "chatRoom";
			assertFalse(processor.processMessageForChatRoom(chatCommander, inputMessage));
			
			
			inputMessage = "chatRoom Hello";
			assertTrue(processor.processMessageForChatRoom(chatCommander, inputMessage));

			
		} catch (JMSException e) {
			e.printStackTrace();
		} 
	}

	@Test
	public void testProcessMessageToSend() {
		InputProcessor processor = new InputProcessor();
		
		String inputMessage;
				

		//testing invalid argument
		inputMessage = "send SavuthyHeloo";
		assertFalse(processor.processMessageToSend(client, inputMessage));
			
		inputMessage = "send Savuthy Hello";
		assertTrue(processor.processMessageToSend(client, inputMessage));

	}

	@Test
	public void testProcessChatRoomCreation() {
		InputProcessor processor = new InputProcessor();
		String inputMessage;
				

		//Testing invalid input 		
		inputMessage = "createChatRoom";
		assertFalse(processor.processChatRoomCreation(chatCommander, inputMessage));

		//Testing invalid input - no room name 		
		inputMessage = "createChatRoom ";
		assertFalse(processor.processChatRoomCreation(chatCommander, inputMessage));
			
		inputMessage = "createChatRoom CSE110";
		assertTrue(processor.processChatRoomCreation(chatCommander, inputMessage));
			

	}

	@Test
	public void testProcessBroadcast() {
		InputProcessor processor = new InputProcessor();
		
		String inputMessage;

		//testing invalid argument
		inputMessage = "BroatCastIambroatcasting";
		assertFalse(processor.processBroadcast(chatCommander, inputMessage));

		inputMessage = "BroatCast Iambroatcasting";
		assertTrue(processor.processBroadcast(chatCommander, inputMessage));


	}


	private static ChatClient wireClient() throws JMSException, URISyntaxException {
		connection = ActiveMQConnection.makeConnection(
				/*currentUser, currentPassword,*/ Constants.ACTIVEMQ_URL);
        connection.start();
        CloseHook.registerCloseHook(connection);
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // for producing messages
        MessageProducer producer =  session.createProducer(null);
        // for consuming messages
        Queue incomingQueue = session.createTemporaryQueue();
        MessageConsumer consumer = session.createConsumer(incomingQueue);
        
        // topic for client to publish broadcast messages and subscribe
        TopicSession topicSession = connection.createTopicSession(
        		false, TopicSession.AUTO_ACKNOWLEDGE);
        Topic broadcastTopic = topicSession.createTopic(Constants.BROADCAST);
        TopicPublisher publisher = topicSession.createPublisher(broadcastTopic);
        TopicSubscriber subscriber = topicSession.createSubscriber(broadcastTopic);
        
        return new ChatClient(incomingQueue,session,producer,consumer,
        		topicSession,publisher,subscriber);
	}
	
	static private class CloseHook extends Thread {
		ActiveMQConnection connection;
		private CloseHook(ActiveMQConnection connection) {
			this.connection = connection;
		}
		
		public static Thread registerCloseHook(ActiveMQConnection connection) {
			Thread ret = new CloseHook(connection);
			Runtime.getRuntime().addShutdownHook(ret);
			return ret;
		}
		
		public void run() {
			try {
				System.out.println("Closing ActiveMQ connection");
				connection.close();
			} catch (JMSException e) {
				/* 
				 * This means that the connection was already closed or got 
				 * some error while closing. Given that we are closing the
				 * client we can safely ignore this.
				*/
			}
		}
	}
}

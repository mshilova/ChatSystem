package edu.ucsd.cse110.clientTest;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.client.ChatClient;
import edu.ucsd.cse110.client.Constants;
import edu.ucsd.cse110.client.User;


public class TestChatClient
{
	private static User user;
	//private static Session session;
	private static ChatClient client1;
	//private static ChatClient client2;
	private static ActiveMQConnection connection;
	
	//private Queue incomingQueue;
	//private MessageConsumer consumer;
	//private MessageProducer producer;
	
	@Before
	public void setup() throws JMSException, URISyntaxException
	{
		client1 = wireClient();
	}
	
	@After
	public void tearDown() throws JMSException, URISyntaxException
	{
		//Log Off User
		client1.sendServer( Constants.LOGOFF, client1.getUser().getUsername() );
		
	}
	
	@Test
	public void testSetGetUser()
	{
		user = new User();
		user.setUsername("User1");
		user.setPassword("password");
		assertNotSame(user, client1.getUser());
		
		client1.setUser(user);
		
		assertEquals(user, client1.getUser());
		
		assertEquals("User1", client1.getUser().getUsername() );
		assertEquals("password", client1.getUser().getPassword());

		
	}
	
	@Test
	public void testGetChatCommander()
	{
		assertNotNull( client1.getChatCommander() );
	}
	
	@Test
	public void testGetOnlineUsers()
	{
		Map<String, Destination> onlineUsers;
		
		User user1 = new User();
		user1.setUsername("Savuthy");
		user1.setPassword("password");
		
		//Test User not yet LogIn
		onlineUsers = client1.getOnlineUsers();
		assertFalse(onlineUsers.containsKey(user1.getUsername()));
		
		//Test User Online
		client1.setUser(user1);
		client1.verifyUser(user1.getUsername(), user1.getPassword());
		
		try {
		    Thread.sleep(500);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		onlineUsers = client1.getOnlineUsers();
		assertTrue(onlineUsers.containsKey(user1.getUsername()));
		
	}
	
	@Test
	public void testUserOnline()
	{
		
		User user1 = new User();
		user1.setUsername("Savuthy");
		user1.setPassword("password");
				
		
		assertFalse(client1.userOnline(user1.getUsername()));
		
		//Test User Online
		client1.setUser(user1);
		client1.verifyUser(user1.getUsername(), user1.getPassword());
		
		try {
		    Thread.sleep(500);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}

		assertTrue(client1.userOnline(user1.getUsername()));
	}
	
	
	@Test
	public void testuserOnlineRegistUserVerifyUser()
	{
		
		user = new User();
		user.setUsername("noob");
		user.setPassword("noob");
		user.setVerified(true);
		client1.setUser(user);
		client1.registerUser("noob", "noob");
		client1.verifyUser("noob", "noob");
		
		assertFalse(client1.userOnline("noob"));
	}
	
	@Test
	public void testGetDestination()
	{
		
		User user1 = new User();
		user1.setUsername("Savuthy");
		user1.setPassword("password");
		client1.setUser(user1);
		client1.verifyUser(user1.getUsername(), user1.getPassword());
		
		try {
		    Thread.sleep(500);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		assertNotNull(client1.getDestination(user1.getUsername()));
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
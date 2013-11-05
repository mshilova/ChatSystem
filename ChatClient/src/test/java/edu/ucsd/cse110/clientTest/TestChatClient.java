package edu.ucsd.cse110.clientTest;
import static org.junit.Assert.*;

import java.net.URISyntaxException;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.client.ChatClient;
import edu.ucsd.cse110.client.Constants;
import edu.ucsd.cse110.client.User;

public class TestChatClient {
	ChatClient client1;
	ChatClient client2;
	ChatClient client3;
	ChatClient client4;
	
	@Before
	public void setup () throws JMSException, URISyntaxException
	{
		client1 = wireClient();
	    client2 = wireClient();
	    client3 = wireClient();

	}
	@After
	public void tearDown () throws JMSException, URISyntaxException
	{
		client1 = wireClient();
	    client2 = wireClient();
	    client3 = wireClient();

	}

    /* Test that each client can send a broadcast message */
	@Test
	public void testChatClientBroadacast() throws URISyntaxException, JMSException 
	{

		try
		{
		  client1.send("broadcast", "test1");
		  assertEquals (client1.getMsg(), client2.getMsg());
		  assertEquals (client1.getMsg(), client3.getMsg());
		  assertEquals (client2.getMsg(), client3.getMsg());
		  
		  if (client1.equals("test1")&&client2.equals("test1") && client3.equals("test1")) 
		  {
		    client2.send("broadcast", "test2");
		    assertEquals (client2.getMsg(),"test2");
		    assertFalse (client2.getMsg().equals("test3"));
		    assertEquals (client1.getMsg(), client2.getMsg());
		    assertEquals (client1.getMsg(), client3.getMsg());
		    assertEquals (client2.getMsg(), client3.getMsg());
		  }
		  if (client1.equals("test2") && client2.equals("test2") && client3.equals("test2")) 
		  {
		    client3.send("broadcast", "test3");
		    assertEquals (client2.getMsg(),"test3");
		    assertEquals (client1.getMsg(), client2.getMsg());
		    assertEquals (client1.getMsg(), client3.getMsg());
		    assertEquals (client2.getMsg(), client3.getMsg());
		  }
		}
		catch (JMSException e)
		{
			fail("should not fail");
		}
		
       
	}
	
	/* Test to make sure non connected client does not receive a broadcast message */
	@Test
	public void testSingleClient () throws JMSException, URISyntaxException
	{
		
		try
		{
		  client1.send("broadcast", "test");
		  assertEquals (client1.getMsg(),"test");
		  assertNull(client4);
		  client4 = wireClient();
		  assertNull(client4.getMsg());
		}
		catch (JMSException e)
		{
			fail("should not fail");
		} 
	}
	
	
	
	private static ChatClient wireClient() throws JMSException, URISyntaxException {
		ActiveMQConnection connection;
		User user = new User();
		
		connection = 
				ActiveMQConnection.makeConnection(
						user.userName,//Constants.USERNAME,
						user.password,//Constants.PASSWORD,
						Constants.ACTIVEMQ_URL);
        connection.start();
        CloseHook.registerCloseHook(connection);
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // queue for client to receive messages from server
        // queue name will be the user name
        Queue incomingQueue = session.createQueue("max");//Constants.USERNAME);
        // queue for client to send messages to server
        Queue destQueue = session.createQueue(Constants.SERVERQUEUE);
        MessageProducer producer =  session.createProducer(null);
        MessageConsumer consumer = session.createConsumer(incomingQueue);
        
        TopicSession topicSession = connection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
        // topic for client to publish broadcast messages
        Topic broadcastTopic = topicSession.createTopic(Constants.BROADCAST);
        TopicPublisher publisher = topicSession.createPublisher(broadcastTopic);
        TopicSubscriber subscriber = topicSession.createSubscriber(broadcastTopic);
        
        return new ChatClient(session,producer,consumer,incomingQueue,destQueue,topicSession,publisher,subscriber,broadcastTopic);
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

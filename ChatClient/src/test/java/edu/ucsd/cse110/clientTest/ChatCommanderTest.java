package edu.ucsd.cse110.clientTest;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

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
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.client.ChatClient;
import edu.ucsd.cse110.client.ChatCommander;
import edu.ucsd.cse110.client.Constants;
import edu.ucsd.cse110.client.User;

public class ChatCommanderTest {

	protected ArrayList<String> chatRooms = new ArrayList<String>();
	private ArrayList<String> pendingInvitations = new ArrayList<String>();
	private ArrayList<TopicPublisher> publisherList = new ArrayList<TopicPublisher>();
	private ArrayList<TopicSubscriber> subscriberList = new ArrayList<TopicSubscriber>();
	private TopicSession topicSession;
	//private ChatClient client;
	
	private static ActiveMQConnection connection;
	private ChatClient client;
	private ChatCommander chatCommander;

	
	@Before
	public void setUp() throws Exception {
		client = ChatCommanderTest.wireClient(); 
		chatCommander = client.getChatCommander();
	}

	@After
	public void tearDown() throws Exception {
		connection.close();
	}

	@Test
	public void testChatCommander() {
		assertSame( client, chatCommander.getClient() );	
	}

	@Test
	public void testGetClient() {
		assertSame( client, chatCommander.getClient() );	
	}

	@Test
	public void testBroadcast() {
		
		assertFalse( chatCommander.broadcast( "" ) );
		assertFalse( chatCommander.broadcast( "             " ) );
		assertTrue( chatCommander.broadcast( "Broadcast works likes a boss." ) );

	}

	@Test
	public void testAddPendingInvitation() {
		assertTrue( chatCommander.addPendingInvitation( "Boss" ) );
		assertFalse( chatCommander.addPendingInvitation( "Boss" ) );
	}

	@Test
	public void testCreateChatRoom() {
		
		chatCommander.createChatRoom( "Boss" );
		chatCommander.createChatRoom( "Kacy" );
		assertEquals( "Kacy", chatCommander.removeLastRoomAdded() );
		assertEquals( "Boss", chatCommander.removeLastRoomAdded() );
		
	}

	@Test
	public void testLeaveChatRoom() {
		
		chatCommander.createChatRoom( "Boss" );
		try {
			assertTrue( chatCommander.leaveChatRoom( "Boss" ) );
			assertFalse( chatCommander.leaveChatRoom( "Boss" ) );
		} catch (JMSException e) {
		}
		
	}

	@Test
	public void testAdd() {
		
		chatCommander.add( "Boss" );
		chatCommander.add( "Kacy" );
		assertEquals( "Kacy", chatCommander.removeLastRoomAdded() );
		assertEquals( "Boss", chatCommander.removeLastRoomAdded() );
		
	}

	@Test
	public void testSetupChatRoomTopicString() {
		assertTrue( chatCommander.setupChatRoomTopic( "Boss" ) );
	}
	
	@Test
	public void testSetupChatRoomTopic() {
		chatCommander.add( "Boss" );
		assertEquals( "Boss", chatCommander.setupChatRoomTopic() );
	}

	@Test
	public void testAddSubscriber() {	
		TopicSubscriber subscriber = EasyMock.createMock( TopicSubscriber.class );		
		assertTrue( chatCommander.addSubscriber( subscriber ) );	
	}

	@Test
	public void testAddPublisher() {
		TopicPublisher publisher = EasyMock.createMock( TopicPublisher.class );
		assertTrue( chatCommander.addPublisher( publisher ) );
	}

	@Test
	public void testSendInvitation() {

	/*    
	    try {
	    	assertFalse( chatCommander.sendInvitation( "whoever", "Boss" ) );
	    	
	    	client.verifyUser( "Nobel", "password" );
	    	
			ChatClient clientToGetInvite = ChatCommanderTest.wireClient();
			clientToGetInvite.verifyUser( "Kacy", "password" );

			chatCommander.createChatRoom( "Boss" );
			assertTrue( chatCommander.sendInvitation( "Kacy", "Boss" ) );
		} catch (JMSException | URISyntaxException e1) {
		}
	    
*/
				
	}
	
	

	@Test
	public void testInviteToChatRoom() {
		
	}

	@Test
	public void testAcceptInvite() throws JMSException {
		assertFalse(chatCommander.acceptInvite("InvalidInput"));
		chatCommander.addPendingInvitation("Boss");
		assertTrue(chatCommander.acceptInvite("accept Boss"));
	}

	@Test
	public void testChatRoomEntered() {
		
		assertFalse(chatCommander.chatRoomEntered("OneString"));
		chatCommander.createChatRoom( "Boss" );
		chatCommander.updateAllChatRooms(chatCommander.getChatRooms());
		assertTrue(chatCommander.chatRoomEntered("Boss Message"));	
	}

	@Test
	public void testSubscribedToChatRoom() {
		chatCommander.createChatRoom( "Boss" );
		assertTrue(chatCommander.subscribedToChatRoom("Boss"));
	}

	@Test
	public void testAddToChatRoomList() {
		String[] str = {"Gang","Gangster","BossLife"};
		assertTrue(chatCommander.getChatRooms().size()==0);
		chatCommander.addToChatRoomList(str[0]);
		assertTrue(chatCommander.getChatRooms().size()==1);
		chatCommander.addToChatRoomList(str[1]);
		assertTrue(chatCommander.getChatRooms().size()==2);
		chatCommander.addToChatRoomList(str[2]);
		assertTrue(chatCommander.getChatRooms().size()==3);
		
		ArrayList<String> list = chatCommander.getChatRooms();
		Iterator<String> itr=list.iterator();
		int count = 0;
		while(itr.hasNext()){
			assertEquals(itr.next(),str[count]);
			count++;
		}
		
	}



	@Test
	public void testRequestUsersInChatRoom() throws InterruptedException {
		assertFalse(chatCommander.requestUsersInChatRoom("Gang"));
		chatCommander.createChatRoom("Gang");
		//Thread.sleep(3000);
		//assertTrue(chatCommander.requestUsersInChatRoom("Gang"));
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

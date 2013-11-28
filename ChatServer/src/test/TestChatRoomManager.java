import edu.ucsd.cse110.server.*; 
import static org.junit.Assert.*;

 import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;


 import javax.jms.Connection;
//import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestChatRoomManager {

	private ChatRoomManager manager;
	//private static ActiveMQConnection connection;
    private ActiveMQConnection connection;
	private Session session;
	//private ChatRoom room;
	
	@Before 
 	public void setUp()
 	{
		try {
 			connection = ActiveMQConnection.makeConnection(
 					/*currentUser, currentPassword,*/ Constants.ACTIVEMQ_URL);
 			CloseHook.registerCloseHook(connection);
 	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
 			manager = ChatRoomManager.getInstance();
 		} catch (JMSException e) {
 			 
 			//e.printStackTrace();
 			System.out.println( "Hello there");
 		} catch (URISyntaxException e) {
 			
 			//e.printStackTrace();
 			System.out.println("here?");
 		}
 	}
	
	@After
	public void tearDown()
	{
		manager.removeAllItems();
	}
	
	@Test
	public void testConstructor()
	{
	  ChatRoomManager man1 = ChatRoomManager.getInstance();
	  assertNotNull( man1 ); // Checks that chatroom manager is created
	}
	
	@Test
	public void testaddItem() // Adding correct inputs
	{
		ChatRoomManager managing = ChatRoomManager.getInstance();
		try
		{
			Message messy = session.createTextMessage("Bonnie chatroom1"); 
			assertTrue( managing.addItem( messy ) ); // Adding chatroom
			assertTrue( managing.containsItem( "chatroom1" ) ); // Checks if chatroom is in map
			
			Message messy1 = session.createTextMessage("Masha chatroom2");
			assertFalse( managing.containsItem( "chatroom2") );
			assertTrue( managing.addItem( messy1 ) );
		}
		catch( JMSException e )
		{
			fail(); // Should not go here
		}
	}
	
	@Test
	public void testaddItemsAgain() // Adding message with one parameter -- incorrect inputs
	{
		ChatRoomManager manage = ChatRoomManager.getInstance();
		try
		{
			Message messy = session.createTextMessage("Bonnie");
			assertFalse(manage.addItem(messy)); 
			assertFalse(manage.containsItem("chatroom"));
		}
		catch( JMSException e)
		{
			System.out.println("Not supposed to be here");
		}
	}
	
	@Test
	public void testcontainsItem() // Chatroom is contained in hashmap
	{
		ChatRoomManager manager = ChatRoomManager.getInstance();
		try
		{
			Message mess = session.createTextMessage("Bonnie chatting");
			assertFalse( manager.containsItem( "chatting" ) ); // Message not added
			assertTrue( manager.addItem( mess ));
			assertTrue( manager.containsItem("chatting") );
			assertFalse( manager.containsItem("chatting ") );
			assertFalse( manager.containsItem("Bonnie") ); // Not getting first value
			assertFalse( manager.containsItem("Masha") ); // Random one
		}
		catch( JMSException e)
		{
			fail(); // Should not go here
		}
	}
	
	@Test
	public void testcopyRoom()
	{
		try
		{
		  ChatRoomManager manager = ChatRoomManager.getInstance();
		  Message messy = session.createTextMessage( "create Chatroom1");
		  assertTrue( manager.addItem( messy ) );
		  assertTrue( manager.containsItem("Chatroom1") );
		
		  ChatRoom chat1 = manager.getRoom("Chatroom1" );
		  ChatRoom chat2 = manager.copyRoom( "Chatroom1" );
		  assertEquals( chat1.getName(), chat2.getName() );
		  assertSame( chat1.getName(), chat2.getName() );
		
		  ChatRoom chat3 = new ChatRoom("Chatroom1");
		  assertEquals( chat1.getName(), chat3.getName() );
		  assertNotSame( chat1.getName(), chat3.getName() );
		}
		catch( JMSException e)
		{
			fail(); // Should not go here
		}
	}
	
	@Test
	public void testgetRoom()
	{
		ChatRoomManager manage = ChatRoomManager.getInstance();
		try
		{
			Message mess = session.createTextMessage("create Chatroom" );
			assertTrue( manage.addItem( mess ) );
			ChatRoom chat1 = manage.getRoom("Chatroom");
			assertEquals("Chatroom", chat1.getName());
		}
		catch( JMSException e)
		{
			fail(); // Should not go here
		}
	}
	
	@Test
	public void testremoveItem()
	{
		ChatRoomManager manage = ChatRoomManager.getInstance();
		try
		{
			Message mess = session.createTextMessage("create Chatroom" );
			Message messy = session.createTextMessage("create Chatroom1" );
			assertTrue( manage.addItem( mess ) );
			assertTrue( manage.addItem( messy ) );
			assertTrue( manager.containsItem("Chatroom") );
			assertTrue( manager.containsItem("Chatroom1") );
			assertFalse( manager.containsItem("Chatroom2") );
			
			/* Will remove chatroom if the message is message[0] */
			Message messier = session.createTextMessage("Chatroom" ); //Comment this line out after debug
			assertTrue( manage.removeItem( messier ) ); // This should be true but is currently false
			                                            // Change messier back to mess 
			assertFalse( manager.containsItem("Chatroom") ); 
			assertTrue( manager.containsItem("Chatroom1") );
			assertFalse( manager.removeItem( mess ) );
		}
		catch( JMSException e)
		{
			fail(); // Should not go here
		}
	}
	
	/*@Test
	public void testaddUser()
	{
		ChatRoomManager manage = ChatRoomManager.getInstance();
		try
		{
			Message mess = session.createTextMessage("Bonnie Chatroom" );
			Message messy = session.createTextMessage("Kyle Chatroom" );
			assertTrue( manage.addItem( mess ) );
			ChatRoom chat1 = manage.getRoom("Chatroom");
			assertEquals( 0, chat1.numUsers() );
			Message user1 = session.createTextMessage("Bonnie Chatroom");
			Message user2 = session.createTextMessage("Kyle");
			//System.out.println( "hi");
			chat1 = manage.addUser( mess );
			chat1 = manage.addUser(messy);
			
			//System.out.println( "hey");
			//assertEquals( 1, manage.getRoom("Chatroom").numUsers() );
			assertTrue( manage.getRoom("Chatroom").containsUser("Kyle"));
			//System.out.println( "boo");
			//assertEquals( 1, chat1.numUsers() );
			//System.out.println( "hellloooo");
			//chat1 = manage.addUser( user2 );
			//assertEquals( 2, chat1.numUsers() );
			
		}
		catch( JMSException e)
		{
			fail(); // Should not go here
		}
	}*/
	
 	static private class CloseHook extends Thread {
 		ActiveMQConnection connection;
 		private CloseHook(Connection connection2) {
 			this.connection = (ActiveMQConnection) connection2;
 		}
 		
 		public static Thread registerCloseHook(Connection connection2) {
 			Thread ret = new CloseHook(connection2);
 			Runtime.getRuntime().addShutdownHook(ret);
 			return ret;
 		}
 		
 		public void run() {
 			try {
 				connection.close();
 			} catch (JMSException e) {
 				
 			}
 		}
 	}
}

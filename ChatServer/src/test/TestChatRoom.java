import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.server.Authenticator;
import edu.ucsd.cse110.server.ChatRoom;
import edu.ucsd.cse110.server.Constants;


public class TestChatRoom {

	private ChatRoom room;
	private static ActiveMQConnection connection;
	private Session session;
	private Destination dest1;
	private Destination dest2;
	private Destination dest3;
	private Destination dest4;
	private Destination dest5;
	
	@Before
	public void setUp() throws Exception {
	
		connection = ActiveMQConnection.makeConnection(
				/*currentUser, currentPassword,*/ Constants.ACTIVEMQ_URL);
	    connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        dest1 = session.createQueue("dest1");
        dest2 = session.createQueue("dest2");
        dest3 = session.createQueue("dest3");
        dest4 = session.createQueue("dest4");
        dest5 = session.createQueue("dest5");
		room = new ChatRoom("test");
	}

	@After
	public void tearDown() throws Exception {
		connection.close();
		room = null;
	}

	@Test
	public void testConstructor(){
		try{
			room = new ChatRoom("");
		}catch(IllegalArgumentException e){}
		assertTrue(room.getName() != null);
		assertTrue(room.getName().equals(""));
	}
	@Test
	public void testCopyConstructor(){
		ChatRoom room1 = new ChatRoom("toCopy");
		assertTrue(room1.addUser("Kyle", dest1));
		assertTrue(room1.addUser("Kacy", dest2));
		assertTrue(room1.addUser("Masha", dest3));
		assertTrue(room1.addUser("Nobel", dest4));
		assertTrue(room1.addUser("Hrach", dest5));
		
		room.addUser("Dude", dest1);
		assertFalse(room == room1);
		assertEquals(5, room1.numUsers());
		assertEquals(1, room.numUsers());
		
		room = new ChatRoom(room1);
		assertEquals(5, room.numUsers());
		assertTrue(room.containsUser("Kyle"));
		assertTrue(room.containsUser("Kacy"));
		assertTrue(room.containsUser("Masha"));
		assertTrue(room.containsUser("Nobel"));
		assertTrue(room.containsUser("Hrach"));
		assertFalse(room.containsUser("Dude"));
		
	}
	@Test
	public void testGetSetName() {
		assertEquals("test", room.getName());
		room.setName("another");
		assertEquals("another", room.getName());
	}
	@Test
	public void testGetSetNameNull(){
		try{
			room.setName(null);
			fail("set chatroom name to null");
		}catch(IllegalArgumentException e){}
		
		assertTrue(room.getName() != null);
	}
	
	@Test
	public void testNumUsersZero(){
		assertEquals(0, room.numUsers());
	}
	
	@Test
	public void testAddContainsNumUsers(){
		assertEquals(0, room.numUsers());
		assertFalse(room.containsUser("test"));
		
		room.addUser("Kyle", dest1);
		assertEquals(1, room.numUsers());
		assertTrue(room.containsUser("Kyle"));
		
		room.addUser("Kacy", dest2);
		assertEquals(2, room.numUsers());
		assertTrue(room.containsUser("Kacy"));
		
		room.addUser("Masha", dest3);
		assertEquals(3, room.numUsers());
		assertTrue(room.containsUser("Masha"));
		
		room.addUser("Nobel", dest4);
		assertEquals(4, room.numUsers());
		assertTrue(room.containsUser("Nobel"));
		
		room.addUser("Hrach", dest5);
		assertEquals(5, room.numUsers());
		assertTrue(room.containsUser("Hrach"));
		
		assertFalse(room.containsUser("Bonnie"));
		assertFalse(room.containsUser("Savuthy"));
	}
	
	@Test
	public void testAddRemove(){
		assertEquals(0, room.numUsers());
		assertFalse(room.containsUser("test"));
		
		assertTrue(room.addUser("Kyle", dest1));
		assertEquals(1, room.numUsers());
		assertTrue(room.containsUser("Kyle"));
		
		assertTrue(room.addUser("Hrach", dest5));
		assertEquals(2, room.numUsers());
		assertTrue(room.containsUser("Hrach"));
		
		assertTrue(room.removeUser("Kyle"));
		assertEquals(1, room.numUsers());
		assertFalse(room.containsUser("Kyle"));
		assertTrue(room.containsUser("Hrach"));
		
		assertTrue(room.removeUser("Hrach"));
		assertEquals(0, room.numUsers());
		assertFalse(room.containsUser("Hrach"));
	}
	
	@Test
	public void testAddRemoveAll(){
		assertEquals(0, room.numUsers());
		assertFalse(room.containsUser("test"));
		
		assertTrue(room.addUser("Kyle", dest1));
		assertEquals(1, room.numUsers());
		assertTrue(room.containsUser("Kyle"));
		
		assertTrue(room.addUser("Kacy", dest2));
		assertEquals(2, room.numUsers());
		assertTrue(room.containsUser("Kacy"));
		
		assertTrue(room.addUser("Masha", dest3));
		assertEquals(3, room.numUsers());
		assertTrue(room.containsUser("Masha"));
		
		assertTrue(room.addUser("Nobel", dest4));
		assertEquals(4, room.numUsers());
		assertTrue(room.containsUser("Nobel"));
		
		assertTrue(room.addUser("Hrach", dest5));
		assertEquals(5, room.numUsers());
		assertTrue(room.containsUser("Hrach"));
		
		room.removeAllUsers();
		assertEquals(0, room.numUsers());
		assertFalse(room.containsUser("Kyle"));
		assertFalse(room.containsUser("Kacy"));
		assertFalse(room.containsUser("Masha"));
		assertFalse(room.containsUser("Nobel"));
		assertFalse(room.containsUser("Hrach"));
		
		room.removeAllUsers();
		assertEquals(0, room.numUsers());
	}

	@Test
	public void testAddNull(){
		assertTrue(room.addUser("Kyle", dest1));
		assertTrue(room.addUser("Kacy", dest2));
		assertTrue(room.addUser("Masha", dest3));
		assertTrue(room.addUser("Nobel", dest4));
		assertTrue(room.addUser("Hrach", dest5));
		
		assertFalse(room.addUser(null, null));		
		assertFalse(room.addUser("test", null));
		assertFalse(room.addUser(null, dest5));
		
		assertEquals(5, room.numUsers());
		assertTrue(room.containsUser("Hrach"));
		assertTrue(room.containsUser("Kacy"));
		assertTrue(room.containsUser("Kyle"));
		assertTrue(room.containsUser("Nobel"));
		assertTrue(room.containsUser("Masha"));

	}
	
	@Test
	public void testAddRemoveDuplicate(){
		assertTrue(room.addUser("Kyle", dest1));
		assertTrue(room.addUser("Kacy", dest2));
		assertTrue(room.addUser("Masha", dest3));
		assertTrue(room.addUser("Nobel", dest4));
		assertTrue(room.addUser("Hrach", dest5));
		
		assertFalse(room.addUser("Kyle", dest1));
		assertFalse(room.addUser("Kyle", dest2));
		
		assertTrue(room.removeUser("Kyle"));
		assertFalse(room.removeUser("Kyle"));
	}
	
	@Test
	public void testAddGetAllUsers(){
		assertTrue(room.addUser("Kyle", dest1));
		assertTrue(room.addUser("Kacy", dest2));
		assertTrue(room.addUser("Masha", dest3));
		assertTrue(room.addUser("Nobel", dest4));
		assertTrue(room.addUser("Hrach", dest5));
		
		Map<String, Destination> testMap = new HashMap<String, Destination>();
		
		assertFalse(testMap.equals(room.getAllUsers()));
		testMap = room.getAllUsers();
		assertTrue(testMap.containsKey("Kyle"));
		assertTrue(testMap.containsKey("Kacy"));
		assertTrue(testMap.containsKey("Masha"));
		assertTrue(testMap.containsKey("Nobel"));
		assertTrue(testMap.containsKey("Hrach"));
		
		for(String user : testMap.keySet()){
			assertTrue(testMap.get(user) == room.getUserDestination(user) );
		}		
	}
	
	@Test
	public void testGetUserDestinationNull(){
		try{
			room.getUserDestination(null);
			fail("tried to get a null user");
		}catch(IllegalArgumentException e){}
	}
	
	@Test 
	public void testGetUserDestinationDNE(){
		assertEquals(null, room.getUserDestination("Kyle"));
		room.addUser("Kyle", dest1);
		assertEquals(dest1, room.getUserDestination("Kyle"));
	}
}

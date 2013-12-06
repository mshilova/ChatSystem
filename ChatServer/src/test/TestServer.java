import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.expect;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.print.attribute.standard.Destination;

import org.apache.activemq.ActiveMQConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;

import edu.ucsd.cse110.server.Authenticator;
import edu.ucsd.cse110.server.ChatRoom;
import edu.ucsd.cse110.server.ChatRoomManager;
import edu.ucsd.cse110.server.Constants;
import edu.ucsd.cse110.server.IServer;
import edu.ucsd.cse110.server.LoginManager;
import edu.ucsd.cse110.server.Server;


public class TestServer {

	IServer server;
	Server serv;
	Session session;
	Connection connection;
	JmsTemplate template;
	javax.jms.Destination destination;
	
	@Before
	public void setUp() throws Exception {
     server = createNiceMock(IServer.class);
     serv = new Server();
     connection = ActiveMQConnection.makeConnection(
				/*currentUser, currentPassword,*/ Constants.ACTIVEMQ_URL);
	 connection.start();
     session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	@After
	public void tearDown() throws Exception {
		connection.close();
	}

	@Test
	public void testServer() {
		assertEquals(serv.getLoginManager(),LoginManager.getInstance());
		assertEquals(serv.getChatRoomManager(),ChatRoomManager.getInstance());
		assertEquals(serv.getAuthenticator(),Authenticator.getInstance());
		assertNull(serv.getMessageType());
	}

	@Test
	public void testUpdateChatRoom() {
		ChatRoom room = new ChatRoom("test1");
		assertTrue(serv.updateChatRoom(room));
		assertFalse(server.updateChatRoom(null));
	}

	@Test
	public void testUpdateOnlineUsers() {
		assertTrue(serv.updateOnlineUsers(true));
		assertFalse(serv.updateOnlineUsers(false));
	}
	
	@Test
	public void testReceive() throws Exception {

		Message message = session.createTextMessage();
		try {
			
			message.setJMSType("better fail!");
			serv.receive(message);
			fail("Should have failed");
		} catch (Exception e) {
            // passes
		}
	}

	@Test
	public void testSendDestinationChatRoomString() throws JMSException {
	   ChatRoom room = new ChatRoom ("room");
       expect(server.send(destination, room, Constants.ACCEPTEDINVITE)).andReturn(true);
       replay(server);
       assertTrue(server.send(destination, room, Constants.ACCEPTEDINVITE));
	}

	@Test
	public void testSendDestinationBooleanString() {
		ChatRoom room = new ChatRoom ("room");
	    expect(server.send(destination, true, Constants.ACCEPTEDINVITE)).andReturn(true);
	    replay(server);
	    assertTrue(server.send(destination, true, Constants.ACCEPTEDINVITE));
	}

	@Test
	public void testSendDestinationMapOfStringDestinationString() {
		Map<String, javax.jms.Destination> myMap = new HashMap<String, javax.jms.Destination>();
		ChatRoom room = new ChatRoom ("room");
	    expect(server.send(destination,myMap, Constants.ACCEPTEDINVITE)).andReturn(true);
	    replay(server);
	    assertTrue(server.send(destination, myMap, Constants.ACCEPTEDINVITE));
	}
	
	
}




//Finish the Server tests.
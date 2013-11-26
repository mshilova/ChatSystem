import edu.ucsd.cse110.server.*; 
import static org.junit.Assert.*;

 import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

 import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

 import org.apache.activemq.ActiveMQConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLoginManager extends Server {

	Connection connection;
 	LoginManager manager;
 	Session session;
	
 	@Before 
 	public void setUp()
 	{
 		try {
 			connection();
 			manager = LoginManager.getInstance();
 		} catch (JMSException e) {
 			 // TODO Auto-generated catch block
 			e.printStackTrace();
 		} catch (URISyntaxException e) {
 			//  TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 	}
 	@After
 	public void tearDown()
 	{
 		manager.removeAllItems();
 	}
 	
 	
	@Test
	public void testGetInstance() {
		LoginManager manager1 = LoginManager.getInstance();
 		LoginManager manager2 = LoginManager.getInstance();
 		assertNotNull (manager1);
 		assertNotNull (manager2);
 		assertSame(manager1,manager2);
	}

	@Test
	public void testGetAllItems() {
 		Map <String, Destination> retMap = new HashMap<String,Destination>();
 		assertEquals(manager.getAllItems(),retMap);
	}

	@Test
	public void testRemoveItem() {
		LoginManager manager = LoginManager.getInstance();
		try {
			Message msg = session.createTextMessage("Nobel password");
			manager.addItem(msg);
			assertTrue(manager.removeItem(msg));
			assertFalse(manager.removeItem(msg));
			msg = session.createTextMessage("Nobel password");
			manager.addItem(msg);
			msg = session.createTextMessage("Kyle password");
			manager.addItem(msg);
			msg = session.createTextMessage("Masha password");
			manager.addItem(msg);
			msg = session.createTextMessage("Derrick");
			assertFalse(manager.removeItem(msg));
			msg = session.createTextMessage("Masha");
			assertTrue(manager.removeItem(msg));
			msg = session.createTextMessage("Nobel");
			assertTrue(manager.removeItem(msg));
			msg = session.createTextMessage("Kyle");
			assertTrue(manager.removeItem(msg));
			assertFalse(manager.removeItem(null));
		}
		catch(JMSException e)
		{
			fail();
		}
	}

	@Test
	public void testAddItem() {
		LoginManager manager = LoginManager.getInstance();
		try
		{
			Message msg = session.createTextMessage("Nobel password");
			assertTrue(manager.addItem(msg));
			assertFalse(manager.addItem(msg));
			msg = session.createTextMessage("Kyle");
			assertFalse(manager.addItem(msg));
			assertFalse(manager.addItem(null));
			msg = session.createTextMessage("Derrick doesn'tmatter");
		}
		catch(JMSException e)
		{	fail(); }
	}

	@Test
	public void testContainsItem() {
		LoginManager manager = LoginManager.getInstance();
		try
		{
			Message msg = session.createTextMessage("Nobel password");
			manager.addItem(msg);
			msg = session.createTextMessage("Kyle password");
			manager.addItem(msg);
			assertFalse(manager.containsItem("Masha"));
			assertTrue(manager.containsItem("Kyle"));
			manager.removeItem(msg);
			assertFalse(manager.containsItem("Kyle"));
		}
		catch(JMSException e)
		{
			fail();
		}
	}

	@Test
	public void testRemoveAllItems() {
		LoginManager manager = LoginManager.getInstance();
		try
		{
		
			Message msg = session.createTextMessage("Nobel password");
			manager.addItem(msg);
			assertTrue(manager.removeItem(msg));
			assertFalse(manager.removeItem(msg));
			msg = session.createTextMessage("Nobel password");
			manager.addItem(msg);
			msg = session.createTextMessage("Kyle password");
			manager.addItem(msg);
			msg = session.createTextMessage("Masha password");
			manager.addItem(msg);
			
			manager.removeAllItems();
				
			msg = session.createTextMessage("Derrick");
			assertFalse(manager.removeItem(msg));
			msg = session.createTextMessage("Masha");
			assertFalse(manager.removeItem(msg));
			msg = session.createTextMessage("Nobel");
			assertFalse(manager.removeItem(msg));
			msg = session.createTextMessage("Kyle");
			assertFalse(manager.removeItem(msg));
			assertFalse(manager.removeItem(null));
		}
		catch(JMSException e)
		{
			fail();
		}
	}

	
	private void connection() throws JMSException, URISyntaxException
 	{
 		connection = ActiveMQConnection.makeConnection(
 				/*currentUser, currentPassword,*/ Constants.ACTIVEMQ_URL);
         connection.start();
         CloseHook.registerCloseHook(connection);
         
         session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
         /*  for producing messages
         MessageProducer producer =  session.createProducer(null);
           for consuming messages
         Queue incomingQueue = session.createTemporaryQueue();
         MessageConsumer consumer = session.createConsumer(incomingQueue); */
 	}
 	
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
 				/* 
 				 * This means that the connection was already closed or got 
 				 * some error while closing. Given that we are closing the
 				 * client we can safely ignore this.
 				*/
 			}
 		}
 	}
	
}
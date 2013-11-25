
import static org.junit.Assert.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.server.Constants;
import edu.ucsd.cse110.server.MessageProcessor;;

public class MessageProcessorTest {

	private static ActiveMQConnection connection;
	private Session session;
	private Message msg;
	
	
	/** Use this to make messages to pass in to methods */
	private Message msgFact(String str){
		try{
			return session.createTextMessage(str);
		}catch(JMSException e){
			fail("Exception for message creation");
		}
		return null;
	}
	
	
	@Before
	public void setUp() throws Exception {
	
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
	public void testExtractName() {
		
		MessageProcessor processor = new MessageProcessor();
		
		String user = "Mike";
		String room = "CSE250";
		msg = msgFact(user + " " + room);		
		
		try
		{
			String ExstractString = processor.extractName(msg);
			assertEquals(user,ExstractString);
			
		}
		catch(Exception e)
		{
			fail();
		}
		
		
		msg = msgFact(user);
		
		try
		{
			String ExstractString[] = processor.extractTwoArgs(msg);
			assertEquals(null, ExstractString);
		}
		catch(Exception e)
		 {	
			fail();
		 }
		
	}

	@Test
	public void testExtractTwoArgs() {
				
		MessageProcessor processor = new MessageProcessor();
		
		String user = "Mike";
		String room = "CSE250";
		msg = msgFact(user + " " + room);		
		
		try
		{
			String ExstractString[] = processor.extractTwoArgs(msg);
			
			assertEquals(user,ExstractString[0]);
			assertEquals(room,ExstractString[1]);
			
		}
		catch(Exception e)
		{
			fail();
		}
		
		
		msg = msgFact(user);
		
		try
		{
			String ExstractString[] = processor.extractTwoArgs(msg);
			assertEquals(null, ExstractString);
		}
		catch(Exception e)
		 {	
			fail();
		 }
		
	}

}

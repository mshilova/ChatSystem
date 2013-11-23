

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.ucsd.cse110.server.Authenticator;
import edu.ucsd.cse110.server.Constants;

public class TestAuthenticator {
	
	private static ActiveMQConnection connection;
	private Session session;
	private Message msg;
	private Authenticator auth;
	
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

	/* test singleton property */
	@Test 
	public void testGetInstance(){
		Authenticator authenticator2;
		assertEquals(null, auth);
		auth = Authenticator.getInstance();
		assertTrue(null != auth);
		authenticator2 = Authenticator.getInstance();
		assertTrue(auth == authenticator2);
	}
	
	@Test
	public void testAuthenticateNull(){
		auth = Authenticator.getInstance();
		assertFalse(auth.authenticate(null, "password"));
		assertFalse(auth.authenticate("Kyle", null));
		assertFalse(auth.authenticate(null, null));
	}
	
	@Test
	public void testAuthenticateEmptyString(){
		auth = Authenticator.getInstance();
		assertFalse(auth.authenticate("", "password"));
		assertFalse(auth.authenticate("Kyle", ""));
		assertFalse(auth.authenticate("", ""));
	}
	
	@Test
	public void testAuthenticateInvalidUserAndPassword(){
		auth = Authenticator.getInstance();
		assertFalse(auth.authenticate("Josad", "password"));
		assertFalse(auth.authenticate("Kyle", "dogg"));
		assertFalse(auth.authenticate("Kyle", "Password"));
		assertFalse(auth.authenticate("Kyle", "passworD"));
		assertFalse(auth.authenticate("kyle", "password"));
	}
	
	@Test
	public void testAuthenticateValidUsers(){
		auth = Authenticator.getInstance();
		assertTrue(auth.authenticate("Bonnie", "rabbit"));
		assertTrue(auth.authenticate("Hrach", "turtle"));
		assertTrue(auth.authenticate("Masha", "password"));
		assertTrue(auth.authenticate("Kacy", "password"));
		assertTrue(auth.authenticate("Kyle", "password"));
	}


	@Test
	public void testRegisterUserNull(){
		auth = Authenticator.getInstance();
		assertFalse(auth.registerUser(null));
	}
	
	@Test
	public void testRegisterUserBadUserFields(){
		auth = Authenticator.getInstance();
		
		msg = msgFact("");
		assertFalse(auth.registerUser(msg));
		
		msg = msgFact(" ");
		assertFalse(auth.registerUser(msg));
		
		msg = msgFact("dog");
		assertFalse(auth.registerUser(msg));
		
		msg = msgFact(" dog dog");
		assertFalse(auth.registerUser(msg));
		
		msg = msgFact("dud reg");
		assertFalse(auth.registerUser(msg));
		
		msg = msgFact("1234 ");
		assertFalse(auth.registerUser(msg));
		
		msg = msgFact(" asdd21");
		assertFalse(auth.registerUser(msg));
	}
	
	@Test
	public void testRegisterExistingUsers(){
		auth = Authenticator.getInstance();
		
		msg = msgFact("Kyle password");
		assertFalse(auth.registerUser(msg));
		
		msg = msgFact("Kacy password");
		assertFalse(auth.registerUser(msg));
		
		msg = msgFact("Hrach turtle");
		assertFalse(auth.registerUser(msg));
	}
	
	@Test
	public void testRegisterValidNewUser(){
		Scanner reader;
		String line;
		auth = Authenticator.getInstance();
		
		msg = msgFact("Test1 test1");
		assertTrue(auth.registerUser(msg));
		
		msg = msgFact("another another");
		assertTrue(auth.registerUser(msg));
		
		msg = msgFact("written towrite");
		assertTrue(auth.registerUser(msg));
		
		try {
			reader = new Scanner(new File("UserPass.list"));
			
			while(null != (line = reader.nextLine()) ){
				
			}
		} catch (FileNotFoundException e) {
			fail("exception while reading ");
			e.printStackTrace();
		}
		

	}
	
	private Message msgFact(String str){
		try{
			return session.createTextMessage(str);
		}catch(JMSException e){
			fail("Exception for message creation");
		}
		return null;
	}
}

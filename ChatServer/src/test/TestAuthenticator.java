

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.server.Authenticator;
import edu.ucsd.cse110.server.Constants;

/**
 * In order to run these tests, the server MUST BE RUNNING.
 * @author not derrick
 *
 */
public class TestAuthenticator {
	
	private static ActiveMQConnection connection;
	private Session session;
	private Message msg;
	private Authenticator auth;
	
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

	// test singleton property 
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
 		BufferedReader reader;
	    BufferedWriter writer;
		String line;
		List<String> users = new ArrayList<String>();
		users.add("Bonnie rabbit");
		users.add("Hrach turtle");
		users.add("Kyle password");
		users.add("Kacy password");
		users.add("Masha password");
		users.add("Nobel password");
		users.add("Savuthy password");
		auth = Authenticator.getInstance();
		
		msg = msgFact("Tester1 test1");
		assertTrue(auth.registerUser(msg));
		
		msg = msgFact("another1 another");
		assertTrue(auth.registerUser(msg));
		
		msg = msgFact("written1 towrite");
		assertTrue(auth.registerUser(msg));
		
		try {
			reader = new BufferedReader(new FileReader("UserPass.list"));
			int numNewUsers = 0;
			while(null != (line = reader.readLine())){
				line.trim();
				if(line.equals("Tester1 test1")||
				   line.equals("another1 another")||
				   line.equals("written1 towrite"))
					++numNewUsers;
			}
			assertEquals(3, numNewUsers);
			reader.close();
			
			writer = new BufferedWriter(new FileWriter("UserPass.list",false));
			for(String user : users){
				writer.append(user);
				writer.newLine();
			}
			writer.close();

		} catch (FileNotFoundException e) {
			fail("exception while reading ");
			e.printStackTrace();
		}catch(IOException e){
			fail("write error");
		}

	}
	

}

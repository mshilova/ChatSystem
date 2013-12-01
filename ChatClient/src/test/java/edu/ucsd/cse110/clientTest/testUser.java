package edu.ucsd.cse110.clientTest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.client.User;


public class testUser {
    User user;
    @Before 
    public void Setup()
    {
    	user = new User ("abc", "123", true);
    }
    
    @After
    public void TearDown()
    {
    	user = null;
    }
    
    /* Tests the User() constructor 
     * 
     */
	@Test
	public void testnoParamUser() {
		user = new User();
		assertFalse(user.getVerified());

	}

	/* Tests the User(String, String, Boolean) constructor 
	 * 
	 */
	@Test
	public void testUserStringStringBoolean() {
		user = new User ("123", "abc", true);
		assertTrue(user.getVerified());
		assertEquals(user.getPassword(), "abc");
		assertEquals(user.getUsername(), "123");
		assertFalse(user.getPassword().equals("123"));
		assertFalse(user.getPassword().equals("abC"));;
		user = new User ("123", "abc", false);
		assertFalse(user.getVerified());
		assertEquals(user.getPassword(), "abc");
		assertEquals(user.getUsername(), "123");
		assertFalse(user.getPassword().equals("123"));
		assertFalse(user.getPassword().equals("ABC"));;
	}

	/* Tests the getUserName method 
	 * 
	 */
	@Test
	public void testGetUsername() 
	{
		assertEquals(user.getUsername(), "abc");
		assertFalse(user.getUsername().equals("aBC"));
		user.setUsername("Derrick");
		assertEquals(user.getUsername(), "Derrick");
		assertFalse(user.getUsername().equals("derriCK"));
		user.setUsername("abc");
		assertEquals(user.getUsername(), "abc");
		user = new User();
		assertNull(user.getUsername());
	}

	/* Test for the setUsername method 
	 * 
	 */
	@Test
	public void testSetUsername() {
		user.setUsername("Derrick");
		assertEquals(user.getUsername(), "Derrick");
		assertFalse(user.getUsername().equals("derriCK"));
		user.setUsername("abc");
		assertEquals(user.getUsername(), "abc");
		user.setUsername(null);
		assertNull(user.getUsername());
	}

	/* Test for the getPassword method 
	 * 
	 */
	@Test
	public void testGetPassword() {
		assertEquals(user.getPassword(), "123");
		assertFalse(user.getPassword().equals("12 3"));
		user.setPassword("Derrick");
		assertEquals(user.getPassword(), "Derrick");
		assertFalse(user.getPassword().equals("derriCK"));
		user.setPassword("12bc");
		assertEquals(user.getPassword(), "12bc");
		user = new User();
		assertNull(user.getPassword());
	}
	
	/* Test for the setPassword method 
	 * 
	 */

	@Test
	public void testSetPassword() {
		user.setPassword("Derrick");
		assertEquals(user.getPassword(), "Derrick");
		assertFalse(user.getPassword().equals("derriCK"));
		user.setPassword("12bc");
		assertEquals(user.getPassword(), "12bc");
		user.setPassword(null);
		assertNull(user.getPassword());
	}

	/* Test for the setVarified method 
	 * 
	 */
	@Test
	public void testSetVerified() {
		assertTrue(user.getVerified());
		user.setVerified(true);
		assertTrue(user.getVerified());
		user.setVerified(false);
		assertFalse(user.getVerified());
	}

	/* Test for the getVerified method 
	 * 
	 */
	@Test
	public void testGetVerified() {
		assertTrue(user.getVerified());
		user.setVerified(true);
		assertTrue(user.getVerified());
		user.setVerified(false);
		assertFalse(user.getVerified());
	}

}

package edu.ucsd.cse110.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;


public class Authenticator {
    
    private static Authenticator authenticator = null;
	private Map <String, String> userData;
	private MessageProcessor processor;
	private final String filepath = "UserPass.list";	// should be fine if this file is in the same directory as Server.java
	
	private Authenticator(){
	    try {
	    	createUserData();
	    	processor = new  MessageProcessor();
	    } catch (IOException e) {
	    	System.out.println("userData creation error: " + e.getMessage());
	    }
	}
	
	public static Authenticator getInstance(){
	    if(authenticator == null)
		authenticator = new Authenticator();
	    return authenticator;
	}
	
	public boolean authenticate(String username, String password){
	
	    if(null == username || null == password )
	    	return false;
	    
	    if( userData.containsKey(username) ){
	    	if( userData.get(username).equals(password) ){
	    	    return true;
	    	}
	    }	 
	    return false;
	}
	
	public boolean registerUser(Message msg){    
	    BufferedWriter writer;
	    String[] userInfo;

	    try{
	    	userInfo = processor.extractTwoArgs(msg);
		
	    	if(userInfo == null)
	    		return false;	
	    	if( userData.containsKey(userInfo[0]) )
	    	    return false;
			if(userInfo[0].length() < Constants.MINFIELDLENGTH || 
			   userInfo[1].length() < Constants.MINFIELDLENGTH   )
				return false;
	    	writer = new BufferedWriter(new FileWriter(filepath,true));
	    	writer.newLine();
	    	writer.append(userInfo[0] + " " + userInfo[1]);
	    	writer.close();
	    	userData.put(userInfo[0], userInfo[1]);
	    	return true;
	    	
	    }catch(IOException e){ System.out.println(e.getMessage()); }
	    catch(JMSException e){ System.out.println(e.getMessage()); }

	   return false; 
	}
	
	
	/**
	  * This method will read from a file containing users/password
	  * and add to Hashmap	
	  * @throws IOException
     */
 	private void createUserData() throws IOException {
 		int space=0;
 		String userName;
 		String password;
 		String line;
		  		
		userData = new HashMap<String,String>();
 		BufferedReader reader = new BufferedReader(new FileReader(filepath));
 		
		while( null != (line = reader.readLine()) ){
			line     = line.trim();
		  	space    = line.indexOf(" ");
	  		userName = line.substring(0, space);
	  		password = line.substring(space+1,line.length());
	  		userData.put(userName, password);
		}
		reader.close();
	}
}

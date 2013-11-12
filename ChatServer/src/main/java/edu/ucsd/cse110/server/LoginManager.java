package edu.ucsd.cse110.server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jms.*;


public class LoginManager{
		
	private Map <String, Destination> onlineClients;
	private Map <String, String> userData;
	private final String filepath = "UserPass.list";	// should be fine if this file is in the same directory as Server.java
	
	protected LoginManager(){
		onlineClients = new HashMap <String, Destination>();
		userData = new HashMap<String, String>();
		try {
			createUserData();
		} catch (IOException e) {
 			System.out.println("userData creation error: " + e.getMessage());
		}
	}  
	
	protected Map<String, Destination> getAllOnlineUsers (){     
	    Map <String, Destination> retMap = new HashMap<String,Destination>();
	    retMap.putAll(onlineClients);
	    
	    return retMap;
	}  

	protected Destination getClientID(String usr){
		return onlineClients.get(usr); 
	}
	
	protected boolean logOffUser(Message msg){
	    if( !(msg instanceof TextMessage))
		return false;
	    
	    TextMessage temp = (TextMessage) msg;
	    String user;
	    try {
		user = temp.getText();
		if(onlineClients.containsKey(user)){
		    onlineClients.remove(user);
		    return true;
		}
	    } catch (JMSException e) { e.printStackTrace(); }
	    
	    return false;
	}
	    
	protected boolean registerUser(Message msg){
	    if( !(msg instanceof TextMessage) )
		return false;		
	    
	    BufferedWriter writer;
	    TextMessage temp = (TextMessage) msg;
	    String[] userInfo; 
	    
	    try{
		userInfo = temp.getText().split(" ");
	
		if( null != userData.get(userInfo[0]) )
		    return false;
		
		if(userInfo[0].length() < Constants.MINFIELDLENGTH || 
		   userInfo[1].length() < Constants.MINFIELDLENGTH   )
		    return false;
		
		writer = new BufferedWriter(new FileWriter(filepath,true));
		writer.append(userInfo[0] + " " + userInfo[1]);
		writer.close();
		userData.put(userInfo[0], userInfo[1]);
		onlineClients.put(userInfo[0], msg.getJMSReplyTo());
		return true;
		    
	    }catch(IOException e){
		System.out.println(e.getMessage());
	    }
	    catch(JMSException e){
		System.out.println(e.getMessage());
	    }    
	   return false; 
	}
	
	protected boolean checkUserOnline(String user){
		return onlineClients.containsKey(user);
	}
	
	protected boolean validateUser(Message msg){
		
	    if( !(msg instanceof TextMessage) ) 
		return false;
		
	    TextMessage temp = (TextMessage) msg;
	    String[] userInfo;
	    
	    try{
		userInfo = temp.getText().split(" ");
		
		if(this.checkUserOnline(userInfo[0]))
		    return false;
		
		if( userData.containsKey(userInfo[0]) ){
		    if( userData.get(userInfo[0]).equals(userInfo[1]) ){
			onlineClients.put(userInfo[0], msg.getJMSReplyTo());
			return true;
		    }
		}	
	    }catch(JMSException e){
		System.out.println(e.getMessage());
	    }
	    return false;
	}
	

	  
	/**
	  * This method will read from a file containing users/password
	  * and add to Hashmap	
	  * @throws IOException
      */
  	protected void createUserData() throws IOException {
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
 

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
	
    	private static LoginManager manager = null;
	private Map <String, Destination> onlineClients;
	private Authenticator authenticator;
	private MessageProcessor processor;

	
	private LoginManager(){
		onlineClients = new HashMap <String, Destination>();
		authenticator = Authenticator.getInstance();
		processor = new MessageProcessor();
	}  
	
	protected static LoginManager getInstance(){
	    if( manager == null)
		manager = new LoginManager();
	    
	    return manager;
	}
	
	protected Map<String, Destination> getAllOnlineUsers (){     
	    Map <String, Destination> retMap = new HashMap<String,Destination>();
	    retMap.putAll(onlineClients);
	    
	    return retMap;
	}  
	
	protected boolean logOffUser(Message msg){
	    String user;
	    
	    try {
		user = processor.logOffMessage(msg);
		
		if(null == user)
		    return false;	
	    	if(onlineClients.containsKey(user)){
	    	    onlineClients.remove(user);
	    	    return true;
	    	}    	
	    } catch (JMSException e) {
		System.out.println(e.getMessage());
	    }
	    return false;
	}
	    
	protected boolean logInUser(Message msg){
	    String[] userInfo;
	   
	    try {
		userInfo = processor.loginMessage(msg);
		
		if(userInfo == null)
		    return false;		
		if(onlineClients.containsKey(userInfo[0]))
		    return false;
		if(authenticator.authenticate(userInfo[0], userInfo[1])){
		    onlineClients.put(userInfo[0],msg.getJMSReplyTo());
		    return true;
		}
	    } catch (JMSException e) {} 
	    return false;
	}

	protected boolean checkUserOnline(String user){
		return onlineClients.containsKey(user);
	}
	
	

		  
}
 

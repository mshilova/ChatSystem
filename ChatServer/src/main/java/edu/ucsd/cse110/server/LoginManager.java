package edu.ucsd.cse110.server;
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
	
	public static LoginManager getInstance(){
	    if( manager == null)
	    	manager = new LoginManager();
	    
	    return manager;
	}
	
	public Map<String, Destination> getAllOnlineUsers (){     
	    Map <String, Destination> retMap = new HashMap<String,Destination>();
	    retMap.putAll(onlineClients);
	    
	    return retMap;
	}  
	
	public boolean logOffUser(Message msg){
	    String user;
	    
	    try {
	    	user = processor.oneArg(msg);
		
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
	    
	public boolean logInUser(Message msg){
	    String[] userInfo;
	   
	    try {
	    	userInfo = processor.twoArgs(msg);
		
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

	public boolean checkUserOnline(String user){
		return onlineClients.containsKey(user);
	}
	
	public void logAllOff()
	{
		onlineClients.clear();
	}
	

		  
}
 

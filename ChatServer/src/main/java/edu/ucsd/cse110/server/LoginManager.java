package edu.ucsd.cse110.server;
import java.util.HashMap;
import java.util.Map;

import javax.jms.*;


public class LoginManager implements Manager{
	
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
	

	public Map<String, Destination> getAllItems(){     
	    Map <String, Destination> retMap = new HashMap<String,Destination>();
	    retMap.putAll(onlineClients);
	    
	    return retMap;
	}  

	
	public boolean removeItem(Message message){
	    String user;
	    
	    try {
	    	user = processor.extractName(message);
		
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
	 
	public boolean addItem(Message message){
	    String[] userInfo;
	   
	    try {
		userInfo = processor.extractTwoArgs(message);
		
		if(userInfo == null)
		    return false;	
		
		if(userInfo[0].length() < Constants.MINFIELDLENGTH || 
		   userInfo[1].length() < Constants.MINFIELDLENGTH   )
			return false;
	
		if(onlineClients.containsKey(userInfo[0]))
		    return false;
		
		if(authenticator.authenticate(userInfo[0], userInfo[1])){
		    onlineClients.put(userInfo[0], message.getJMSReplyTo());
		    return true;
		}
	    } catch (JMSException e) {} 
	    return false;
	}

	public boolean containsItem(String item){
		return onlineClients.containsKey(item);
	}
	
	public void removeAllItems(){
		onlineClients.clear();
	}
		  
}
 

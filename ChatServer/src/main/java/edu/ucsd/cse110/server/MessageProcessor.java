package edu.ucsd.cse110.server;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class MessageProcessor {
    
    
    public String extractName(Message msg) throws JMSException{
    	if( !(msg instanceof TextMessage))
    	    return null;
    	
    	String mess = ((TextMessage) msg).getText();
    	
    	if(null == mess)
    		return null;
    	String[] temp = mess.split(" ");
    	
    	if(temp.length < 1 )
    	    return null;
    	
    	return temp[0];
    }
    
    public String[] extractTwoArgs(Message msg) throws JMSException{
    	if( !(msg instanceof TextMessage))
    	    return null;
    	
    	String mess = ((TextMessage) msg).getText();
    	
    	if(null == mess)
    		return null;
    	
    	String[] temp = mess.split(" ");
    	
    	if(temp.length < 2 )
    	    return null;
    	
    	
    	return temp;
    }
}

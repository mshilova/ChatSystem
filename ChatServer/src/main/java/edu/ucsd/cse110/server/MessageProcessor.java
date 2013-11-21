package edu.ucsd.cse110.server;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class MessageProcessor {

    /**
     * 
     * @param msg
     * @return array of tokens in the msg text. Splits on " ".
     * @throws JMSException
     */
    public String[] twoArgs(Message msg) throws JMSException{
	
    	if( !(msg instanceof TextMessage))
    		return null;
	
    	String[] temp = ((TextMessage) msg).getText().split(" ");
	
    	if(temp.length < 2 )
    		return null;
    	
    	return temp;
    }
    
    /**
     * 
     * @param msg
     * @return First token in msg object's text.
     * @throws JMSException
     */
    public String oneArg(Message msg) throws JMSException{
    	if( !(msg instanceof TextMessage))
    		return null;
	
    	String[] temp = ((TextMessage)msg).getText().split(" ");
	
    	if(temp.length < 1)
    		return null;
	
    	return temp[0];
    }
}

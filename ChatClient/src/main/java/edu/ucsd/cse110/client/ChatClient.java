package edu.ucsd.cse110.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class ChatClient implements MessageListener {
	private MessageConsumer consumer;
	private MessageProducer producer;
	private Session session;
	
	public ChatClient(MessageConsumer consumer, MessageProducer producer, Session session) {
		super();
		this.consumer = consumer;
		this.producer = producer;
		this.session = session;
		// set consumer to listen for messages from incomingQueue
		try {
			this.consumer.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	
	public void send(String TypeofMessage,String msg) throws JMSException {
		
		Message myMessage = session.createObjectMessage(msg);
		myMessage.setJMSType(TypeofMessage);
		// TODO fix this later
		// need to have a way to specify the reply address...
		//myMessage.setJMSReplyTo(Constants.QUEUENAME);
		producer.send(myMessage);
	}
	
	// send message to server's queue directly
	// server must then forward that message to all online users/wired clients
/*	public void broadcast(String msg) throws JMSException {
		producer.send(session.createTextMessage(msg));
	}
	*/
	public void onMessage(Message message) {
		System.out.println(message.toString());
	}

}

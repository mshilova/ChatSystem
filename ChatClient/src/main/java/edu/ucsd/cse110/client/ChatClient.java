package edu.ucsd.cse110.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class ChatClient implements MessageListener {
	private MessageConsumer consumer;
	private MessageProducer producer;
	private Session session;
	private Queue incomingQueue;
	
	public ChatClient(MessageProducer producer, Session session) {
		super();
		
		this.producer = producer;
		this.session = session;
		try {
			this.incomingQueue = session.createQueue(Constants.QUEUENAME);
			this.consumer = session.createConsumer(incomingQueue);
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//inQueue;
		// set consumer to listen for messages from incomingQueue
		try {
			this.consumer.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	// Broadcast
	public void send(String msg) throws JMSException {
		Message message = session.createTextMessage(msg);
		message.setJMSReplyTo(incomingQueue);
		producer.send(message);
	}
	
	// Send to specific user
	public void send(String usr, String msg) throws JMSException {
		Message message = session.createTextMessage(msg);
		message.setJMSReplyTo(incomingQueue);
		message.setJMSType(usr);
		producer.send(message);
	}
	
	public void onMessage(Message message) {
		try {
			System.out.println(((TextMessage) message).getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

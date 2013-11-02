package edu.ucsd.cse110.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

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
	
	public void send(String msg) throws JMSException {
		producer.send(session.createTextMessage(msg));
	}
	
	public void onMessage(Message message) {
		System.out.println(message.toString());
	}

}

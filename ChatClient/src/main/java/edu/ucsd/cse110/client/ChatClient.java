package edu.ucsd.cse110.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

public class ChatClient implements MessageListener {
	private MessageConsumer consumer;
	private MessageProducer producer;
	private Session session;
	private Queue incomingQueue;
	private TopicSession topicSession;
	private TopicPublisher publisher;
	private TopicSubscriber subscriber;
	private Topic broadcastTopic;
	
	public ChatClient(Session session,
			MessageProducer producer,
			MessageConsumer consumer,
			Queue inQueue,
			Queue destQueue,
			TopicSession topicSession,
			TopicPublisher publisher,
			TopicSubscriber subscriber,
			Topic broadcastTopic) {
		super();
		
		this.session = session;
		this.incomingQueue = inQueue;
		this.producer = producer;
		this.consumer = consumer;
		this.topicSession = topicSession;
		this.publisher = publisher;
		this.subscriber = subscriber;
		this.broadcastTopic = broadcastTopic;
		try {
			this.consumer.setMessageListener(this);
			this.subscriber.setMessageListener(this);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	
	/**
	 * Send to specific user or publish to all users
	 * @param	usr	username of the message recipient, or "Broadcast" for all users
	 * @param	msg	the message the send
	 */
	public void send(String usr, String msg) throws JMSException {
		Message message = session.createTextMessage(msg);
		message.setJMSType(usr);
		message.setJMSReplyTo(incomingQueue);
		if(usr.equalsIgnoreCase("broadcast")) {
			System.out.println("publisher publishing");
			publisher.publish(message);
		} else {
			System.out.println("producer sending");
			producer.send(message);
		}
	}
	
	/**
	 * What to do when this chat client receives a message
	 * @param	message	the message received
	 */
	public void onMessage(Message message) {
		try {
			System.out.println("Client received: " + ((TextMessage) message).getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

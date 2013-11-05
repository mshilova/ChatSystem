package edu.ucsd.cse110.client;

import java.util.Scanner;

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
	 * @param usr	user-name of the message recipient, or "Broadcast" for all users
	 * @param msg	the message the send
	 */
	public void send(String usr, String msg) throws JMSException {
		Message message = session.createTextMessage(msg);
		message.setJMSType(usr);
		message.setJMSReplyTo(incomingQueue);
		
		if(usr.equalsIgnoreCase("broadcast")) {
			publisher.publish(message);
			System.out.println("Message broadcasted.");
		} else {
			// TODO make a check from trying to send to unidentified user names
			producer.send(session.createQueue(usr), message);
			System.out.println("Message sent.");
		}
	}
	
	/**
	 * What to do when this chat client receives a message
	 * @param message	the message received
	 */
	public void onMessage(Message message) {
		try {
			System.out.println("Message from "
					+ message.getJMSReplyTo().toString().substring("queue://".length()) + ": "
					+ ((TextMessage) message).getText());
			System.out.println("Enter a message:");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

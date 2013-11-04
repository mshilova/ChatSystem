package edu.ucsd.cse110.server;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class Server {

	/**
	 * This method is called when the server receives a message from a client
	 * producer
	 * @param msg	the message received
	 */
	public void receive(TextMessage msg) {
		System.out.println("Server received a message:");
		
		try {
			System.out.println("Text: " + msg.getText());
			System.out.println("Type: " + msg.getJMSType());
			System.out.println("ReplyTo: " + msg.getJMSReplyTo());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AnnotationConfigApplicationContext context = 
		          new AnnotationConfigApplicationContext(ChatServerApplication.class);
		MessageCreator messageCreator = new MessageCreator() {
			public TextMessage createMessage(Session session) throws JMSException {
				return session.createTextMessage("ping! from server.java");
			}
        };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        
        try {
        	// get the queue to reply to from the message
			jmsTemplate.send(msg.getJMSReplyTo(), messageCreator);
			System.out.println("Message sent to " + msg.getJMSType());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        context.close();
	}
	
}

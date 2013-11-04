package edu.ucsd.cse110.server;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class Server {

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
        System.out.println("Sending a new message to " + Constants.QUEUENAME);
        try {
			jmsTemplate.send(msg.getJMSReplyTo(), messageCreator);
		} catch (JmsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("Message sent to " + Constants.QUEUENAME);
	}
	
}

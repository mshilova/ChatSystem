package edu.ucsd.cse110.server;

import java.io.Serializable;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class Server {

	
//	public void receive(Serializable msg) throws JMSException {
//		System.out.println("receive method");
//		Message o = (TextMessage)msg;
//		System.out.println(msg.getJMSType());
//	    System.out.println(((Message) msg).getJMSType());
//	    System.out.println(msg);
//		Message mess = (Message)msg;
//		System.out.println(((TextMessage)mess).getText());
//    	if(((Message)msg).getJMSType().equals("Broadcast"))
//    		System.out.println(msg.toString());
//	}
//	public void receive(TextMessage msg) throws JMSException {
//		System.out.println("receive method");
//		if(msg.getJMSType().equals("Broadcast"))
//			System.out.println(msg.toString());
//		else
//			System.out.println(msg);
//	}
	public void receive(TextMessage msg) {//(String msg) {
		try {
			System.out.println(msg.getText());
			System.out.println(msg.getJMSType());
			System.out.println(msg.getJMSReplyTo());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AnnotationConfigApplicationContext context = 
		          new AnnotationConfigApplicationContext(ChatServerApplication.class);
		MessageCreator messageCreator = new MessageCreator() {
			public TextMessage createMessage(Session session) throws JMSException {
				return session.createTextMessage("ping2!");
			}
        }; 
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println("Sending a new message:2");
        jmsTemplate.send(Constants.QUEUENAME, messageCreator);
        System.out.println("servesentmessagetoclient");
	}
	
}

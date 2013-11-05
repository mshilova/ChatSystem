package edu.ucsd.cse110.server;

import java.util.HashMap;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.apache.activemq.security.SimpleAuthenticationPlugin;

@Configuration
@ComponentScan
public class ChatServerApplication {

	private static HashMap<String,String> userList;
	
	public static void createUserList() {
		userList = new HashMap<String,String>();
		userList.put("user1","password");
		userList.put("user2","password");
		userList.put("user3","password");
	}
	
    @Bean
    ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(
                new ActiveMQConnectionFactory(Constants.ACTIVEMQ_URL));
    }
    
    @Bean
    MessageListenerAdapter receiver() {
        return new MessageListenerAdapter(new Server()) {{
            setDefaultListenerMethod("receive");
            // allow sending of Message objects instead of the default stringS
            setMessageConverter(null);
        }};
    }
    
    @Bean
    SimpleMessageListenerContainer container(final MessageListenerAdapter messageListener,
            final ConnectionFactory connectionFactory) {
        return new SimpleMessageListenerContainer() {{
            setMessageListener(messageListener);
            setConnectionFactory(connectionFactory);
            setDestinationName(Constants.SERVERQUEUE);
        }};
    }

    @Bean
    JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }
    
    
	public static void main(String[] args) throws Throwable {
		BrokerService broker = new BrokerService();
		broker.addConnector(Constants.ACTIVEMQ_URL);
		broker.setPersistent(false);
		broker.start();
		
		System.out.println("Server ready.");
		
		AnnotationConfigApplicationContext context = 
		          new AnnotationConfigApplicationContext(ChatServerApplication.class);
		
//		MessageCreator messageCreator = new MessageCreator() {
//			public Message createMessage(Session session) throws JMSException {
//				return session.createTextMessage("ping!");
//			}
//		};
//		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
//		System.out.println("Sending a new message:");
//		jmsTemplate.send(Constants.SERVERQUEUE, messageCreator);
		
        //context.close();
	}

}

package edu.ucsd.cse110.server;

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

@Configuration
@ComponentScan
public class ChatServerApplication {
	
    @Bean
    ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(
                new ActiveMQConnectionFactory(Constants.ACTIVEMQ_URL));
    }
    
    @Bean
    MessageListenerAdapter receiver() {
        return new MessageListenerAdapter(new Server()) {{
            setDefaultListenerMethod("receive");
            // allow sending of Message objects instead of the default strings
            setMessageConverter(null);
        }};
    }
    
    @Bean
    SimpleMessageListenerContainer container(
    		final MessageListenerAdapter messageListener,
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
		

		AnnotationConfigApplicationContext context = 
		          new AnnotationConfigApplicationContext(
		        		  ChatServerApplication.class);
		
		System.out.println("Server ready.");
		
		//context.close();
	}

}

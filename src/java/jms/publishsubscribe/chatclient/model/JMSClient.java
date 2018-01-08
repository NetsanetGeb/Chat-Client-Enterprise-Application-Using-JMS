package jms.publishsubscribe.chatclient.model;

import jms.publishsubscribe.chatclient.view.ClientView;
import java.util.Date;
import java.util.StringJoiner;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class JMSClient implements ExceptionListener {
    
    private ClientView clientView;
    private TopicConnection topicConnection = null;
    private TopicPublisher topicPublisher = null;
    private TopicSession topicSession = null;
    private TopicSubscriber topicSubscriber = null;
    private Listener listener = null;
    
    
    public JMSClient() {
    }
    
    public void openConnection(ClientView clientView, String username) throws JMSException, NamingException {
        this.clientView = clientView;
        this.listener = new Listener(this);
        
        Context jndi = new InitialContext();
         TopicConnectionFactory tcf = (TopicConnectionFactory) jndi.lookup("JMSChatConnectionFactory");
        topicConnection = tcf.createTopicConnection();
        topicConnection.setClientID(username);
        topicSession = topicConnection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
        Topic topic = (Topic) jndi.lookup("JMSChatTopic");
        topicPublisher = topicSession.createPublisher(topic);
        topicSubscriber = topicSession.createDurableSubscriber(topic, username);
        topicSubscriber.setMessageListener(listener);
        topicConnection.setExceptionListener(this);
        topicConnection.start();
        
        sendMessageToTopic("MESSAGE SERVER >> ", username + " Joined chat room.");
    }
    
    public void closeConnection(String username) throws JMSException {
        sendMessageToTopic("MESSAGE SERVER >> ", username + " leaving the chat room.");
        topicSubscriber.close();
        topicConnection.close();
    }
    
    public void closeAndUnsubscribeConnection(String username) throws JMSException {
        sendMessageToTopic("MESSAGE SERVER", username + " Unsubscribed and leaft the room.");
        topicSubscriber.close();
        topicSession.unsubscribe(username);
        topicConnection.close();
    }

    public void sendMessageToTopic(String username, String message) throws JMSException {
        TextMessage tx = topicSession.createTextMessage();
        tx.setText(username + " >> " + message);
        topicPublisher.send(tx);
    }
    
    public void sendMessageToView(String message) {
        clientView.updateChatContent(message);
    }

    @Override
    public void onException(JMSException e) {
        StringJoiner errorMessage = new StringJoiner("\n");
        errorMessage.add(new Date() + "Exception received");
        errorMessage.add("** Error Code: " + e.getErrorCode());
        errorMessage.add("** Error Message: " + e.getMessage());
        System.out.println(errorMessage.toString());
    }

}

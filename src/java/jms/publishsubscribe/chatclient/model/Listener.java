package jms.publishsubscribe.chatclient.model;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class Listener implements MessageListener {
    
    private JMSClient jmsClient = null;

    public Listener(JMSClient jmsClient) {
        this.jmsClient = jmsClient;
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage tm = (TextMessage) message;    
            jmsClient.sendMessageToView(tm.getText());
        } catch (JMSException jmse) {
            System.err.println(" Failed to receive message: " + jmse);
        }
    }
    
}

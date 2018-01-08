
package jms.publishsubscribe.chatclient.startup;

import jms.publishsubscribe.chatclient.view.ClientView;
import jms.publishsubscribe.chatclient.model.JMSClient;
import java.awt.EventQueue;

public class Main {

    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(() -> {
            new ClientView(new JMSClient()).setVisible(true);
        });
        
    }
    
}

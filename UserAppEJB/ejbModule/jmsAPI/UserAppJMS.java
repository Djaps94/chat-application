package jmsAPI;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import beans.UserMessagesInterface;

/**
 * Message-Driven Bean implementation class for: UserAppJMS
 */
@MessageDriven(
		activationConfig = { 
		        @ActivationConfigProperty(propertyName  = "destinationType", 
		                                  propertyValue = "javax.jms.Queue"),
		        @ActivationConfigProperty(propertyName  = "destination",
		                                  propertyValue = "java:/jms/queue/userQueue")
		})
public class UserAppJMS implements MessageListener {
    
    @EJB
    private UserMessagesInterface messageBean;
    
    public UserAppJMS() { }
	
    public void onMessage(Message message) {
        
        if(!(message instanceof ObjectMessage))
            return;
        
        try { 
            UserJMSMessage m = (UserJMSMessage) ((ObjectMessage) message).getObject(); 
            switch(m.getMessageType()){
            case      LOGIN: messageBean.loginMessage(m.getUsername(), m.getPassword()); break;
            case     LOGOUT: messageBean.logoutMessage(m);       break;
            case   REGISTER: messageBean.registerMessage(m);     break;
            case REGISTERED: messageBean.getRegisteredMessage(); break;
            case     ACTIVE: messageBean.getActiveMessage();     break;
            default: break;
            }
        } 
        catch (JMSException e) { System.out.println("Error"); } 
    }
}

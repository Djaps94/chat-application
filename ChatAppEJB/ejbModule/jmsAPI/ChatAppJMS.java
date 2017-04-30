package jmsAPI;

import java.util.ArrayList;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.StreamMessage;

import beans.HostManagmentLocal;
import model.User;

/**
 * Message-Driven Bean implementation class for: ChatAppJMS
 */
@MessageDriven(
		activationConfig = { 
		        @ActivationConfigProperty(propertyName  = "destinationType", 
		                                  propertyValue = "javax.jms.Queue"),
		        @ActivationConfigProperty(propertyName  = "destination",
		                                  propertyValue = "java:/jms/queue/chatQueue")
		})
public class ChatAppJMS implements MessageListener {
    
    @EJB
    private HostManagmentLocal hostBean;

    public ChatAppJMS() {
    }
	
    public void onMessage(Message message) {
        try{
            if(message instanceof StreamMessage){
                if(message.propertyExists("registerError")){
                    // TODO: Send response through ws
                    Boolean registered = message.getBooleanProperty("registerError");
                }else if(message.propertyExists("logout")){
                    // TODO: Send response through ws
                    Boolean logout = message.getBooleanProperty("logout");
                }
            }
            else if(message instanceof MapMessage){
                if(message.propertyExists("registerAnswer")){
                    User user = (User) message.getObjectProperty("registerAnswer");
                    if(user != null)
                        hostBean.getCurrentHost().getRegisteredUsers().add(user);
                    
                }                
                else if(message.propertyExists("login")){
                    // TODO: Send response through ws
                    User user = (User) message.getObjectProperty("login");
                }else if(message.propertyExists("registered")){
                    ArrayList<User> registered = (ArrayList<User>) message.getObjectProperty("registered");
                }else if(message.propertyExists("active")){
                    ArrayList<User> registered = (ArrayList<User>) message.getObjectProperty("active");                    
                }
            }
        }
        catch(JMSException e) { }
    }

}

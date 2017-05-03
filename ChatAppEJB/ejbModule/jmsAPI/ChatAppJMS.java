package jmsAPI;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import beans.HostManagmentLocal;
import beans.ResponseSocketMessageLocal;
import model.User;
import restClient.NodeRestClientLocal;

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
    
    @EJB
    private NodeRestClientLocal nodeRequester;
    
    @EJB
    private ResponseSocketMessageLocal socketSender;
    
    public ChatAppJMS() {
    }
	
    public void onMessage(Message message) {
        try{
            if(message instanceof MapMessage){
                if(message.propertyExists("registerAnswer")){
                    User user = (User) message.getObjectProperty("registerAnswer");
                    if(user != null){
                        hostBean.getCurrentHost().getRegisteredUsers().add(user);
                        hostBean.getAllHosts().stream().filter(h -> !(h.getAdress().equals(hostBean.getOwnerAddress())))
                                                       .forEach(h -> nodeRequester.registerUser(h.getAdress(), user));
                        
                        socketSender.registerMessage(user, SocketMessage.type.REGISTER);
                        
                        //TODO: Respond through ws; Dont forget respond for null
                    }
                }                
                else if(message.propertyExists("login")){
                    User user = (User) message.getObjectProperty("login");
                    if(user != null){
                        hostBean.getCurrentHost().getActiveUsers().add(user);
                        hostBean.getAllHosts().stream().filter(h -> !(h.getAdress().equals(hostBean.getOwnerAddress())))
                                                       .forEach(h -> nodeRequester.addUser(h.getAdress(), user));
                        
                        socketSender.loginMessage(user, SocketMessage.type.LOGIN);
                        //TODO: Respond through ws; Dont forget respond for null
                    }
                }else if(message.propertyExists("logout")){
                    User logout = (User) message.getObjectProperty("logout");
                    if(logout != null){
                        hostBean.getCurrentHost().getActiveUsers().remove(logout);
                        hostBean.getAllHosts().stream().filter(h -> !(h.getAdress().equals(hostBean.getOwnerAddress())))
                                                       .forEach(h -> nodeRequester.removeUser(h.getAdress(), logout));
                        
                        socketSender.logoutMessage(logout, SocketMessage.type.LOGOUT);
                        //TODO: Respond through ws
                    }
                }
            }
        }
        catch(JMSException e) { }
    }

}

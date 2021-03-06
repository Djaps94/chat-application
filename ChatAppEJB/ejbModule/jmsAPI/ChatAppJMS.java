package jmsAPI;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import beans.ChatNotificationLocal;
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
    
    @EJB
    private ChatNotificationLocal chatSender;
    
    public ChatAppJMS() {
    }
	
    public void onMessage(Message message) {
        try{
            if(message instanceof ObjectMessage){
                if(message.propertyExists("registerAnswer")){
                    User user = (User) ((ObjectMessage) message).getObject();
                    String sessionId = (String) message.getObjectProperty("registerAnswer");
                    if(!user.getRegistered()){
                        hostBean.getCurrentHost().getRegisteredUsers().add(user);
                        UserJMSMessage msg = new UserJMSMessage(user, UserJMSMessage.types.REGISTER);
                        msg.setSessionId(sessionId);
                        hostBean.getAllHosts().stream().filter(h -> !(h.getAdress().equals(hostBean.getOwnerAddress())))
                                                       .forEach(h -> nodeRequester.registerUser(h.getAdress(), msg));
                        
                        socketSender.registerMessage(user, SocketMessage.type.REGISTER, sessionId);
                        
                    }else
                        socketSender.registerMessage(user, SocketMessage.type.USERNAME_EXISTS, sessionId);
                }                
                else if(message.propertyExists("login")){
                    User user = (User) ((ObjectMessage) message).getObject();
                    String sessionId = (String) message.getObjectProperty("login");
                    if(!user.getLogged() && !user.getNotregistered()){
                        UserJMSMessage msg = new UserJMSMessage(user, UserJMSMessage.types.REGISTER);
                        msg.setSessionId(sessionId);
                        hostBean.getCurrentHost().getActiveUsers().add(user);
                        hostBean.getAllHosts().stream().filter(h -> !(h.getAdress().equals(hostBean.getOwnerAddress())))
                                                       .forEach(h -> nodeRequester.addUser(h.getAdress(), msg));
                        
                        socketSender.loginMessage(user, SocketMessage.type.LOGIN, sessionId);
                        chatSender.sendNotification();
                        
                    }else if(user.getLogged())
                        socketSender.loginMessage(user, SocketMessage.type.ALREADY_LOGED, sessionId);
                    else if(user.getNotregistered())
                        socketSender.loginMessage(user, SocketMessage.type.NOT_REGISTERED, sessionId);
                }else if(message.propertyExists("logout")){
                    User logout      = (User) ((ObjectMessage) message).getObject();
                    String sessionId = (String) message.getObjectProperty("logout");
                    if(logout.getLogout()){
                        UserJMSMessage msg = new UserJMSMessage(logout, UserJMSMessage.types.REGISTER);
                        msg.setSessionId(sessionId);
                        hostBean.getCurrentHost().getActiveUsers().removeIf(element -> element.getUsername().equals(logout.getUsername()));
                        hostBean.getAllHosts().stream().filter(h -> !(h.getAdress().equals(hostBean.getOwnerAddress())))
                                                       .forEach(h -> nodeRequester.removeUser(h.getAdress(), msg));
                        
                        socketSender.logoutMessage(logout, SocketMessage.type.LOGOUT, sessionId);
                        chatSender.sendNotification();
                      
                    }else
                        socketSender.logoutMessage(logout, SocketMessage.type.NOT_LOGOUT, sessionId);
                }
            }
        }
        catch(JMSException e) {e.printStackTrace(); }
    }

}

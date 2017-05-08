package sockets;

import java.io.IOException;
import java.util.Optional;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.ChatMessagesLocal;
import beans.HostManagmentLocal;
import beans.UserSocketSessionLocal;
import jmsAPI.SocketMessage;
import model.User;
import restClient.UserRestClientLocal;
import util.NodesHandlerLocal;

@ServerEndpoint("/webchat")
@MessageDriven(
        activationConfig = { 
                @ActivationConfigProperty(propertyName  = "destinationType", 
                                          propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName  = "destination",
                                          propertyValue = "java:/jms/queue/socketQueue")
        })
public class SocketEndPoint implements MessageListener{
    
    @EJB
    private UserSocketSessionLocal userSession;
    
    @EJB
    private ChatMessagesLocal chatMessages;
    
    @EJB
    private NodesHandlerLocal nodeHandler;
    
    @EJB
    private UserRestClientLocal userRequester;
    
    @EJB
    private HostManagmentLocal hostBean;
    
    
    
    @OnOpen
    public void onOpen(Session session){
        storeSession(session.getId(), session);
    }
    
    @OnClose
    public void onClose(Session session){
        userSession.removeUserSession(session);
    }
    
    @OnMessage
    public void onMessage(Session session, String socketMessage){
        if(session.isOpen()){
            ObjectMapper mapper = new ObjectMapper();
            try {
                SocketMessage message = mapper.readValue(socketMessage, SocketMessage.class);
                switch(message.getMessageType()){
                case    LOGIN: loginUser(message.getUsername(), message.getPassword(), session); break;
                case   LOGOUT: logoutUser(message.getUsername(), message.getPassword(), session);break;
                case  MESSAGE: break;
                case REGISTER: registerUser(message.getUsername(), message.getPassword(), session); break;
                default: break;
                
                }
            }
            catch (IOException e) { return; }
        }
        
    }
    
    
    private void registerUser(String username, String password, Session session){
        if(nodeHandler.isMaster())
            chatMessages.registerMessage(username, password, hostBean.getOwnerAddress(), hostBean.getOwnerAlias(), session.getId());
        else
            userRequester.registerUser(nodeHandler.getMasterAddress(), username, password, hostBean.getOwnerAddress(), hostBean.getOwnerAlias(), session.getId());
            
    }
    
    private void loginUser(String username, String password, Session session){

        if(nodeHandler.isMaster())
            chatMessages.loginMessage(username, password, session.getId());
        else
            userRequester.loginUser(nodeHandler.getMasterAddress(), username, password, session.getId());
    }
    
    private void logoutUser(String username, String password, Session session){
        
        Optional<User> opt = hostBean.getCurrentHost().getRegisteredUsers().stream().filter(h -> h.getUsername().equals(username))
                                                                        .findFirst();
        User u = null;
        if(opt.isPresent()){
            u = opt.get();
        }else{
            u = new User(username, password);
        }
        
        if(nodeHandler.isMaster())
            chatMessages.logoutMessage(u, session.getId());
        else
            userRequester.logoutUser(nodeHandler.getMasterAddress(), u, session.getId()); 
    }

    
    
    private void storeSession(String id, Session session){
        if(!userSession.isSessionActive(id))
            userSession.addUserSession(id, session);
    }
    
    /**
     *   Notify websocket end-point via JMS
     */ 
    
    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage){
            try {
                SocketMessage msg = (SocketMessage) ((ObjectMessage) message).getObject();
                Session session = findSession(msg.getSessionId());
                System.out.println("Socket username: "+msg.getUsername());
                System.out.println("Socket username: "+msg.getSessionId());
                System.out.println("Found socket: "+session);
                if(session != null){
                    System.out.println("Hello from slave "+msg.getMessageType());
                    ObjectMapper mapper = new ObjectMapper();
                    String output       = mapper.writeValueAsString(msg);
                    session.getBasicRemote().sendText(output);
                }
                
            }
            catch (JMSException | IOException e) { e.printStackTrace(); }
        }
        
    }
    
    private Session findSession(String id){
        return userSession.getSession(id);
    }
        
    
}

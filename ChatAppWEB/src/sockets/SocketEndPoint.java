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
    
    private static final String REGISTER = "register";
    private static final String LOGIN    = "login";
    private static final String LOGOUT   = "logout";

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
        storeSession(username+REGISTER, session);
        
        if(nodeHandler.isMaster())
            chatMessages.registerMessage(username, password, hostBean.getOwnerAddress(), hostBean.getOwnerAlias());
        else
            userRequester.registerUser(nodeHandler.getMasterAddress(), username, password, hostBean.getOwnerAddress(), hostBean.getOwnerAlias());
            
    }
    
    private void loginUser(String username, String password, Session session){
        storeSession(username+LOGIN, session);

        if(nodeHandler.isMaster())
            chatMessages.loginMessage(username, password);
        else
            userRequester.loginUser(nodeHandler.getMasterAddress(), username, password);
    }
    
    private void logoutUser(String username, String password, Session session){
        storeSession(username+LOGOUT, session);
        
        Optional<User> opt = hostBean.getCurrentHost().getRegisteredUsers().stream().filter(h -> h.getUsername().equals(username))
                                                                        .findFirst();
        User u = null;
        if(opt.isPresent()){
            u = opt.get();
        }else{
            u = new User(username, password);
        }
        
        if(nodeHandler.isMaster())
            chatMessages.logoutMessage(u);
        else
            userRequester.logoutUser(nodeHandler.getMasterAddress(), u); 
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
                Session session   = null;
                switch(msg.getMessageType()){
                case   ALREADY_LOGED: session = findSession(msg.getUsername()+LOGIN);    break;
                case           LOGIN: session = findSession(msg.getUsername()+LOGIN);    break;
                case          LOGOUT: session = findSession(msg.getUsername()+LOGOUT);   break;
                case         MESSAGE: session = findSession(msg.getUsername());          break;
                case  NOT_REGISTERED: session = findSession(msg.getUsername()+LOGIN);    break;
                case        REGISTER: session = findSession(msg.getUsername()+REGISTER); break;
                case USERNAME_EXISTS: session = findSession(msg.getUsername()+REGISTER); break;
                case      NOT_LOGOUT: session = findSession(msg.getUsername()+LOGOUT); break;
                }
                if(session != null){
                    ObjectMapper mapper = new ObjectMapper();
                    String output       = mapper.writeValueAsString(msg);
                    session.getBasicRemote().sendText(output);
                }
                
            }
            catch (JMSException | IOException e) { e.printStackTrace(); }
        }
        
    }
    
    private Session findSession(String username){
        return userSession.getSession(username);
    }
        
    
}

package sockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.ChatMessagesLocal;
import beans.HostManagmentLocal;
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

    
    private List<Session> sessions = new ArrayList<Session>();
    
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
        if(!sessions.contains(session))
            sessions.add(session);
    }
    
    @OnClose
    public void onClose(Session session){
        sessions.remove(session);
    }
    
    @OnMessage
    public void onMessage(Session session, String socketMessage){
        if(session.isOpen()){
            ObjectMapper mapper = new ObjectMapper();
            try {
                SocketMessage message = mapper.readValue(socketMessage, SocketMessage.class);
                switch(message.getMessageType()){
                case    LOGIN: loginUser(message.getUsername(), message.getPassword()); break;
                case   LOGOUT: logoutUser(message.getUsername(), message.getPassword());break;
                case  MESSAGE: break;
                case REGISTER: registerUser(message.getUsername(), message.getPassword()); break;
                default: break;
                
                }
            }
            catch (IOException e) { return; }
        }
        
    }
    
    
    private void registerUser(String username, String password){
        if(nodeHandler.isMaster())
            chatMessages.registerMessage(username, password, hostBean.getOwnerAddress(), hostBean.getOwnerAlias());
        else
            userRequester.registerUser(nodeHandler.getMasterAddress(), username, password, hostBean.getOwnerAddress(), hostBean.getOwnerAlias());
            
    }
    
    private void loginUser(String username, String password){
        User u = hostBean.getCurrentHost().getRegisteredUsers().stream().filter(h -> h.getUsername().equals(username))
                                                                        .findFirst()
                                                                        .get();
        if(u == null)
            //TODO: WS response 
        
        if(nodeHandler.isMaster())
            chatMessages.loginMessage(u);
        else
            userRequester.loginUser(nodeHandler.getMasterAddress(), username, password);
    }
    
    private void logoutUser(String username, String password){
        
        User u = hostBean.getCurrentHost().getRegisteredUsers().stream().filter(h -> h.getUsername().equals(username))
                                                                        .findFirst()
                                                                        .get();
        if(u == null){
            
        }
        
        if(nodeHandler.isMaster())
            chatMessages.logoutMessage(u);
        else
            userRequester.logoutUser(nodeHandler.getMasterAddress(), u); 
    }

    
    // Notify websocket end-point via JMS
    @Override
    public void onMessage(Message message) {
        // TODO Auto-generated method stub
        
    }
        
    
}

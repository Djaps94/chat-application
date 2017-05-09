package sockets;

import java.io.IOException;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.HostManagmentLocal;
import beans.UserSocketSessionLocal;
import jmsAPI.SocketMessage;
import model.User;

@ServerEndpoint("/chat")
@MessageDriven(
        activationConfig = { 
                @ActivationConfigProperty(propertyName  = "destinationType", 
                                          propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName  = "destination",
                                          propertyValue = "java:/jms/queue/socketChatQueue")
        })
public class ChatSocketEndPoint implements MessageListener{

    @EJB
    private UserSocketSessionLocal userSession;
        
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
    
    @SuppressWarnings("incomplete-switch")
    @OnMessage
    public void onMessage(Session session, String message){
        if(session.isOpen()){
            try{
                ObjectMapper mapper         = new ObjectMapper();
                SocketMessage socketMessage = (SocketMessage) mapper.readValue(message, SocketMessage.class);
                switch(socketMessage.getMessageType()){
                case  ACTIVE_USERS: sendActiveUsers(session); break;
                }
            }
            catch(Exception e) { e.printStackTrace(); }
        }
    }
    
    private void sendActiveUsers(Session session) throws IOException{
        if(session != null){
            System.out.println("Hello from Active users");
            List<User> activeUsers = hostBean.getCurrentHost().getActiveUsers();
            System.out.println(activeUsers.size());
            ObjectMapper mapper    = new ObjectMapper();
            String output          = mapper.writeValueAsString(activeUsers);
            session.getBasicRemote().sendText(output);
        }
    }
    
    private void storeSession(String id, Session session){
        if(!userSession.isSessionActive(id))
            userSession.addUserSession(id, session);
    }

    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage){
            try{
                ObjectMapper mapper    = new ObjectMapper();
                List<User> activeUsers = hostBean.getCurrentHost().getActiveUsers(); 
                String output          = mapper.writeValueAsString(activeUsers);
                List<Session> sessions = userSession.getAllSessions();
                for(Session session : sessions){
                    session.getBasicRemote().sendText(output);
                }
            }
            catch (IOException e) { e.printStackTrace(); }
        }
    }
    
}

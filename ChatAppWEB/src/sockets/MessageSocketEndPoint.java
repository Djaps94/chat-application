package sockets;

import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.HostManagmentLocal;
import beans.UserSocketSessionLocal;
import model.Message;
import restClient.UserRestClientLocal;

@ServerEndpoint("/messages/{user}")
@MessageDriven(
        activationConfig = { 
                @ActivationConfigProperty(propertyName  = "destinationType", 
                                          propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName  = "destination",
                                          propertyValue = "java:/jms/queue/socketMessageQueue")
        })
public class MessageSocketEndPoint implements MessageListener{
    
    @EJB
    private UserSocketSessionLocal userSession;
    
    @EJB
    private UserRestClientLocal userRestSender;
    
    @EJB
    private HostManagmentLocal hostBean;
    
    
    @OnOpen
    public void onOpen(@PathParam("user") String user, Session session){
        storeSession(session.getId(), session);
        storePrivate(session.getId(), user);
    }
    
    private void storePrivate(String id, String user) {
        if(!userSession.isUserActive(id))
            userSession.addPrivateMessage(id, user);
    }

    @OnClose
    public void onClose(Session session){
        userSession.removeUserSession(session);
    }
    
    @OnMessage
    public void onMessage(Session session, String message){
        if(session.isOpen()){
            try{
                ObjectMapper mapper = new ObjectMapper();
                Message msg         = mapper.readValue(message, Message.class);
                hostBean.getAllHosts().forEach(host -> userRestSender.publishMessage(host.getAdress(), msg));
            }catch(Exception e) { e.printStackTrace(); }
        }
    }
    
    
    private void storeSession(String id, Session session){
        if(!userSession.isSessionActive(id))
            userSession.addUserSession(id, session);
    }

    @Override
    public void onMessage(javax.jms.Message message) {
        if(message instanceof ObjectMessage){
            try {
                ObjectMapper mapper = new ObjectMapper();
                Message msg         = (Message) ((ObjectMessage) message).getObject();
                String output       = mapper.writeValueAsString(msg);
                if(msg.getTo() == null){
                    for(Session s : userSession.getAllSessions())   
                        s.getBasicRemote().sendText(output);
                }else{
                    String username = msg.getTo().getUsername();
          
                    for(Session s : userSession.getAllSessions()){
                        if(userSession.getPrivateMessage(s.getId()) == null)
                            continue;
                        if(userSession.getPrivateMessage(s.getId()).equals(username)){
                            s.getBasicRemote().sendText(output); 
                            break;
                        }
                    }
                        
                }
            }
            catch (JMSException | IOException e) { e.printStackTrace(); }
        }
        
    }
    

}

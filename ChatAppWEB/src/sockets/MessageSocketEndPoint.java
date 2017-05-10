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
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.HostManagmentLocal;
import beans.UserSocketSessionLocal;
import model.Message;
import restClient.UserRestClientLocal;

@ServerEndpoint("/messages")
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
    public void onOpen(Session session){
        storeSession(session.getId(), session);
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
                }
            }
            catch (JMSException | IOException e) { e.printStackTrace(); }
        }
        
    }
    

}

package sockets;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.MessageListener;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;


import beans.UserSocketSessionLocal;
import model.Message;

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
                if(msg.getTo() == null){
                    for(Session s : userSession.getAllSessions())   
                        s.getBasicRemote().sendText(message);
                }
            }catch(Exception e) { e.printStackTrace(); }
        }
    }
    
    
    private void storeSession(String id, Session session){
        if(!userSession.isSessionActive(id))
            userSession.addUserSession(id, session);
    }

    @Override
    public void onMessage(javax.jms.Message message) {
        // TODO Auto-generated method stub
        
    }
    

}

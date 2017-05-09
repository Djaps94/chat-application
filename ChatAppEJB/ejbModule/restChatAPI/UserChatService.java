package restChatAPI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import beans.ChatNotificationLocal;
import beans.HostManagmentLocal;
import beans.ResponseSocketMessageLocal;
import jmsAPI.SocketMessage;
import jmsAPI.UserJMSMessage;
import util.NodesHandlerLocal;

@Stateless
@Path("/chat")
public class UserChatService {

    @EJB
    private NodesHandlerLocal nodeHandler;
    
    @EJB
    private HostManagmentLocal hostBean;
    
    @EJB
    private ResponseSocketMessageLocal socketSender;
    
    @EJB
    private ChatNotificationLocal chatSender;
    
    
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerUser(UserJMSMessage message){
        if(!nodeHandler.isMaster()){
            hostBean.getCurrentHost().getRegisteredUsers().add(message.getU());
            
            socketSender.registerMessage(message.getU(), SocketMessage.type.REGISTER, message.getSessionId());
        }
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addUser(UserJMSMessage message){
        if(!nodeHandler.isMaster()){
            hostBean.getCurrentHost().getActiveUsers().add(message.getU());
            if(hostBean.getCurrentHost().getAdress().equals(message.getU().getHost().getAdress()))
                socketSender.loginMessage(message.getU(), SocketMessage.type.LOGIN, message.getSessionId());
            chatSender.sendNotification();
        }
    }
    
    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeUser(UserJMSMessage message){
        if(!nodeHandler.isMaster()){
            hostBean.getCurrentHost().getActiveUsers().remove(message.getU());
            
            chatSender.sendNotification();
        }
    }
    
}

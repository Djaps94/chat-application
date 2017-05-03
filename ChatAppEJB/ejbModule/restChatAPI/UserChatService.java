package restChatAPI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import beans.HostManagmentLocal;
import beans.ResponseSocketMessageLocal;
import jmsAPI.SocketMessage;
import model.User;
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
    
    
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerUser(User user){
        if(!nodeHandler.isMaster()){
            hostBean.getCurrentHost().getRegisteredUsers().add(user);
        
            socketSender.registerMessage(user, SocketMessage.type.REGISTER);
        }
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addUser(User user){
        if(!nodeHandler.isMaster()){
            hostBean.getCurrentHost().getActiveUsers().add(user);
            
            socketSender.loginMessage(user, SocketMessage.type.LOGIN);
        }
    }
    
    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeUser(User user){
        if(!nodeHandler.isMaster()){
            hostBean.getCurrentHost().getActiveUsers().remove(user);
            
            socketSender.logoutMessage(user, SocketMessage.type.LOGOUT);
        }
    }
    
}

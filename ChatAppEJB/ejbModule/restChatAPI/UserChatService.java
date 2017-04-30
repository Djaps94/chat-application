package restChatAPI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import beans.HostManagmentLocal;
import model.User;
import util.NodesHandlerLocal;

@Stateless
@Path("/chat")
public class UserChatService {

    @EJB
    private NodesHandlerLocal nodeHandler;
    
    @EJB
    private HostManagmentLocal hostBean;
    
    
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public void registerUser(User user){
        if(nodeHandler.isMaster())
            hostBean.getCurrentHost().getRegisteredUsers().add(user);
    
        //TODO: Respond through ws
        
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addUser(User user){
        if(nodeHandler.isMaster())
            hostBean.getCurrentHost().getActiveUsers().add(user);
    
        //TODO: Respond through ws
        
    }
    
    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeUser(User user){
        if(nodeHandler.isMaster())
            hostBean.getCurrentHost().getActiveUsers().remove(user);
    
        //TODO: Respond through ws
        
    }
    
}

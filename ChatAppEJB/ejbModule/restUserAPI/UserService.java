package restUserAPI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.ChatMessagesLocal;
import beans.HostManagmentLocal;
import model.User;
import restClient.UserRestClientLocal;
import util.NodesHandlerLocal;

@Stateless
@Path("user")
public class UserService {
    
    @EJB
    private ChatMessagesLocal chatMessages;
    
    @EJB
    private NodesHandlerLocal nodeHandler;
    
    @EJB
    private UserRestClientLocal userRequester;
    
    @EJB
    private HostManagmentLocal hostBean;
    
    @POST
    @Path("/register")
    public void registerUser(@FormParam("username") String username, @FormParam("password") String password){
        if(nodeHandler.isMaster())
            chatMessages.registerMessage(username, password, hostBean.getOwnerAddress(), hostBean.getOwnerAlias());
        else
            userRequester.registerUser(nodeHandler.getMasterAddress(), username, password, hostBean.getOwnerAddress(), hostBean.getOwnerAlias());
            
    }
    
    @POST
    @Path("/login")
    public void loginUser(@FormParam("username") String username, @FormParam("password") String password){
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
    
    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    public void logoutUser(User user){
        if(nodeHandler.isMaster())
            chatMessages.logoutMessage(user);
        else
            userRequester.logoutUser(nodeHandler.getMasterAddress(), user); 
    }

}

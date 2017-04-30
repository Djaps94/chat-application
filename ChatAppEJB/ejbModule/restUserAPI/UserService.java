package restUserAPI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.ChatMessagesLocal;
import beans.HostManagmentLocal;
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
    
    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_PLAIN)
    public void registerUser(@FormParam("username") String username, @FormParam("password") String password){
        if(nodeHandler.isMaster())
            chatMessages.registerMessage(username, password, "address", "alias");
        else
            userRequester.registerUser(nodeHandler.getMasterAddress(), username, password, hostBean.getOwnerAddress(), hostBean.getOwnerAlias());
            
    }

}

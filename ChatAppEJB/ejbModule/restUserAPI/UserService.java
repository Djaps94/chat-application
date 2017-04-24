package restUserAPI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.ChatMessagesLocal;

@Stateless
@Path("user")
public class UserService {
    
    @EJB
    private ChatMessagesLocal chatMessages;
    
    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_PLAIN)
    public String registerUser(@FormParam("username") String username, @FormParam("password") String password){
        chatMessages.registerMessage(username, password, "address", "alias");
        return "Success!";
    }

}

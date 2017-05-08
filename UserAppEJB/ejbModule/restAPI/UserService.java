package restAPI;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.UserManagmentLocal;
import beans.UserMessagesLocal;
import jmsAPI.UserJMSMessage;
import model.Host;
import model.User;

@Stateless
@Path("/user")
public class UserService{

	@EJB
	private UserManagmentLocal userBean;
	
	@EJB
	private UserMessagesLocal userMessages;
	
	@POST
	@Path("/register")
	public void register(@FormParam("username") String username, @FormParam("password") String password,
	                     @FormParam("address") String address, @FormParam("alias") String alias,
	                     @FormParam("session") String sessionId){
		
		User u = new User(username, password, new Host(address, alias));
		UserJMSMessage message = new UserJMSMessage(u, UserJMSMessage.types.REGISTER);
		message.setSessionId(sessionId);
		userMessages.registerMessage(message);
	}
	
	@POST
	@Path("/login")
	public void login(@FormParam("username") String username, @FormParam("password") String password,
	                  @FormParam("session") String sessionId){
	    
	    UserJMSMessage message = new UserJMSMessage();
	    message.setUsername(username);
	    message.setPassword(password);
	    message.setSessionId(sessionId);
	    message.setMessageType(UserJMSMessage.types.LOGIN);
	    userMessages.loginMessage(message);	    
	}
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public void logout(User user){
	    userMessages.logoutMessage(new UserJMSMessage(user, UserJMSMessage.types.LOGOUT));
	}
	
	@GET
	@Path("/allActive")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getAllActiveUsers(){
	    return userBean.getAllActiveUsers();
	}
	
	@GET
	@Path("/allRegistered")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getAllRegisteredUsers(){
	    return userBean.getAllRegisteredUsers();
	}

}

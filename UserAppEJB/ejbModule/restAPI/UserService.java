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
import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
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
	                     @FormParam("address") String address, @FormParam("alias") String alias){
		
		User u = new User(username, password, new Host(address, alias)); 
        userMessages.registerMessage(new UserJMSMessage(u, UserJMSMessage.types.REGISTER));
	}
	
	@POST
	@Path("/login")
	public void login(@FormParam("username") String username, @FormParam("password") String password){
	    
	    userMessages.loginMessage(username, password);	    
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

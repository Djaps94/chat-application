package restAPI;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.UserManagmentLocal;
import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import model.User;

@Stateless
@Path("/user")
public class UserService{
    
    //TODO: Change methods to POST. 
    //      Currently GET is only for testing.
	
	@EJB
	private UserManagmentLocal userBean;
	
	@GET
	@Path("/register")
	public Boolean register(@FormParam("username") String username, @FormParam("password") String password,
	                       @FormParam("address") String address, @FormParam("alias") String alias){
		Boolean notRegistered = false;
		try { notRegistered = userBean.register(username, password, address, alias); } 
		catch (UsernameExistsException e) { return false; }
		
		if(notRegistered) return true;
		else              return false;
	}
	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public User login(@FormParam("username") String username, @FormParam("password") String password){
       try { return userBean.login(username, password); }
       catch (InvalidCredentialsException e) { return null; }	    
	}
	
	@GET
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Boolean logout(User user){
       return userBean.logout(user);
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

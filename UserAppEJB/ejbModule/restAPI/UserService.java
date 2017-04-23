package restAPI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.UserManagmentLocal;
import exceptions.UsernameExistsException;

@Stateless
@Path("/user")
public class UserService{
	
	@EJB
	private UserManagmentLocal userBean;
	
	@GET
	@Path("/register")
	@Produces(MediaType.TEXT_PLAIN)
	public String register(){
		Boolean flag = false;
		try {
			flag = userBean.register("", "", "", "");
		} catch (UsernameExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(flag)
			return "Uspeeh";
		else
			return "Neuspeh";
	}

}

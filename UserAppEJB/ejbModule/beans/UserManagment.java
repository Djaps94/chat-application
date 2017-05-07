package beans;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Host;
import model.User;


@Startup
@Singleton
@Local(UserManagmentLocal.class)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class UserManagment implements UserManagmentLocal{
	
	private static final String REGISTER_PATH = "registeredUsers.txt";
	private static final String ACTIVE_PATH   = "activeUsers.txt";
	
	private List<User> activeUsers;
	private List<User> registeredUsers;
	
	@PostConstruct
	private void initialise(){
		activeUsers     = loadUsers(ACTIVE_PATH);
		registeredUsers = loadUsers(REGISTER_PATH);
		
	}

	@Lock(LockType.WRITE)
	@Override
	public User register(String username, String password, String address, String alias){
		if(!checkParams(username, password, address, alias))
			return null;
		
		User user = new User(username, password, new Host(address, alias));
		
		if(registeredUsers.stream().anyMatch(e -> e.getUsername().equals(username))){
			user.setRegistered(true);
		    return user;
		}
		
		registeredUsers.add(user);
		try {
		    saveUser(registeredUsers, REGISTER_PATH);
		}catch (URISyntaxException | IOException e) {
		    return null;
		}
		
		return user;
	}
	
	@Lock(LockType.WRITE)
	@Override
	public User login(String username, String password) {
		if(!checkParams(username, password))
			return null;
		
		if(activeUsers.stream().anyMatch(e -> e.getUsername().equals(username))){
		    User u = activeUsers.stream().filter(e -> e.getUsername().equals(username))
                                         .findFirst()
                                         .get();
		    u.setLogged(true);
		    return u;
		}
		
		if(registeredUsers.stream().anyMatch(e -> e.getUsername().equals(username))){
			User u = registeredUsers.stream().filter(e -> e.getUsername().equals(username))
			                                 .findFirst()
			                                 .get();
			activeUsers.add(u);
			try { saveUser(activeUsers, ACTIVE_PATH); } 
			catch (URISyntaxException | IOException e1) { }
			return u;
		}
							
		
		return null;
	}

	@Lock(LockType.WRITE)
	@Override
	public User logout(User logout) {
		if(!checkParams(logout.getUsername(), logout.getPassword()))
			return null;
		
		if(activeUsers.stream().anyMatch(e -> e.equals(logout))){
			activeUsers.remove(logout);
			
			try { saveUser(activeUsers, ACTIVE_PATH); }
            catch (URISyntaxException | IOException e1) { e1.printStackTrace(); }
         
			return logout;
		}
		
		return null;
	}

	@Lock(LockType.READ)
	@Override
	public List<User> getAllRegisteredUsers() {
		return registeredUsers;
	}
	
	@Lock(LockType.READ)
	@Override
	public List<User> getAllActiveUsers(){
		return activeUsers;
	}
	
	private Boolean checkParams(String... args){
		for(String arg : args){
			if(arg == "" || arg.equals(null))
				return false;
		}
		
		return true;
	}
	
	private void saveUser(List<User> user, String destination) throws URISyntaxException, IOException{
		URL u               = this.getClass().getClassLoader().getResource(destination);
		ObjectMapper mapper = new ObjectMapper();
		String jsonUser     = mapper.writeValueAsString(user);
		if(u.getPath() != ""){
			PrintWriter writer = new PrintWriter(new File(u.getPath()));
			writer.write("");
			writer.write(jsonUser);
			writer.flush();
			writer.close();
		}
	}
	
	private List<User> loadUsers(String destination){
		URL u 	            = this.getClass().getClassLoader().getResource(destination);
		ObjectMapper mapper = new ObjectMapper();
		JsonParser parser   = null;
		
		try { parser = new JsonFactory().createParser(new File(u.getPath())); } 
		catch (IOException e) { return new ArrayList<>(); }

		if(u.getPath() != ""){
			List<User> list;
			
			try { list = new ArrayList<User>(Arrays.asList(mapper.readValue(parser, User[].class))); } 
			catch (IOException e) { return new ArrayList<>(); }
			
			return list;
		}
		return new ArrayList<>();
	}
}

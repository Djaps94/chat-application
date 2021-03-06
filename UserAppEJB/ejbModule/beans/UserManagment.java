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
	
	private List<User> activeUsers;
	private List<User> registeredUsers;
	
	@PostConstruct
	private void initialise(){
		registeredUsers = loadUsers(REGISTER_PATH);
		activeUsers     = new ArrayList<>();
		
	}

	@Lock(LockType.WRITE)
	@Override
	public User register(String username, String password, String address, String alias){
		if(!checkParams(username, password, address, alias)){
		    User user = new User();
		    user.setRegistered(true);
		    return user;
		}
			
		
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
	public User login(String username, String password, String hostAddress) {
		if(!checkParams(username, password))
			return null;
		
		if(!activeUsers.isEmpty()){
    		if(activeUsers.stream().anyMatch(e -> e.getUsername().equals(username))){
    		    User u = activeUsers.stream().filter(e -> e.getUsername().equals(username))
                                             .findFirst()
                                             .get();
    		    u.setLogged(true);
    		    return u;
		    }
		}
		
		if(!registeredUsers.isEmpty()){
    		if(!registeredUsers.stream().anyMatch(e -> e.getUsername().equals(username))){
    		    User u = new User();
    		    u.setUsername(username);
    		    u.setPassword(password);
    		    u.setNotregistered(true);
    		    return u;
    		}
		}
		
		if(!registeredUsers.isEmpty()){
    		if(registeredUsers.stream().anyMatch(e -> e.getUsername().equals(username))){
    			User u = registeredUsers.stream().filter(e -> e.getUsername().equals(username))
    			                                 .findFirst()
    			                                 .get();
    			u.getHost().setAdress(hostAddress);
    			activeUsers.add(u);
    			return u;
    		}					
		}
		User u = new User(username, password);
		u.setNotregistered(true);
		return u;
	}

	@Lock(LockType.WRITE)
	@Override
	public User logout(User logout) {
		if(!checkParams(logout.getUsername(), logout.getPassword()))
			return null;
		
		if(activeUsers.stream().anyMatch(e -> e.getUsername().equals(logout.getUsername()))){
		    activeUsers.removeIf(element -> element.getUsername().equals(logout.getUsername()));
		    
			logout.setLogout(true);
			return logout;
		}
		
		return logout;
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
			System.out.println(arg);
		    if(arg == "" || arg == null)
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

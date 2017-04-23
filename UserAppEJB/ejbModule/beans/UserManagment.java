package beans;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

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
		activeUsers     = new ArrayList<>();
		registeredUsers = new ArrayList<>();
	}

	@Lock(LockType.WRITE)
	@Override
	public Boolean register(String username, String password, String address, String alias){
		if(!checkParams(username, password, address, alias))
			return false;
		
		User user = new User(username, password, new Host(address, alias));
		
		if(registeredUsers.contains(user))
			return false;
		
		registeredUsers.add(user);
		try {
			saveUser(user, REGISTER_PATH);
		} catch (URISyntaxException | IOException e) {
			return false;
		}
		
		return true;
	}
	
	@Lock(LockType.WRITE)
	@Override
	public User login(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Lock(LockType.WRITE)
	@Override
	public Boolean logout(User logout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Lock(LockType.READ)
	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Boolean checkParams(String username, String password, String address, String alias){
		if(username == "" || username.equals(null) || password == "" || password.equals(null) || address == "" || address.equals(null))
			return false;
		
		return true;
	}
	
	private void saveUser(User user, String destination) throws URISyntaxException, IOException{
		URL u               = this.getClass().getClassLoader().getResource(destination);
		ObjectMapper mapper = new ObjectMapper();
		String jsonUser     = mapper.writeValueAsString(user);
		if(u.getPath() != ""){
			FileWriter writer = new FileWriter(new File(u.getPath()), true);
			writer.write(jsonUser);
			writer.close();
		}
	}
	
//	private void loadUsers(String destination){
//		URL u 				= this.getClass().getClassLoader().getResource(destination);
//		ObjectMapper mapper = new ObjectMapper();
//	}
	
	
}

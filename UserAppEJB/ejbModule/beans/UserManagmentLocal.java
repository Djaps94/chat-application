package beans;

import java.util.List;

import javax.ejb.Local;

import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import model.User;

@Local
public interface UserManagmentLocal {
	
	public User register(String username, String password, String address, String alias) throws UsernameExistsException;
	public User login(String username, String password, String hostAddress) throws InvalidCredentialsException;
	public User logout(User logout);
	public List<User> getAllActiveUsers();
	public List<User> getAllRegisteredUsers();
	
}

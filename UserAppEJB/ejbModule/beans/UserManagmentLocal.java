package beans;

import java.util.List;

import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import model.User;

public interface UserManagmentLocal {
	
	public User register(String username, String password) throws UsernameExistsException;
	public Boolean login(String username, String password) throws InvalidCredentialsException;
	public Boolean logout(User logout);
	public List<User> getAllUsers();
	
}

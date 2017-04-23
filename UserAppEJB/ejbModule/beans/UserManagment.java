package beans;

import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Stateless;

import model.User;

@Stateless
@Local(UserManagmentLocal.class)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class UserManagment implements UserManagmentLocal{

	@Override
	public User register(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean login(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean logout(User logout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}

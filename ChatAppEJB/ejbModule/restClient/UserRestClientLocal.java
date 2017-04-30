package restClient;

import java.util.List;

import javax.ejb.Local;

import model.User;

@Local
public interface UserRestClientLocal {

    public List<User> getRegisteredUsers(String masterAddress);
}

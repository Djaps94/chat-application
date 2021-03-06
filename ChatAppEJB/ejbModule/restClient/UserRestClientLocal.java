package restClient;

import java.util.List;

import javax.ejb.Local;

import model.Message;
import model.User;

@Local
public interface UserRestClientLocal {

    public List<User> getRegisteredUsers(String masterAddress);
    public void registerUser(String masterAddress, String username, String password, String address, String alias, String sessionId);
    public void loginUser(String masterAddress, String username, String password, String sessionId, String hostAddress);
    public void logoutUser(String masterAddress, User user, String sessionId);
    public void publishMessage(String address, Message message);
}

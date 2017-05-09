package beans;

import javax.ejb.Local;

import model.User;

@Local
public interface ChatMessagesLocal {

    public void registerMessage(String username, String password, String addres, String alias, String sessionId);
    public void loginMessage(String username, String password, String sessionId, String hostAddress);
    public void logoutMessage(User user, String sessionId);
    public void getRegisteredUsers();
    public void getActiveUsers();
}

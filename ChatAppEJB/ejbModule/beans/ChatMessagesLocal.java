package beans;

import javax.ejb.Local;

import model.User;

@Local
public interface ChatMessagesLocal {

    public void registerMessage(String username, String password, String addres, String alias);
    public void loginMessage(String username, String password);
    public void logoutMessage(User user);
    public void getRegisteredUsers();
    public void getActiveUsers();
}

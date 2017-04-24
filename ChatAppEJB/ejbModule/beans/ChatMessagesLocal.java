package beans;

import javax.ejb.Local;

@Local
public interface ChatMessagesLocal {

    public void registerMessage(String username, String password, String addres, String alias);
    public void loginMessage();
    public void logoutMessage();
    public void getRegisteredUsers();
    public void getActiveUsers();
}

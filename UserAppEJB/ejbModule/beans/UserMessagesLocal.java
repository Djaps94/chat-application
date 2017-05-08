package beans;

import javax.ejb.Local;

import jmsAPI.UserJMSMessage;

@Local
public interface UserMessagesLocal {
    
    public void registerMessage(UserJMSMessage message);
    public void loginMessage(UserJMSMessage message);
    public void logoutMessage(UserJMSMessage message);
    public void getRegisteredMessage();
    public void getActiveMessage();

}

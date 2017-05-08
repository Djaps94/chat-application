package beans;

import javax.ejb.Local;

import jmsAPI.SocketMessage;
import model.User;

@Local
public interface ResponseSocketMessageLocal {
    
    public void registerMessage(User user, SocketMessage.type messageType, String sessionId);
    public void loginMessage(User user, SocketMessage.type messageType, String sessionId);
    public void logoutMessage(User user, SocketMessage.type messageType, String sessionId);

}

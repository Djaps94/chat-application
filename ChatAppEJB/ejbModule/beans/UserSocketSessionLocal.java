package beans;

import javax.ejb.Local;
import javax.websocket.Session;

@Local
public interface UserSocketSessionLocal {

    public void addUserSession(String username, Session value);
    public void removeUserSession(String username);
    public void removeUserSession(Session value);
    public boolean isSessionActive(String username);
    
}

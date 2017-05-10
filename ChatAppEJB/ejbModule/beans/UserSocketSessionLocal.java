package beans;

import java.util.List;

import javax.ejb.Local;
import javax.websocket.Session;

@Local
public interface UserSocketSessionLocal {

    public void addUserSession(String username, Session value);
    public void removeUserSession(String username);
    public void removeUserSession(Session value);
    public boolean isSessionActive(String username);
    public Session getSession(String key);
    public List<Session> getAllSessions();
    public boolean isUserActive(String sessionId);
    public void addPrivateMessage(String sessionId, String username);
    public void removePrivateMessage(String sessionId);
    public String getPrivateMessage(String sessionId);
    
}

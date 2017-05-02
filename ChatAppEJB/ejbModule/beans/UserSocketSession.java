package beans;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.websocket.Session;

@Singleton
@Local(UserSocketSessionLocal.class)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class UserSocketSession implements UserSocketSessionLocal{

    private Map<String, Session> sessionMap;
    
    @PostConstruct
    public void initialise(){
        sessionMap = new HashMap<String, Session>();
    }

    @Override
    @Lock(LockType.WRITE)
    public void addUserSession(String username, Session value) {
        if(!isSessionActive(username))
            sessionMap.put(username, value);
        
    }

    @Override
    @Lock(LockType.WRITE)
    public void removeUserSession(String username) {
        if(!isSessionActive(username))
            sessionMap.remove(username);
        
    }

    @Override
    @Lock(LockType.READ)
    public boolean isSessionActive(String username) {
        return sessionMap.containsKey(username);
    }
}

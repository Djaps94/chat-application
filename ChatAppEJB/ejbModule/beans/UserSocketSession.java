package beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private Map<String, String> privateMessage;
    
    @PostConstruct
    public void initialise(){
        sessionMap     = new HashMap<String, Session>();
        privateMessage = new HashMap<String, String>();
    }

    @Override
    @Lock(LockType.WRITE)
    public void addUserSession(String username, Session value) {
        if(!isSessionActive(username))
            sessionMap.put(username, value);
        
    }
    
    @Override
    @Lock(LockType.WRITE)
    public void addPrivateMessage(String sessionId, String username){
        if(!isUserActive(sessionId))
            privateMessage.put(sessionId, username);
    }
    
    @Override
    @Lock(LockType.WRITE)
    public void removePrivateMessage(String sessionId){
        if(isUserActive(sessionId))
            privateMessage.remove(sessionId);
        
    }
    
    @Override
    @Lock(LockType.READ)
    public String getPrivateMessage(String sessionId){
        if(isUserActive(sessionId))
            return privateMessage.get(sessionId);
        
        return null;
    }
    
    @Override
    @Lock(LockType.WRITE)
    public void removeUserSession(String username) {
        if(isSessionActive(username))
            sessionMap.remove(username);
        
    }

    @Override
    @Lock(LockType.READ)
    public boolean isSessionActive(String username) {
        return sessionMap.containsKey(username);
    }
    
    @Override
    @Lock(LockType.READ)
    public boolean isUserActive(String sessionId){
        return privateMessage.containsKey(sessionId);
    }
    
    @Override
    @Lock(LockType.READ)
    public Session getSession(String key){
        if(isSessionActive(key))
            return sessionMap.get(key);
        
        return null;
    }

    @Override
    @Lock(LockType.WRITE)
    public void removeUserSession(Session value) {
        sessionMap.values().remove(value);
        
    }
    
    @Override
    @Lock(LockType.READ)
    public List<Session> getAllSessions(){
        return sessionMap.entrySet().stream().map(e -> e.getValue())
                                             .collect(Collectors.toList());
    }
}

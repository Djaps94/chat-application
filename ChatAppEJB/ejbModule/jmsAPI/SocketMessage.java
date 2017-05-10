package jmsAPI;

import java.io.Serializable;

import model.User;

public class SocketMessage implements Serializable{

    private static final long serialVersionUID = 1L;


    public enum type { LOGIN, 
                       REGISTER, 
                       LOGOUT, 
                       MESSAGE,
                       USERNAME_EXISTS,
                       ALREADY_LOGED,
                       NOT_REGISTERED,
                       NOT_LOGOUT,
                       ACTIVE_USERS,
                       ADD_ACTIVE,
                       REMOVE_ACTIVE
                      };
    
    private type messageType;
    private String username;
    private String password;
    private String hostAddress;
    private String sessionId;
    private User user;
    
    public SocketMessage() { }
    
    
    public SocketMessage(String username, String password, type typeMessage){
        this.messageType = typeMessage;
        this.username    = username;
        this.password    = password;
    }
    
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }


    public type getMessageType() {
        return messageType;
    }


    public void setMessageType(type messageType) {
        this.messageType = messageType;
    }


    public String getHostAddress() {
        return hostAddress;
    }


    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }
}

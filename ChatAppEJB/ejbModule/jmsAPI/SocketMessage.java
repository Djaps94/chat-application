package jmsAPI;

import java.io.Serializable;

public class SocketMessage implements Serializable{

    private static final long serialVersionUID = 1L;


    public enum type { LOGIN, 
                       REGISTER, 
                       LOGOUT, 
                       MESSAGE,
                       USERNAME_EXISTS,
                       ALREADY_LOGED,
                       NOT_REGISTERED,
                       NOT_LOGOUT
                      };
    
    private type messageType;
    private String username;
    private String password;
    private String errorMessage;
    
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


    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

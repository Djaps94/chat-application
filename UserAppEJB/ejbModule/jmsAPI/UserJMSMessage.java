package jmsAPI;

import java.io.Serializable;

import model.User;

public class UserJMSMessage implements Serializable{

    private static final long serialVersionUID = -6332424773771578337L;

    public static enum types { REGISTER, 
                                LOGIN, 
                                LOGOUT,
                                REGISTERED,
                                ACTIVE
                              }
    private String username;
    private String password;
    private String address;
    private String alias;
    private types messageType;
    private User u;
    
    public UserJMSMessage() { }
    
    public UserJMSMessage(String username, String password, String address, String alias, types type) {
        this.username    = username;
        this.password    = password;
        this.address     = address;
        this.alias       = alias;
        this.messageType = type;
    }

    public UserJMSMessage(User u, types type){
        this.u           = u;
        this.messageType = type;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public types getMessageType() {
        return messageType;
    }
    
    public User getUser(){
        return u;
    }
}

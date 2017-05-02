package sockets;

public class SocketMessage {
    
    
    public enum type { LOGIN, 
                       REGISTER, 
                       LOGOUT, 
                       MESSAGE 
                      };
    
    private type messageType;
    private String username;
    private String password;
    
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
}

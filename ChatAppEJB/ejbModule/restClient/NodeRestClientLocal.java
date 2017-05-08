package restClient;

import java.util.List;

import javax.ejb.Local;

import jmsAPI.UserJMSMessage;
import model.Host;
import model.User;

@Local
public interface NodeRestClientLocal {

    public List<Host> register(String nodeAddress, String slaveAddress, String alias);
    public void unregister(String nodeAddress, String slaveAdress);
    public void registerUser(String destination, UserJMSMessage message);
    public void addUser(String destination, UserJMSMessage message);
    public void removeUser(String destination, UserJMSMessage message);
}

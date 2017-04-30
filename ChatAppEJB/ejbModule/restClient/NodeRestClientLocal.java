package restClient;

import java.util.List;

import javax.ejb.Local;

import model.Host;
import model.User;

@Local
public interface NodeRestClientLocal {

    public List<Host> register(String nodeAddress, String slaveAddress, String alias);
    public void unregister(String nodeAddress, String slaveAdress);
    public void registerUser(String destination, User user);
    public void addUser(String destination, User user);
    public void removeUser(String destination, User user);
}

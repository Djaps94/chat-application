package restClient;

import java.util.List;

import javax.ejb.Local;

import model.Host;

@Local
public interface NodeRestClientLocal {

    public List<Host> register(String nodeAddress, String slaveAddress, String alias);
    public void unregister(String nodeAddress, String slaveAdress);
}

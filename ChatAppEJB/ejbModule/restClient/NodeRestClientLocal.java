package restClient;

import javax.ejb.Local;

@Local
public interface NodeRestClientLocal {

    public void register(String nodeAddress, String slaveAddress, String alias);
    public boolean unregister(String nodeAddress, String slaveAdress);
}

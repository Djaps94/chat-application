package beans;

import java.util.List;

import javax.ejb.Local;

import model.Host;

@Local
public interface HostManagmentLocal {

    public void addHost(Host host);
    public void removeHost(Host host);
    public List<Host> getAllHosts(Host host);
    public boolean contains(Host host);
    
}

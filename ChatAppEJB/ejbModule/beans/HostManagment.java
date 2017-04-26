package beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import model.Host;

@Singleton
@Local(HostManagmentLocal.class)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class HostManagment implements HostManagmentLocal{
    
    private List<Host> hosts;
    
    @PostConstruct
    public void initialise(){
        this.hosts = new ArrayList<>();
    }

    @Override
    @Lock(LockType.WRITE)
    public void addHost(Host host) {
        hosts.add(host);
    }

    @Override
    @Lock(LockType.WRITE)
    public void removeHost(Host host) {
        hosts.remove(host);
        
    }

    @Override
    @Lock(LockType.READ)
    public List<Host> getAllHosts(Host host) {
        return hosts;
        
    }

    @Override
    @Lock(LockType.READ)
    public boolean contains(Host host) {
        return hosts.contains(host);
    }

}

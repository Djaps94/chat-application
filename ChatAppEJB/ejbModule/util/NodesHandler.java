package util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jboss.logging.Logger;

import beans.HostManagmentLocal;
import model.Host;
import restClient.NodeRestClientLocal;
import restClient.UserRestClientLocal;

@Startup
@Singleton
@Local(NodesHandlerLocal.class)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class NodesHandler implements NodesHandlerLocal{
    
    private static final int DEFAULT_PORT      = 8080;
    private static final int ADMIN_PORT        = 9990;
    private static final String MASTER_IP      = "master";
    private static final String ALIAS          = "alias";
    private static final String OFFSET         = "jboss.socket.binding.port-offset";
    private static final String SCRIPT         = "shutdown-server.sh";
    private static final String LOCAL          = "local";
    
    private String masterIpAdress;
    private String slaveAddress;
    private Boolean master = true;
    
    private Logger logger = Logger.getLogger(NodesHandler.class);

    @EJB
    private HostManagmentLocal hostBean;
    
    @EJB
    private NodeRestClientLocal nodeRequester;
    
    @EJB
    private UserRestClientLocal userRequester;
    
    @PostConstruct
    @Lock(LockType.WRITE)
    public void initialise(){
        masterIpAdress = System.getProperty(MASTER_IP);
        if(masterIpAdress == "" || masterIpAdress == null){
            logger.warn("Master node up!");
            Host master = createHost();
            hostBean.addHost(master);
            return;
        }
        
        master = false;
        if(System.getProperty(OFFSET) == null || System.getProperty(OFFSET) == ""){
            String ip          = System.getProperty(LOCAL);
            shutdownServer(ip+":"+ADMIN_PORT);
        }
          
        logger.warn("Slave node up!");
        Host slave   = createHost();
        slaveAddress = slave.getAdress();
        logger.warn("masterAddress: "+masterIpAdress+", slaveAddress: "+slaveAddress);
        List<Host> hosts = nodeRequester.register(masterIpAdress, slaveAddress, slave.getAlias());
        
        if(!hosts.isEmpty())
            hostBean.getAllHosts().addAll(hosts);
        
       
        slave.setRegisteredUsers(userRequester.getRegisteredUsers(masterIpAdress));
        
    }
    
    @PreDestroy
    public void destroy(){
        if(!isMaster()){
            nodeRequester.unregister(masterIpAdress, slaveAddress);
        }
    }
    
    private Host createHost(){
        try {
            InetAddress adress = InetAddress.getLocalHost();
            String ip          = System.getProperty(LOCAL) == null ? "127.0.0.1" : System.getProperty(LOCAL);
            int offset         = (System.getProperty(OFFSET) == null || System.getProperty(OFFSET) == "") ? 0: Integer.parseInt(System.getProperty(OFFSET));
            String hostName    = System.getProperty(ALIAS) == null ? adress.getHostName() : System.getProperty(ALIAS);
            
            if(offset == 0)
                masterIpAdress = ip+":"+(offset+DEFAULT_PORT);
            
            hostBean.setOwnerAddress(ip+":"+(offset+DEFAULT_PORT));
            hostBean.setOwnerAlias(hostName);
            return new Host(ip+":"+(offset+DEFAULT_PORT), hostName);
            
        }
        catch (UnknownHostException e) { }
        return null;
    }
    
    private void shutdownServer(String... ipport){
        URL script        = this.getClass().getClassLoader().getResource(SCRIPT);
        String permission = "chmod +x "+script.getPath();
        try{
            switch(ipport.length){
                case 0: {
                    Runtime.getRuntime().exec(permission);
                    String[] cmd = { script.getPath() }; 
                    Runtime.getRuntime().exec(cmd);
                    break;
                }
                case 1: {
                    Runtime.getRuntime().exec(permission);
                    String[] cmd = { script.getPath(), ipport[0]}; 
                    Runtime.getRuntime().exec(cmd);
                    break;
                }
            }
        }
        catch (IOException e) { e.printStackTrace(); }
    }
    
    @Lock(LockType.READ)
    @Override
    public boolean isMaster(){
        return master;
    }
    
    @Lock(LockType.READ)
    @Override
    public String getMasterAddress(){
        return masterIpAdress;
    }
    
}

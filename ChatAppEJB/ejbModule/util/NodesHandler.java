package util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import beans.HostManagmentLocal;
import model.Host;

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
    
    private String masterIpAdress;

    @EJB
    private HostManagmentLocal hostBean;
    
    @PostConstruct
    @Lock(LockType.WRITE)
    public void initialise(){
        masterIpAdress = System.getProperty(MASTER_IP);
        if(masterIpAdress == "" || masterIpAdress == null){
            System.out.println("This is master node!");
            Host master = createHost();
            hostBean.addHost(master);
            return;
        }
        
        if(System.getProperty(OFFSET) == null || System.getProperty(OFFSET) == ""){
            try{
            InetAddress adress = InetAddress.getLocalHost();
            String ip          = adress.getHostAddress();
            shutdownServer(ip+":"+ADMIN_PORT);
            }
            catch(UnknownHostException e) { }
        }
          
        
        Host slave = createHost();
        System.out.println("Slave created! Life is good!");
        
    }
    
    
    private Host createHost(){
        try {
            InetAddress adress = InetAddress.getLocalHost();
            String ip          = adress.getHostAddress();
            int offset         = (System.getProperty(OFFSET) == null || System.getProperty(OFFSET) == "") ? 0: Integer.parseInt(System.getProperty(OFFSET));
            String hostName    = System.getProperty(ALIAS) == null ? adress.getHostName() : System.getProperty(ALIAS);
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
    
    public boolean isMaster(){
        return !(masterIpAdress == null || masterIpAdress.equals(""));
    }
    
}

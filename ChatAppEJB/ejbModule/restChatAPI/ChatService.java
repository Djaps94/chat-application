package restChatAPI;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.HostManagmentLocal;
import model.Host;
import restClient.NodeRestClientLocal;
import util.NodesHandlerLocal;

@Stateless
@Path("node")
public class ChatService {
    
    
    @EJB
    private HostManagmentLocal hostBean;
    
    @EJB
    private NodesHandlerLocal nodesBean;
    
    @EJB
    private NodeRestClientLocal nodeRequester;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/register")
    public List<Host> register(@FormParam("slaveAdress") String address, @FormParam("alias") String alias){
        Host host = new Host(address, alias);
        if(hostBean.contains(host)) return new ArrayList<Host>();
    
        if(nodesBean.isMaster()){
            for(Host h : hostBean.getAllHosts()){
                if(h.getAdress().equals(hostBean.getOwnerAddress()) || h.getAdress().equals(nodesBean.getMasterAddress()))
                    continue;
                
                List<Host> list = nodeRequester.register(h.getAdress(), address, alias);
            }
        }
        hostBean.addHost(host);
        return hostBean.getAllHosts();
    }
    
    @POST
    @Path("/unregister")
    public void unregister(@FormParam("slaveAddress") String address){
        if(nodesBean.isMaster()){
            hostBean.getAllHosts().stream().filter(h -> !(h.getAdress().equals(hostBean.getOwnerAddress())) || h.getAdress().equals(nodesBean.getMasterAddress()))
                                           .forEach(h -> nodeRequester.unregister(h.getAdress(), address));
                
        }
        
        
        if(hostBean.getAllHosts().stream().anyMatch(h -> h.getAdress().equals(address)))
            hostBean.getAllHosts().remove(hostBean.getAllHosts().stream().filter(h -> h.getAdress().equals(address))
                                                                         .findFirst()
                                                                         .get()); 
    }
}

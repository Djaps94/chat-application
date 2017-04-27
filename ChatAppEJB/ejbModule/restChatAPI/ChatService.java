package restChatAPI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

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
    @Path("/register")
    public void register(@FormParam("slaveAdress") String address, @FormParam("alias") String alias){
        Host host = new Host(address, alias);
        if(hostBean.contains(host)) return;
    
        hostBean.addHost(host);
        if(nodesBean.isMaster()){
            hostBean.getAllHosts().stream().filter(h -> !(h.getAdress().equals(address)))
                                           .forEach(h -> nodeRequester.register(h.getAdress(), address, alias));
        }
    }
    
    @POST
    @Path("/unregister")
    public void unregister(@FormParam("slaveAdress") String address){
        if(nodesBean.isMaster()){
            hostBean.getAllHosts().stream().filter(h -> !(h.getAdress().equals(address)))
                                           .forEach(h -> nodeRequester.unregister(h.getAdress(), address));
                
        }
        
        
        if(hostBean.getAllHosts().stream().anyMatch(h -> h.getAdress().equals(address)))
            hostBean.getAllHosts().remove(hostBean.getAllHosts().stream().filter(h -> h.getAdress().equals(address))
                                                                         .findFirst()
                                                                         .get());
                                                             
                                                    
        
    }
    

}

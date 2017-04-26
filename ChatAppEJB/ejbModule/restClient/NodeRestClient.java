package restClient;

import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import model.Host;

@Stateless
public class NodeRestClient {
  
    public List<Host> register(String masterAddress, String slaveAddress, String alias){
        ResteasyWebTarget target = createResteasyClient("http://"+masterAddress+"/ChatApp/rest/node/register");
        Form form = new Form();
        form.param("slaveAdress", slaveAddress);
        form.param("alias", alias);
        Entity<Form> entity = Entity.form(form);
        Response response   = target.request(MediaType.APPLICATION_JSON).post(entity);
        List<Host> hosts    = response.readEntity(new GenericType<List<Host>>(){});
        return hosts;
    }
    
    public boolean unregister(){
        return false;
    }
    
    private ResteasyWebTarget createResteasyClient(String destination){
        ResteasyClient client    = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(destination);
        return target;
    }

}

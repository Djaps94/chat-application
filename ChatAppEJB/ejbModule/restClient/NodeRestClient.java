package restClient;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

@Stateless
@Local(NodeRestClientLocal.class)
public class NodeRestClient {
  
    public void register(String masterAddress, String slaveAddress, String alias){
        ResteasyWebTarget target = createResteasyClient("http://"+masterAddress+"/ChatApp/rest/node/register");
        Form form = new Form();
        form.param("slaveAdress", slaveAddress);
        form.param("alias", alias);
        Entity<Form> entity = Entity.form(form);
        target.request(MediaType.APPLICATION_JSON).post(entity);
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

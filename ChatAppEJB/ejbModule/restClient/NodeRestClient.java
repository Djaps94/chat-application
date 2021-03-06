package restClient;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import jmsAPI.UserJMSMessage;
import model.Host;

@Stateless
@Local(NodeRestClientLocal.class)
public class NodeRestClient implements NodeRestClientLocal{
  
    public List<Host> register(String masterAddress, String slaveAddress, String alias){
        ResteasyWebTarget target = createResteasyClient("http://"+masterAddress+"/ChatApp/rest/node/register");
        Form form = new Form();
        form.param("slaveAdress", slaveAddress);
        form.param("alias", alias);
        Entity<Form> entity = Entity.form(form);
        Response response   = target.request(MediaType.APPLICATION_JSON).post(entity);
        List<Host> list     = response.readEntity(new GenericType<List<Host>>(){});
        response.close();
        return list;
    }
    
    public void unregister(String masterAddress, String slaveAddress){
        ResteasyWebTarget target = createResteasyClient("http://"+masterAddress+"/ChatApp/rest/node/unregister");
        Form form                = new Form();
        form.param("slaveAddress", slaveAddress);
        Entity<Form> entity = Entity.form(form);
        target.request().post(entity);
    }
    
    public void registerUser(String destination, UserJMSMessage message){
        ResteasyWebTarget target = createResteasyClient("http://"+destination+"/ChatApp/rest/chat/register");
        target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
    }
    
    public void addUser(String destination, UserJMSMessage message){
        ResteasyWebTarget target = createResteasyClient("http://"+destination+"/ChatApp/rest/chat/login");
        target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
    }
    
    public void removeUser(String destination, UserJMSMessage message){
        ResteasyWebTarget target = createResteasyClient("http://"+destination+"/ChatApp/rest/chat/logout");
        target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
    }
    
    private ResteasyWebTarget createResteasyClient(String destination){
        ResteasyClient client    = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(destination);
        return target;
    }

}

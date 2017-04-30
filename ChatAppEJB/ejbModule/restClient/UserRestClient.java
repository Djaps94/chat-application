package restClient;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import model.User;

@Stateless
@Local(UserRestClientLocal.class)
public class UserRestClient implements UserRestClientLocal{

    @Override
    public List<User> getRegisteredUsers(String masterAddress) {
        ResteasyWebTarget target = createResteasyClient("http://"+masterAddress+"/UserApp/rest/user/allRegistered");
        Response response        = target.request(MediaType.APPLICATION_JSON).get();
        return response.readEntity(new GenericType<List<User>>(){});
  
    }
    
    private ResteasyWebTarget createResteasyClient(String destination){
        ResteasyClient client    = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(destination);
        return target;
    }

}

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
import model.Message;
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
    
    @Override
    public void registerUser(String masterAddress, String username, String password, String address, String alias, String sessionId){
        ResteasyWebTarget target = createResteasyClient("http://"+masterAddress+"/UserApp/rest/user/register");
        Form form                = new Form();
        form.param("username", username);
        form.param("password", password);
        form.param("address", address);
        form.param("alias", alias);
        form.param("session", sessionId);
        Entity<Form> entity = Entity.form(form);
        target.request().post(entity);
    } 
    

    @Override
    public void loginUser(String masterAddress, String username, String password, String sessionId, String hostAddress) {
        ResteasyWebTarget target = createResteasyClient("http://"+masterAddress+"/UserApp/rest/user/login");
        Form form                = new Form();
        form.param("username", username);
        form.param("password", password);
        form.param("session", sessionId);
        form.param("address", hostAddress);
        Entity<Form> entity = Entity.form(form);
        target.request().post(entity);
        
    }

    @Override
    public void logoutUser(String masterAddress, User user, String sessionId) {
        ResteasyWebTarget target = createResteasyClient("http://"+masterAddress+"/UserApp/rest/user/logout");
        UserJMSMessage message = new UserJMSMessage(user, UserJMSMessage.types.LOGOUT);
        message.setSessionId(sessionId);
        target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
    }
    
    @Override
    public void publishMessage(String address, Message message){
        ResteasyWebTarget target = createResteasyClient("http://"+address+"/ChatApp/rest/chat/publish");
        target.request().post(Entity.entity(message, MediaType.APPLICATION_JSON));
    }
    
    private ResteasyWebTarget createResteasyClient(String destination){
        ResteasyClient client    = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(destination);
        return target;
    }
}

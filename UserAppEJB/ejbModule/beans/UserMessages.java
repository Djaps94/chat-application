package beans;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import jmsAPI.UserJMSMessage;
import model.User;

@Stateless
@Local(UserMessagesInterface.class)
public class UserMessages implements UserMessagesInterface{

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory factory;
    
    @Resource(mappedName = "java:/jms/queue/chatQueue")
    private Queue chatQueue;
    
    private QueueSession session;
    private QueueRequestor requestor;
    private Connection connection;
    
    @EJB
    private UserManagmentLocal userBean;
    
    public UserMessages() { }
    
    @PostConstruct
    public void initialise(){
        try{
            this.connection = factory.createConnection();
            connection.start();
            this.session    = (QueueSession)connection.createSession();
            this.requestor  = new QueueRequestor(session, chatQueue);
        }
        catch(JMSException e) { return; }
    }
    
    @PreDestroy
    public void destroy(){
        try {
            connection.stop();
            requestor.close();
        }
        catch (JMSException e) { }
    }
    
    @Override
    public void registerMessage(UserJMSMessage message) {
        try {
            Boolean successfulRegister = userBean.register(message.getUsername(), message.getPassword(), message.getAddress(), message.getAlias());
            StreamMessage msg          = session.createStreamMessage();
            msg.setBooleanProperty("registerAnswer", successfulRegister);
            requestor.request(msg);
        }
        catch (UsernameExistsException | JMSException e) {
            try {
                StreamMessage msg = session.createStreamMessage();
                msg.setBooleanProperty("registerError", false);
                requestor.request(msg);
            }catch(Exception ex) { }
        }
        
    }

    @Override
    public void loginMessage(String username, String password) {
        try{
            User user         = userBean.login(username, password);
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(user);
            requestor.request(msg);
        }
        catch (InvalidCredentialsException | JMSException e) { 
            try{
                TextMessage msg = session.createTextMessage();
                msg.setStringProperty("error", "Invalid credentials");
                requestor.request(msg);
            }
            catch(Exception ex) { }
        }
    }

    @Override
    public void logoutMessage(UserJMSMessage message) {
        try{
            Boolean logout  = userBean.logout(message.getUser());
            StreamMessage m = session.createStreamMessage();
            m.setBooleanProperty("logout", logout);
            requestor.request(m);
        }
        catch(JMSException e) { }
    }

    @Override
    public void getRegisteredMessage() {
        try{
            ArrayList<User> users = (ArrayList<User>) userBean.getAllRegisteredUsers();
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(users);
            requestor.request(msg);
        }
        catch(JMSException e) { }
    }

    @Override
    public void getActiveMessage() {
        try{
            ArrayList<User> users = (ArrayList<User>) userBean.getAllActiveUsers();
            ObjectMessage msg = session.createObjectMessage();
            msg.setObject(users);
            requestor.request(msg);   
        }catch(JMSException e) { }
    }

}

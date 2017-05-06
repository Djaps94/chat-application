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
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import exceptions.InvalidCredentialsException;
import exceptions.UsernameExistsException;
import jmsAPI.UserJMSMessage;
import model.User;

@Stateless
@Local(UserMessagesLocal.class)
public class UserMessages implements UserMessagesLocal{

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory factory;
    
    @Resource(mappedName = "java:/jms/queue/chatQueue")
    private Queue chatQueue;
    
    private QueueSession session;
    private QueueSender sender;
    private Connection connection;
    
    @EJB
    private UserManagmentLocal userBean;
    
    public UserMessages() { }
    
    @PostConstruct
    public void initialise(){
        try{
            this.connection = factory.createConnection();
            connection.start();
            this.session    = (QueueSession)connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.sender     = session.createSender(chatQueue);
        }
        catch(JMSException e) { return; }
    }
    
    @PreDestroy
    public void destroy(){
        try {
            connection.stop();
            sender.close();
        }
        catch (JMSException e) { }
    }
    
    @Override
    public void registerMessage(UserJMSMessage message) {
        try {
            User user      = userBean.register(message.getUsername(), message.getPassword(), message.getAddress(), message.getAlias());
            ObjectMessage msg = session.createObjectMessage(user);
            msg.setObjectProperty("registerAnswer", "");
            sender.send(msg);
        }
        catch (UsernameExistsException | JMSException e) {
            try {
                e.printStackTrace();
                StreamMessage msg = session.createStreamMessage();
                msg.setBooleanProperty("registerError", false);
                sender.send(msg);
            }catch(Exception ex) { }
        }
        
    }

    @Override
    public void loginMessage(String username, String password) {
        try{
            User user         = userBean.login(username, password);
            ObjectMessage msg    = session.createObjectMessage(user);
            msg.setObjectProperty("login", "");
            sender.send(msg);
        }
        catch (InvalidCredentialsException | JMSException e) { 
            try{
                TextMessage msg = session.createTextMessage();
                msg.setStringProperty("error", "Invalid credentials");
                sender.send(msg);
            }
            catch(Exception ex) { }
        }
    }

    @Override
    public void logoutMessage(UserJMSMessage message) {
        try{
            User logout  = userBean.logout(message.getUser());
            ObjectMessage m = session.createObjectMessage(logout);
            m.setObjectProperty("logout", "");
            sender.send(m);
        }
        catch(JMSException e) { }
    }

    @Override
    public void getRegisteredMessage() {
        try{
            ArrayList<User> users = (ArrayList<User>) userBean.getAllRegisteredUsers();
            ObjectMessage msg        = session.createObjectMessage(users);
            msg.setObjectProperty("registered", "");
            sender.send(msg);
        }
        catch(JMSException e) { }
    }

    @Override
    public void getActiveMessage() {
        try{
            ArrayList<User> users = (ArrayList<User>) userBean.getAllActiveUsers();
            ObjectMessage msg        = session.createObjectMessage(users);
            msg.setObjectProperty("active", "");
            sender.send(msg);   
        }catch(JMSException e) { }
    }

}

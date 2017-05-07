package beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
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

import jmsAPI.UserJMSMessage;
import model.User;

@Stateless
@Local(ChatMessagesLocal.class)
public class ChatMessages implements ChatMessagesLocal{

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory factory;
    
    @Resource(mappedName = "java:/jms/queue/userQueue")
    private Queue userQueue;
    
    private Connection connection;
    private QueueSender sender;
    private QueueSession session;
    
    @PostConstruct
    public void initialise(){
        try{
            this.connection = factory.createConnection();
            connection.start();
            this.session    = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.sender     = session.createSender(userQueue);
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
    public void registerMessage(String username, String password, String address, String alias) {
        try{
            UserJMSMessage message = new UserJMSMessage(username, password, address, alias, UserJMSMessage.types.REGISTER);
            ObjectMessage msg      = session.createObjectMessage(message);
            sender.send(msg);
        }
        catch(JMSException e) { }
    }

    @Override
    public void loginMessage(String username, String password) {
        try{
            UserJMSMessage message = new UserJMSMessage();
            message.setMessageType(UserJMSMessage.types.LOGIN);
            message.setUsername(username);
            message.setPassword(password);
            ObjectMessage msg      = session.createObjectMessage(message);
            sender.send(msg);
        }
        catch(JMSException e) { }
    }

    @Override
    public void logoutMessage(User u) {
        try{
            UserJMSMessage message = new UserJMSMessage(u, UserJMSMessage.types.LOGOUT);
            ObjectMessage msg      = session.createObjectMessage(message);
            sender.send(msg);
        }
        catch(JMSException e) { }
    }

    @Override
    public void getRegisteredUsers() {
        try{
            UserJMSMessage message = new UserJMSMessage(UserJMSMessage.types.REGISTER);
            ObjectMessage msg      = session.createObjectMessage(message);
            sender.send(msg);
        }
        catch(JMSException e) { }
    }

    @Override
    public void getActiveUsers() {
        try{
            UserJMSMessage message = new UserJMSMessage(UserJMSMessage.types.ACTIVE);
            ObjectMessage msg      = session.createObjectMessage(message);
            sender.send(msg);
        }
        catch(JMSException e) { }
    }

}

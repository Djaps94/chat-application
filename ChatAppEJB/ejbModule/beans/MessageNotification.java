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

import model.Message;

@Stateless
@Local(MessageNotificationLocal.class)
public class MessageNotification implements MessageNotificationLocal{
    
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory factory;
    
    @Resource(mappedName = "java:/jms/queue/socketMessageQueue")
    private Queue socketQueue;
    
    private Connection connection;
    private QueueSender sender;
    private QueueSession session;
    
    @PostConstruct
    public void initialise(){
        try{
            this.connection = factory.createConnection();
            connection.start();
            this.session    = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.sender     = session.createSender(socketQueue);
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
    
    
    public void sendMessage(Message message){
        try {
            ObjectMessage msg = session.createObjectMessage(message);
            sender.send(msg);
        }
        catch (JMSException e) { e.printStackTrace(); }
        
    }
    
}

package beans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

@Stateless
@Local(ResponseSocketMessageLocal.class)
public class ResponseSocketMessage implements ResponseSocketMessageLocal{
    
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory factory;
    
    @Resource(mappedName = "java:/jms/queue/socketQueue")
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
    
    //TODO: Response methods 

}

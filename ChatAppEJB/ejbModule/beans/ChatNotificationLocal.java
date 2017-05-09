package beans;

import javax.ejb.Local;

@Local
public interface ChatNotificationLocal {

    public void sendNotification();
    
    
}

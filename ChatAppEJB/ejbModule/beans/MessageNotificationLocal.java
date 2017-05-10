package beans;

import javax.ejb.Local;

import model.Message;

@Local
public interface MessageNotificationLocal {

    public void sendMessage(Message message);
}

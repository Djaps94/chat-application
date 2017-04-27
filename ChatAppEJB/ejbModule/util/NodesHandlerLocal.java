package util;

import javax.ejb.Local;

@Local
public interface NodesHandlerLocal {

    public boolean isMaster();
}

package pt.tecnico.bicloin.hub;

import pt.tecnico.bicloin.hub.exceptions.BadEntrySpecificationException;
import java.util.List;
import java.util.ArrayList;
import io.grpc.*;


public class HubServerImplOperations {

    public HubServerImplOperations() {}


    public synchronized String ping(String ping) throws BadEntrySpecificationException{
        if (ping == null || ping.isBlank()){
            throw new BadEntrySpecificationException("Error ping: null or empty");
        }
        return ping;
    }

    public synchronized void sys_status(String sysStatus) throws BadEntrySpecificationException{
        if(sysStatus == null || sysStatus.isBlank()){
            throw new BadEntrySpecificationException("Error system status: null or empty");
        }
        
    }
}
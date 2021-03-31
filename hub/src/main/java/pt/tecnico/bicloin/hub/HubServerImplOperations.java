package pt.tecnico.bicloin.hub;

import pt.tecnico.bicloin.hub.exceptions.BadEntrySpecificationException;

public class HubServerImplOperations {
    
    public HubServerImplOperations() {}

    public synchronized String ping(String ping) throws BadEntrySpecificationException{
        if (ping == null || ping.isBlank()){
            throw new BadEntrySpecificationException("Error ping: null or empty");
        }
        return ping;
    }
}
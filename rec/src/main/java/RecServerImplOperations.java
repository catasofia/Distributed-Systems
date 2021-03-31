package pt.tecnico.rec;

import pt.tecnico.rec.exceptions.BadEntrySpecificationException;

public class RecServerImplOperations {
    
    public RecServerImplOperations() {}

    public synchronized String ping(String ping) throws BadEntrySpecificationException{
        if (ping == null || ping.isBlank()){
            throw new BadEntrySpecificationException("Error ping: null or empty");
        }
        return ping;
    }
}
package pt.tecnico.rec;

import pt.tecnico.rec.exceptions.BadEntrySpecificationException;
import pt.tecnico.rec.MutableUser;
import java.util.Map;
import java.util.HashMap;

public class RecServerImplOperations {

    private Map <String, MutableUser> mutableUsers = new HashMap<>();
    
    public RecServerImplOperations() {}

    public synchronized String ping(String ping) throws BadEntrySpecificationException{
        if (ping == null || ping.isBlank()){
            throw new BadEntrySpecificationException("Error ping: null or empty");
        }
        return ping;
    }

    public synchronized String read(String input) throws BadEntrySpecificationException{
        if (input.equals("") || !mutableUsers.containsKey(input)){
            throw new BadEntrySpecificationException("Error read: null or empty");
        }

        String[] attributes = input.split("/");

        switch (attributes[1]){
            case "balance":
                MutableUser mutableUser = mutableUsers.get(attributes[0]);
                return String.valueOf(mutableUser.getBalance());

            default:
                return "";

        }

    }
}
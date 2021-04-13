package pt.tecnico.rec;

import pt.tecnico.rec.exceptions.BadEntrySpecificationException;
import pt.tecnico.rec.MutableUser;
import java.util.Map;
import java.util.HashMap;

public class RecServerImplOperations {

    private static Map <String, MutableUser> mutableUsers = new HashMap<>();
    private static Map <String, MutableStation> mutableStations = new HashMap<>();

    public RecServerImplOperations() {}

    public synchronized static void initializeStations(String abbr, Integer docksNr, Integer bikesNr){
        MutableStation station = new MutableStation(abbr, docksNr, bikesNr);
        mutableStations.put(abbr, station);
    }

    public synchronized String ping(String ping) throws BadEntrySpecificationException{
        if (ping == null || ping.isBlank()){
            throw new BadEntrySpecificationException("Error ping: null or empty");
        }
        return ping;
    }

    public synchronized String read(String input) throws BadEntrySpecificationException{
        //TODO -> ESTE IF NAO PODE SER ASSIM, SE NAO EXISTIR INICIALIZA, DIZ NO ENUNCIADO
        //if (input.equals("") || !mutableUsers.containsKey(input)){
        //    throw new BadEntrySpecificationException("Error read: null or empty");
        //}

        String[] attributes = input.split("/");
        switch (attributes[1]){
            case "balance":
                if (mutableUsers.get(attributes[0]) == null){
                    mutableUsers.put(attributes[0], new MutableUser(attributes[0]));
                    return "0 BIC";
                }
                else{
                    MutableUser mutableUser = mutableUsers.get(attributes[0]);
                    return mutableUser.getBalance() + " BIC";
                }
            case "info":
                if(mutableStations.get(attributes[0]) == null){
                    mutableStations.put(attributes[0], new MutableStation(attributes[0]));
                    //TODO ver o que retorna quando nao existe
                    return "";
                }
                else{
                    MutableStation mutableStation = mutableStations.get(attributes[0]);
                    String result = "";
                    result += mutableStation.getAvailableBikesNr() + " bicicletas,";
                    result = result + " " + mutableStation.getRequisitions() + " levantamentos,";
                    result = result + " " + mutableStation.getDeliveries() + " devoluções, ";
                    return result;
                }
            default:
                return "";

        }
    }

    public synchronized String write(String input) throws BadEntrySpecificationException{
        String[] attributes = input.split("/");

        /* switch (attributes[1]) {
            case "top_up": */
        if(attributes[1].startsWith("top_up")){
            if (mutableUsers.get(attributes[0]) == null) {
                mutableUsers.put(attributes[0], new MutableUser(attributes[0]));
                String[] amount = attributes[1].split(" ");
                mutableUsers.get(attributes[0]).increaseBalance(Integer.parseInt(amount[1]));
                Integer balance = mutableUsers.get(attributes[0]).getBalance();
                return String.valueOf(balance);
            }
            else {
                MutableUser mutableUser = mutableUsers.get(attributes[0]);
                String[] amount = attributes[1].split(" ");
                mutableUsers.get(attributes[0]).increaseBalance(Integer.parseInt(amount[1]));
                Integer balance = mutableUsers.get(attributes[0]).getBalance();
                return String.valueOf(balance);
            }
        }

        return ""; //default case
        /* default:
            return ""; */
    }
}
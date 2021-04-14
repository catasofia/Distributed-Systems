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
            throw new BadEntrySpecificationException("Erro ping: nulo ou vazio");
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
                mutableUser.increaseBalance(Integer.parseInt(amount[1]));
                Integer balance = mutableUser.getBalance();
                return String.valueOf(balance);
            }
        }
        else if(attributes[1].startsWith("bike_up")){
            String[] userId = attributes[1].split(" ");

            if(mutableStations.get(attributes[0]).getAvailableBikesNr() == 0) {
                throw new BadEntrySpecificationException("Erro write: Não há bicicletas disponiveis para requisitar.");
            }

            if (mutableUsers.get(userId[1]) == null) {
                mutableUsers.put(userId[1], new MutableUser(userId[1]));
            }

            if(mutableUsers.get(userId[1]).getBikeState()) {
                throw new BadEntrySpecificationException("Erro write: Este utilizador não pode requisitar mais bicicletas.");
            }

            if (mutableStations.get(attributes[0]) == null) {
                mutableStations.put(attributes[0], new MutableStation(attributes[0]));
                mutableStations.get(attributes[0]).bikeUp();
                mutableUsers.get(userId[1]).setBikeState();
                Integer requisitions = mutableStations.get(attributes[0]).getRequisitions();
                return String.valueOf(requisitions);
            }
            else {
                MutableStation mutableStation = mutableStations.get(attributes[0]);
                mutableStation.bikeUp();
                mutableUsers.get(userId[1]).setBikeState();
                Integer requisitions = mutableStation.getRequisitions();
                return String.valueOf(requisitions);
            }
        }
        else if(attributes[1].startsWith("bike_down")){
            String[] userId = attributes[1].split(" ");

            if(mutableStations.get(attributes[0]).getDocksNumber() -  mutableStations.get(attributes[0]).getAvailableBikesNr() == 0) {
                throw new BadEntrySpecificationException("Erro write: Não pode devolver a bicicleta nesta doca. Doca cheia.");
            }

            if (mutableStations.get(attributes[0]) == null) {
                mutableStations.put(attributes[0], new MutableStation(attributes[0]));
                mutableStations.get(attributes[0]).bikeDown();
                mutableUsers.get(userId[1]).setBikeState();
                Integer deliveries = mutableStations.get(attributes[0]).getDeliveries();
                return String.valueOf(deliveries);
            }
            else {
                MutableStation mutableStation = mutableStations.get(attributes[0]);
                mutableStations.get(attributes[0]).bikeDown();
                mutableUsers.get(userId[1]).setBikeState();
                Integer deliveries = mutableStation.getDeliveries();
                return String.valueOf(deliveries);
            }
        }

        return ""; //default case
    }
}
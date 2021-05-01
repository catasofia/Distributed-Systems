package pt.tecnico.rec;

import pt.tecnico.rec.exceptions.BadEntrySpecificationException;
import pt.tecnico.rec.MutableUser;
import java.util.Map;
import java.util.HashMap;

public class RecServerImplOperations {

    private Map <String, MutableUser> mutableUsers = new HashMap<>();
    private Map <String, MutableStation> mutableStations = new HashMap<>();
    private ReplicaManager replicaManager;

    public RecServerImplOperations(String zooHost, String zooPort) {
        replicaManager = new ReplicaManager(zooHost, zooPort, "/grpc/bicloin/rec");
    }

    public synchronized void initializeStations(String abbr, Integer docksNr, Integer bikesNr){
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
        String[] attributes = input.split("/");

        switch (attributes[1]){
            case "balance":
                if (mutableUsers.get(attributes[0]) == null){
                    mutableUsers.put(attributes[0], new MutableUser(attributes[0]));
                    replicaManager.update(input);
                    return "0 BIC";
                }
                else{
                    MutableUser mutableUser = mutableUsers.get(attributes[0]);
                    replicaManager.update(input);
                    return mutableUser.getBalance() + " BIC";
                }
            case "info":
                if(mutableStations.get(attributes[0]) == null){
                    mutableStations.put(attributes[0], new MutableStation(attributes[0]));
                    MutableStation mutableStation = mutableStations.get(attributes[0]);
                    String result = "";
                    result += mutableStation.getAvailableBikesNr() + " bicicletas,";
                    result = result + " " + mutableStation.getRequisitions() + " levantamentos,";
                    result = result + " " + mutableStation.getDeliveries() + " devoluções, ";
                    return result;
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
                mutableUsers.get(attributes[0]).increaseBalance(Integer.parseInt(amount[1])*10);
                Integer balance = mutableUsers.get(attributes[0]).getBalance();
                replicaManager.update(input);
                return String.valueOf(balance);
            }
            else {
                MutableUser mutableUser = mutableUsers.get(attributes[0]);
                String[] amount = attributes[1].split(" ");
                mutableUser.increaseBalance(Integer.parseInt(amount[1])*10);
                Integer balance = mutableUser.getBalance();
                replicaManager.update(input);
                return String.valueOf(balance);
            }
        }
        else if(attributes[1].startsWith("bike_up")){
            String[] userId = attributes[1].split(" ");

            if (mutableUsers.get(userId[1]) == null) {
                mutableUsers.put(userId[1], new MutableUser(userId[1]));
            }

            if(mutableStations.get(attributes[0]).getAvailableBikesNr() == 0) {
                throw new BadEntrySpecificationException("Erro write: Não há bicicletas disponiveis para requisitar.");
            }

            if(mutableUsers.get(userId[1]).getBikeState()) {
                throw new BadEntrySpecificationException("Erro write: Este utilizador não pode requisitar mais bicicletas.");
            }

            if(mutableUsers.get(userId[1]).getBalance() < 10) {
                throw new BadEntrySpecificationException("Erro write: Este utilizador não pode requisitar bicicletas. " +
                        "Conta com dinheiro insuficiente");
            }

            if (mutableStations.get(attributes[0]) == null) {
                mutableStations.put(attributes[0], new MutableStation(attributes[0]));
                mutableStations.get(attributes[0]).bikeUp();
                mutableUsers.get(userId[1]).setBikeState();
                mutableUsers.get(userId[1]).increaseBalance(-10);
                Integer requisitions = mutableStations.get(attributes[0]).getRequisitions();
                return String.valueOf(requisitions);
            }
            else {
                MutableStation mutableStation = mutableStations.get(attributes[0]);
                mutableStation.bikeUp();
                mutableUsers.get(userId[1]).setBikeState();
                mutableUsers.get(userId[1]).increaseBalance(-10);
                Integer requisitions = mutableStation.getRequisitions();
                return String.valueOf(requisitions);
            }
        }
        else if(attributes[1].startsWith("bike_down")){
            String[] userId = attributes[1].split(" ");

            if (mutableUsers.get(userId[1]) == null) {
                mutableUsers.put(userId[1], new MutableUser(userId[1]));
            }

            if(mutableStations.get(attributes[0]).getDocksNumber() -  mutableStations.get(attributes[0]).getAvailableBikesNr() == 0) {
                throw new BadEntrySpecificationException("Erro write: Não pode devolver a bicicleta nesta doca. Doca cheia.");
            }

            if(!(mutableUsers.get(userId[1]).getBikeState())){
                throw new BadEntrySpecificationException("Erro write: Este utilizador não tem bicicleta para devolver");
            }

            if (mutableStations.get(attributes[0]) == null) {
                mutableStations.put(attributes[0], new MutableStation(attributes[0]));
                mutableStations.get(attributes[0]).bikeDown();
                mutableUsers.get(userId[1]).setBikeState();
                mutableUsers.get(userId[1]).increaseBalance(Integer.parseInt(userId[2]));
                Integer deliveries = mutableStations.get(attributes[0]).getDeliveries();
                return String.valueOf(deliveries);
            }
            else {
                MutableStation mutableStation = mutableStations.get(attributes[0]);
                mutableStations.get(attributes[0]).bikeDown();
                mutableUsers.get(userId[1]).setBikeState();
                mutableUsers.get(userId[1]).increaseBalance(Integer.parseInt(userId[2]));
                Integer deliveries = mutableStation.getDeliveries();
                return String.valueOf(deliveries);
            }
        }

        return ""; //default case
    }
}
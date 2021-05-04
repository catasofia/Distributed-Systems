package pt.tecnico.rec;

import pt.tecnico.rec.exceptions.BadEntrySpecificationException;
import pt.tecnico.rec.MutableUser;
import java.util.Map;
import java.util.HashMap;

public class RecServerImplOperations {

    private Map <String, MutableUser> mutableUsers = new HashMap<>();
    private Map <String, MutableStation> mutableStations = new HashMap<>();
    //private ReplicaManager replicaManager;

    public RecServerImplOperations(String zooHost, String zooPort) {
        //replicaManager = new ReplicaManager(zooHost, zooPort, "/grpc/bicloin/rec");
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
                    return "0 BIC";
                }
                else{
                    MutableUser mutableUser = mutableUsers.get(attributes[0]);
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

    public synchronized void updateValues(String input, Integer newTag){
        String[] attributes = input.split("/");
        if(attributes[1].startsWith("top_up")){
            String[] amount = attributes[1].split(" ");
            mutableUsers.get(attributes[0]).setNewBalance(Integer.parseInt(amount[1]));
            mutableUsers.get(attributes[0]).setTagBalance(newTag);
        }
    }

    public Integer getTags(String input){
        if(!input.startsWith("update")) {
            String[] attributes = input.split("/");
            if (attributes[1].equals("balance") || attributes[1].startsWith("top_up"))
                return mutableUsers.get(attributes[0]).getTagBalance();
            else if (attributes[1].startsWith("info") || attributes[1].startsWith("bike_up") || attributes[1].startsWith("bike_down"))
                return mutableStations.get(attributes[0]).getTagStation();
            return -1; //default
        } else{
            String[] attributes = input.split(":");
            if (attributes[2].equals("balance") || attributes[2].startsWith("top_up"))
                return mutableUsers.get(attributes[1]).getTagBalance();
            //check this index number, line 89
            else if (attributes[1].startsWith("info") || attributes[1].startsWith("bike_up") || attributes[1].startsWith("bike_down"))
                return mutableStations.get(attributes[2]).getTagStation();
            return -1; //default
        }
    }

    public synchronized String write(String input) throws BadEntrySpecificationException{
        String result = "";
        if(input.startsWith("update")){
            String[] attributes1 = input.split(":");
            if(attributes1[2].equals("top_up")){
                mutableUsers.get(attributes1[1]).setNewBalance(Integer.parseInt(attributes1[3]));
                mutableUsers.get(attributes1[1]).setTagBalance(Integer.parseInt(attributes1[4]));
                return "";
            }
            else if(attributes1[1].equals("bike_up") || attributes1[1].equals("bike_down")){
                mutableStations.get(attributes1[2]).updateStats(Integer.parseInt(attributes1[3]),
                        Integer.parseInt(attributes1[4]), Integer.parseInt(attributes1[5]),
                        Integer.parseInt(attributes1[6]));
                mutableStations.get(attributes1[2]).setTagStation(Integer.parseInt(attributes1[7]));
                return "";
            }
        }
        String[] attributes = input.split("/");
        if(attributes[1].startsWith("top_up")){
            if (mutableUsers.get(attributes[0]) == null) {
                mutableUsers.put(attributes[0], new MutableUser(attributes[0]));
                String[] amount = attributes[1].split(" ");
                mutableUsers.get(attributes[0]).increaseBalance(Integer.parseInt(amount[1])*10);
                Integer balance = mutableUsers.get(attributes[0]).getBalance();
                return String.valueOf(balance);
            }
            else {
                MutableUser mutableUser = mutableUsers.get(attributes[0]);
                String[] amount = attributes[1].split(" ");
                mutableUser.increaseBalance(Integer.parseInt(amount[1])*10);
                Integer balance = mutableUser.getBalance();
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
                        "Conta com dinheiro insuficiente.");
            }

            if (mutableStations.get(attributes[0]) == null) {
                mutableStations.put(attributes[0], new MutableStation(attributes[0]));
                mutableStations.get(attributes[0]).bikeUp();
                mutableUsers.get(userId[1]).setBikeState();
                mutableUsers.get(userId[1]).increaseBalance(-10);
                Integer requisitions = mutableStations.get(attributes[0]).getRequisitions();
                Integer deliveries = mutableStations.get(attributes[0]).getDeliveries();
                Integer docks = mutableStations.get(attributes[0]).getDocksNumber();
                Integer bikes = mutableStations.get(attributes[0]).getAvailableBikesNr();
                result = requisitions + ":" + deliveries + ":" + docks + ":" + bikes;
                return result;
            }
            else {
                MutableStation mutableStation = mutableStations.get(attributes[0]);
                mutableStation.bikeUp();
                mutableUsers.get(userId[1]).setBikeState();
                mutableUsers.get(userId[1]).increaseBalance(-10);
                Integer requisitions = mutableStation.getRequisitions();
                Integer deliveries = mutableStations.get(attributes[0]).getDeliveries();
                Integer docks = mutableStations.get(attributes[0]).getDocksNumber();
                Integer bikes = mutableStations.get(attributes[0]).getAvailableBikesNr();
                result = requisitions + ":" + deliveries + ":" + docks + ":" + bikes;
                return result;
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
                throw new BadEntrySpecificationException("Erro write: Este utilizador não tem bicicleta para devolver.");
            }

            if (mutableStations.get(attributes[0]) == null) {
                mutableStations.put(attributes[0], new MutableStation(attributes[0]));
                mutableStations.get(attributes[0]).bikeDown();
                mutableUsers.get(userId[1]).setBikeState();
                mutableUsers.get(userId[1]).increaseBalance(Integer.parseInt(userId[2]));
                Integer requisitions = mutableStations.get(attributes[0]).getRequisitions();
                Integer deliveries = mutableStations.get(attributes[0]).getDeliveries();
                Integer docks = mutableStations.get(attributes[0]).getDocksNumber();
                Integer bikes = mutableStations.get(attributes[0]).getAvailableBikesNr();
                result = requisitions + ":" + deliveries + ":" + docks + ":" + bikes;
                return result;
            }
            else {
                MutableStation mutableStation = mutableStations.get(attributes[0]);
                mutableStations.get(attributes[0]).bikeDown();
                mutableUsers.get(userId[1]).setBikeState();
                mutableUsers.get(userId[1]).increaseBalance(Integer.parseInt(userId[2]));
                Integer requisitions = mutableStations.get(attributes[0]).getRequisitions();
                Integer deliveries = mutableStations.get(attributes[0]).getDeliveries();
                Integer docks = mutableStations.get(attributes[0]).getDocksNumber();
                Integer bikes = mutableStations.get(attributes[0]).getAvailableBikesNr();
                result = requisitions + ":" + deliveries + ":" + docks + ":" + bikes;
                return result;
            }
        }
        return ""; //default case
    }
}
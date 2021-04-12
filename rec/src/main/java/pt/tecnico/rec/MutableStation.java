package pt.tecnico.rec;

import java.util.*;
import org.apache.commons.lang3.tuple.MutablePair;

public class MutableStation{

    private static MutablePair<String, Boolean[]> _docksAvailability = new MutablePair<>();
    //null == there is an available bike
    private Integer _deliveries;
    private Integer _requisitions;

    public MutableStation(String abbr){
        _docksAvailability.setLeft(abbr + "/docksAvailability");
        /*Boolean[] docks = new Boolean[];
        _docksAvailability.setRight(docks);*/
        _deliveries = 0;
        _requisitions = 0;
    }

    public MutableStation(String abbr, Integer docksNr, Integer bikesNr){
        _docksAvailability.setLeft(abbr + "/docksAvailability");
        _requisitions = docksNr - bikesNr;
        _deliveries = 0;
        Boolean[] docks = new Boolean[docksNr];
        for(int i = 0; i < (docksNr - bikesNr); i++){
            docks[i] = true;
        }
        _docksAvailability.setRight(docks);
    }

    /*public ArrayList<Boolean> getDocksAvailability(){
        return _docksAvailability.getRight();
    }*/

    public Boolean[] getDocksAvailability(){
        return _docksAvailability.getRight();
    }

    public void setDockAvailability(Integer dockNr){
        //Boolean availability = _docksAvailability.getRight().get(dockNr);
        Boolean availability = _docksAvailability.getRight()[dockNr];
        _docksAvailability.getRight()[dockNr] = !availability;
    }

    public Integer getAvailableBikesNr(){
        Integer count = 0;
        //List<Boolean> availability = _docksAvailability.getRight();
        Boolean[] availability = _docksAvailability.getRight();
        for (Boolean bool: availability) {
            if (bool == null) count++;
        }
        return count;
    }

    public void addDeliveries(){
        _deliveries++;
    }

    public void addRequisitions(){
        _requisitions++;
    }

    public Integer getDeliveries() {
        return _deliveries;
    }

    public Integer getRequisitions() {
        return _requisitions;
    }
}
package pt.tecnico.rec;

import java.util.*;
import org.apache.commons.lang3.tuple.MutablePair;

public class MutableStation{

    private static MutablePair<String, ArrayList<Boolean>> _docksAvailability = new MutablePair<String, ArrayList<Boolean>>();
    //false = no bike in the dock
    private Integer _deliveries;
    private Integer _requisitions;

    public MutableStation(String abbr){
        _docksAvailability.setLeft(abbr + "/docksAvailability");
        _docksAvailability.setRight(new ArrayList<Boolean>());
        _deliveries = 0;
        _requisitions = 0;
    }

    public ArrayList<Boolean> getDocksAvailability(){
        return _docksAvailability.getRight();
    }

    public void setDockAvailability(Integer dockNr){
        Boolean availability = _docksAvailability.getRight().get(dockNr);
        _docksAvailability.getRight().set(dockNr, !availability);
    }

    public Integer getAvailableBikesNr(){
        Integer count = 0;
        List<Boolean> availability = _docksAvailability.getRight();
        for (Boolean bool: availability) {
            if (bool) count++;
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
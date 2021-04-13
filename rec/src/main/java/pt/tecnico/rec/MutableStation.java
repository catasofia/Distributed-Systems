package pt.tecnico.rec;

import java.util.*;
import org.apache.commons.lang3.tuple.MutablePair;

public class MutableStation{

    private Integer _deliveries;
    private Integer _requisitions;
    private Integer _docks;
    private Integer _bikes;


    public MutableStation(String abbr){
        _deliveries = 0;
        _requisitions = 0;
        _docks = 0;
        _bikes = 0;
    }

    public MutableStation(String abbr, Integer docksNr, Integer bikesNr){
        _requisitions = docksNr - bikesNr;
        _deliveries = 0;
        _docks = docksNr;
        _bikes = bikesNr;
    }


    public Integer getAvailableBikesNr(){
        return _bikes;
    }

    public void bikeDown(){
        _bikes++;
        _deliveries++;
    }

    public void bikeUp(){
        _requisitions++;
        _bikes--;
    }

    public Integer getDeliveries() {
        return _deliveries;
    }

    public Integer getRequisitions() {
        return _requisitions;
    }
}
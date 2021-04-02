package pt.tecnico.rec;

import java.util.*;
import org.apache.commons.lang3.tuple.MutablePair;

public class MutableStation{

    private static MutablePair<String, ArrayList<Boolean>> _docksAvailability = new MutablePair<String, ArrayList<Boolean>>();

    public MutableStation(String abbr){
        _docksAvailability.setLeft(abbr);
        _docksAvailability.setRight(new ArrayList<Boolean>());
    }

    public ArrayList<Boolean> getDocksAvailability(){
        return _docksAvailability.getRight();
    }

    public void setDockAvailability(Integer dockNr){
        Boolean availability = _docksAvailability.getRight().get(dockNr);
        _docksAvailability.getRight().set(dockNr, !availability);
    }
}
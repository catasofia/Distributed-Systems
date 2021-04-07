package pt.tecnico.bicloin.hub;

import pt.ulisboa.tecnico.sdis.zk.*;
import pt.tecnico.bicloin.hub.exceptions.BadEntrySpecificationException;
import org.apache.commons.lang3.tuple.MutablePair;
import pt.tecnico.rec.MutableStation;
import pt.tecnico.bicloin.hub.Station;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import io.grpc.*;


public class HubServerImplOperations {
    Map <String, Station> stations = new HashMap<>();
    Map <String, MutableStation> mutableStations = new HashMap<>();

    public HubServerImplOperations() {}

    public synchronized String ping(String ping) throws BadEntrySpecificationException{
        if (ping == null || ping.isBlank()){
            throw new BadEntrySpecificationException("Error ping: null or empty");
        }
        return ping;
    }

    public synchronized String sys_status(String sysStatus) throws BadEntrySpecificationException{
        if(sysStatus == null || sysStatus.isBlank()){
            throw new BadEntrySpecificationException("Error system status: null or empty");
        }
        return sysStatus;
    }

    public synchronized List<String> info_station(String abbr) throws BadEntrySpecificationException{
        if(stations.get(abbr) == null){
            throw new BadEntrySpecificationException("Error: there is no station with abbr:" + abbr);
        }

        Station station = stations.get(abbr);
        MutableStation mStation = mutableStations.get(abbr);
        List<String> result = new ArrayList<>();
        result.add(station.getName());
        result.add(String.valueOf(station.getLatitude()));
        result.add(String.valueOf(station.getLongitude()));
        result.add(String.valueOf(station.getDocksNr()));
        result.add(String.valueOf(station.getPrize()));
        result.add(String.valueOf(mStation.getAvailableBikesNr()));
        result.add(String.valueOf(mStation.getDeliveries() + mStation.getRequisitions()));

        return result;
    }
}
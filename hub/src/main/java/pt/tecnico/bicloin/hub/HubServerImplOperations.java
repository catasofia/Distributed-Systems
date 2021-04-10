package pt.tecnico.bicloin.hub;

import pt.ulisboa.tecnico.sdis.zk.*;
import pt.tecnico.bicloin.hub.exceptions.BadEntrySpecificationException;
import org.apache.commons.lang3.tuple.MutablePair;
import pt.tecnico.rec.MutableStation;
import pt.tecnico.bicloin.hub.Station;
import pt.tecnico.bicloin.hub.HubFrontend;

import java.util.*;

import io.grpc.*;


public class HubServerImplOperations {
    private Map <String, Station> stations = new HashMap<>();
    private HubFrontend hub = new HubFrontend();

    public HubServerImplOperations() {
        stations = HubMain.getStations();
    }

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
        List<String> result = new ArrayList<>();
        result.add(station.getName());
        result.add("lat " + String.valueOf(station.getLatitude()));
        result.add(String.valueOf(station.getLongitude()) + " long");
        result.add(String.valueOf(station.getDocksNr()));
        result.add(String.valueOf(station.getPrize()));

        return result;
    }

    public synchronized String locate_station(Double lat, Double longt, Integer k){
        Map <String, Double> stationsDistance = new HashMap<>();
        Map <String, Double> sortedMap = new HashMap<>();
        for (Station station : stations.values()){
            stationsDistance.put(station.getAbbr(), station.calculateDistance(lat, longt));
        }
        stationsDistance.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        StringBuilder result = new StringBuilder();
        Set<String> abbrs = sortedMap.keySet();
        String[] ordered = new String[abbrs.size()];
        abbrs.toArray(ordered);

        for(int i = 0; i < k; i++){
            result.append(ordered[i]);
            result.append("\n");
        }
        return result.toString().replaceFirst("[\n\r]+$", ""); //removes last \n
    }

    public synchronized String balance(String name){
        return hub.balance(name);
    }
}
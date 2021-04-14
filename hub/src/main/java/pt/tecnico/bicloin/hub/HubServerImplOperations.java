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
    private Map <String, Station> stations;
    private Map <String, User> users;
    private HubFrontend hub = new HubFrontend();

    public HubServerImplOperations() {
        stations = HubMain.getStations();
        users = HubMain.getUsers();
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
        result.add(String.valueOf(station.getLatitude()));
        result.add(String.valueOf(station.getLongitude()));
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
        return name + "/balance";
    }

    public synchronized String topUp(String name, Integer amount, String phone) throws BadEntrySpecificationException{
   
        if (!(users.get(name).getPhone().equals(phone))){
            throw new BadEntrySpecificationException("O número de telemóvel não corresponde ao utilizador");
        }
        if(amount < 1 || amount > 20){
            throw new BadEntrySpecificationException("Só se pode carregar com valores entre 1 EUR e 20 EUR, inclusive.");
        }
        return name+"/top_up "+amount;
    }

    public synchronized String bikeUp(String name, Double latitude, Double longitude, String abbr) throws BadEntrySpecificationException{
        Station station = stations.get(abbr);
        if (station.calculateDistance(latitude, longitude) < 200){
            return abbr+"/bike_up " + name;
        }else{
            throw new BadEntrySpecificationException("Fora de alcance");
        }
    }

    public synchronized String bikeDown(String name, Double latitude, Double longitude, String abbr) throws BadEntrySpecificationException{
        Station station = stations.get(abbr);
        if (station.calculateDistance(latitude, longitude) < 200){
            return abbr+"/bike_down " + name + " " + station.getPrize();
        }else{
            throw new BadEntrySpecificationException("Fora de alcance");
        }
    }
}
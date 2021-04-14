package pt.tecnico.bicloin.hub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.bicloin.hub.grpc.Hub;
import pt.tecnico.bicloin.hub.grpc.HubServiceGrpc;
import pt.tecnico.rec.MutableStation;
import pt.tecnico.rec.RecFrontend;
import pt.tecnico.rec.exceptions.BadEntrySpecificationException;
import pt.tecnico.rec.grpc.Rec;
import pt.tecnico.rec.grpc.RecordServiceGrpc;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;
//import pt.tecnico.bicloin.hub.Station;

import java.io.IOException;
import java.util.*;

public class HubFrontend{

    private String path = "/grpc/bicloin/hub";
    private static List<HubServiceGrpc.HubServiceBlockingStub> stubs = new ArrayList<>();
    private static RecFrontend rec = new RecFrontend();

    public HubFrontend(){}

    public List<ManagedChannel> createChannels(String host, String port) throws ZKNamingException, IOException, InterruptedException {
        ZKNaming zkNaming = new ZKNaming(host, port);
        Collection<ZKRecord> records = zkNaming.listRecords(path);
        List<ManagedChannel> channels = new ArrayList<>();
        for(ZKRecord zkRecord: records) {
            String target = zkRecord.getURI(); //host:port
            System.out.println(target);
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            channels.add(channel);
            stubs.add(HubServiceGrpc.newBlockingStub(channel));
        }
        rec.createChannel(host, port);
        return channels;
    }

    public static String ctrlPing(String ping){
        Hub.CtrlPingRequest pingRequest = Hub.CtrlPingRequest.newBuilder().setInput(ping).build();
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;
        Hub.CtrlPingResponse pingResponse = stubs.get(result).ctrlPing(pingRequest);

        return pingResponse.getOutput();

    }

    public String sys_status(String status){
        String result = "";
        int x = 1;
        for (HubServiceGrpc.HubServiceBlockingStub stub: stubs){
            try{
            Hub.CtrlPingRequest pingRequest = Hub.CtrlPingRequest.newBuilder().setInput(status).build();
            stub.ctrlPing(pingRequest);
            result = result + "/grpc/bicloin/hub/" + x + " up\n";
            } catch (StatusRuntimeException e) {
                result = result + "/grpc/bicloin/hub/" + x + " down\n";
            }
            finally {
                x++;
            }
        }

        try{
            rec.ctrlPing(status);
            result = result + "/grpc/bicloin/rec/1 up\n";
        } catch(StatusRuntimeException e) {
            result = result + "/grpc/bicloin/rec/1 down\n";
        }
        return result;
    }

    public static String info_station(String abbr){

        //TODO erro e teste correspondente
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;
        Hub.InfoStationRequest infoRequest = Hub.InfoStationRequest.newBuilder().setAbbr(abbr).build();
        Hub.InfoStationResponse infoResponse = stubs.get(result).infoStation(infoRequest);

        String finalResult = "";

        finalResult += infoResponse.getName();
        finalResult = finalResult + ", lat " + infoResponse.getLatitude();
        finalResult = finalResult + ", " + infoResponse.getLongitude() + " long, ";
        finalResult = finalResult + infoResponse.getDocksNr() + " docas, ";
        finalResult = finalResult + infoResponse.getPrize() + " BIC prémio,";

        finalResult = finalResult + " " + rec.info_station(abbr) + "https://www.google.com/maps/place/"
                + infoResponse.getLatitude() + "," + infoResponse.getLongitude();

        return finalResult;
    }

    public String scan(String abbr, Double latitude, Double longitude){
        String info = info_station(abbr);
        String[] attributes = info.split(",");

        attributes[1] = attributes[1].substring(5);
        attributes[2] = attributes[2].substring(1, attributes[2].length() - 5);

        String[] splitedResult= info.split("lat");
        String[] result = splitedResult[1].split("bicicletas");
        Double distance = calculateDistance(latitude, longitude, Double.parseDouble(attributes[1]),
                Double.parseDouble(attributes[2]));
        return "lat" + result[0] + "bicicletas, a " + (int)Math.round(distance) + " metros";
    }

    public Double calculateDistance(Double lat1, Double long1, Double lat2, Double long2){
        Integer earthRadius = 6371000;

        Double dLat = Math.toRadians((lat1 - lat2));
        Double dLong = Math.toRadians((long1 - long2));

        Double startLat = Math.toRadians(lat1);
        Double endLat = Math.toRadians(lat2);

        Double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

    public Double haversin(Double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public static String locate_station(Double lat, Double longt, Integer k){
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;
        Hub.LocateStationRequest locateRequest = Hub.LocateStationRequest.newBuilder().setLatitude(lat)
                .setLongitude(longt).setK(k).build();
        Hub.LocateStationResponse locateResponse = stubs.get(result).locateStation(locateRequest);
        return locateResponse.getAbbrs();
    }

    public static String balance(String name){
        Hub.BalanceRequest request = Hub.BalanceRequest.newBuilder().setName(name).build();
        String value = stubs.get(0).balance(request).getBalance();
        return rec.balance(value);
    }

    public static String topUp(String name, Integer amount, String phone){
        Hub.TopUpRequest request = Hub.TopUpRequest.newBuilder().setName(name)
                .setAmount(amount).setPhone(phone).build();
        String value = stubs.get(0).topUp(request).getBalance();
        return rec.topUp(value);
    }

    public static void bikeUp(String name, Double latitude, Double longitude, String abbr){
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;
        Hub.BikeUpRequest bikeUpRequest = Hub.BikeUpRequest.newBuilder().setName(name).setLatitude(latitude)
                .setLongitude(longitude).setAbbr(abbr).build();
        String value = stubs.get(result).bikeUp(bikeUpRequest).getResponse();

        rec.bikeUp(value);
    }

    public static void bikeDown(String name, Double latitude, Double longitude, String abbr){
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;
        Hub.BikeDownRequest bikeDownRequest = Hub.BikeDownRequest.newBuilder().setName(name).setLatitude(latitude)
                .setLongitude(longitude).setAbbr(abbr).build();
        String value = stubs.get(result).bikeDown(bikeDownRequest).getResponse();

        rec.bikeDown(value);
    }
}
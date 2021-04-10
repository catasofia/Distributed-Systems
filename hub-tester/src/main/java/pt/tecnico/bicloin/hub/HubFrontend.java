package pt.tecnico.bicloin.hub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.bicloin.hub.grpc.Hub;
import pt.tecnico.bicloin.hub.grpc.HubServiceGrpc;
import pt.tecnico.rec.MutableStation;
import pt.tecnico.rec.RecFrontend;
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
    private RecFrontend rec = new RecFrontend();

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

    public static String ctrlPing(String ping) throws StatusRuntimeException{
        try{
            Hub.CtrlPingRequest pingRequest = Hub.CtrlPingRequest.newBuilder().setInput(ping).build();
            Random r = new Random();
            int low = 0;
            int high = stubs.size();
            int result = r.nextInt(high - low) + low;
            Hub.CtrlPingResponse pingResponse = stubs.get(result).ctrlPing(pingRequest);

            return pingResponse.getOutput();

        }catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " +
                    e.getStatus().getDescription());
        }
        return "";
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

        RecordServiceGrpc.RecordServiceBlockingStub recStub = rec.getStub();

        try{
            rec.ctrlPing(status);
            result = result + "/grpc/bicloin/rec/1 up\n";
        } catch(StatusRuntimeException e) {
            result = result + "/grpc/bicloin/rec/1 down\n";
        }
        return result;
    }

    public String info_station(String abbr){
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;
        Hub.InfoStationRequest infoRequest = Hub.InfoStationRequest.newBuilder().setAbbr(abbr).build();
        Hub.InfoStationResponse infoResponse = stubs.get(result).infoStation(infoRequest);

        String finalResult = "";

        finalResult += infoResponse.getName();
        finalResult = finalResult + " " + infoResponse.getLatitude();
        finalResult = finalResult + " " + infoResponse.getLongitude();
        finalResult = finalResult + " " + infoResponse.getDocksNr();
        finalResult = finalResult + " " + infoResponse.getPrize();

        finalResult = finalResult + " " + rec.info_station(abbr);


        return finalResult;
    }

    public String locate_station(Double lat, Double longt, Integer k){
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;
        Hub.LocateStationRequest locateRequest = Hub.LocateStationRequest.newBuilder().setLatitude(lat)
                .setLongitude(longt).setK(k).build();
        Hub.LocateStationResponse locateResponse = stubs.get(result).locateStation(locateRequest);
        return locateResponse.getAbbrs();
    }

    public String balance(String name){
        return rec.balance(name);
    }
}
package pt.tecnico.rec;

import pt.ulisboa.tecnico.sdis.zk.ZKRecord;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import io.grpc.*;
import pt.tecnico.rec.grpc.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RecFrontend{

    private String path = "/grpc/bicloin/rec";
    private static List<RecordServiceGrpc.RecordServiceBlockingStub> stubs = new ArrayList<>();

    public RecFrontend(){}

    public List<ManagedChannel> createChannels(String host, String port) throws ZKNamingException, IOException, InterruptedException {
        ZKNaming zkNaming = new ZKNaming(host, port);
        Collection<ZKRecord> records = zkNaming.listRecords(path);
        List<ManagedChannel> channels = new ArrayList<>();
        for(ZKRecord record: records) {
            String target = record.getURI(); //host:port
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            channels.add(channel);
            stubs.add(RecordServiceGrpc.newBlockingStub(channel));
        }
        return channels;
    }

    public List<RecordServiceGrpc.RecordServiceBlockingStub> getStub(){
        return stubs;
    }

    public static String ctrlPing(String ping){
        Rec.CtrlPingRequest pingRequest = Rec.CtrlPingRequest.newBuilder().setInput(ping).build();

        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        Rec.CtrlPingResponse pingResponse = stubs.get(result).ctrlPing(pingRequest);

        return pingResponse.getOutput();
    }

    public static String info_station(String abbr){
        Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(abbr+"/info").build();

        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        return stubs.get(result).read(readRequest).getValue();
    }

    public static String balance(String input){
        Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(input).build();

        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        return stubs.get(result).read(readRequest).getValue();
    }

    public static String topUp(String name){
        Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();

        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        return stubs.get(result).write(writeRequest).getValue();
    }

    public static String bikeUp(String name){
        Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();

        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        return stubs.get(result).write(writeRequest).getValue();
    }

    public static String bikeDown(String name){
        Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();

        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        return stubs.get(result).write(writeRequest).getValue();
    }
}
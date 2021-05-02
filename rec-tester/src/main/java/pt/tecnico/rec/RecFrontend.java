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

        System.out.println("Conectei-me à réplica: " + (result+1) + " no localhost 809" + (result+1));

        Rec.CtrlPingResponse pingResponse = stubs.get(result).ctrlPing(pingRequest);

        return pingResponse.getOutput();
    }

    public static String info_station(String abbr){
        Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(abbr+"/info").build();

        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        System.out.println("Conectei-me à réplica: " + (result+1) + " no localhost 809" + (result+1));


        return stubs.get(result).read(readRequest).getValue();
    }

    public static String balance(String input){
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;


        try {
            Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(input).build();
            Rec.ReadResponse readResponse = stubs.get(result).read(readRequest);
            System.out.println("Conectei-me à réplica: " + (result+1) + " no localhost 809" + (result+1));
            return readResponse.getValue();
        } catch (StatusRuntimeException e) {
            return "Tentei conectar-me à réplica: " + (result + 1) + " e falhei! ";
        }
    }

    public static String topUp(String name){
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        try {
            Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
            Rec.WriteResponse writeResponse = stubs.get(result).write(writeRequest);
            System.out.println("Conectei-me à réplica: " + (result + 1) + " no localhost 809" + (result + 1));
            Rec.UpdateRequest updateRequest = Rec.UpdateRequest.newBuilder().setInput(name).build();
            stubs.get(result).update(updateRequest);
            return writeResponse.getValue();
        } catch (StatusRuntimeException e) {
            return "Tentei conectar-me à réplica: " + (result + 1) + " e falhei! ";
        }
    }

    public static String bikeUp(String name){
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        try {
            Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
            Rec.WriteResponse writeResponse = stubs.get(result).write(writeRequest);
            System.out.println("Conectei-me à réplica: " + (result + 1) + " no localhost 809" + (result + 1));
            Rec.UpdateRequest updateRequest = Rec.UpdateRequest.newBuilder().setInput(name).build();
            stubs.get(result).update(updateRequest);
            return writeResponse.getValue();
        } catch (StatusRuntimeException e) {
            return "Tentei conectar-me à réplica: " + (result + 1) + " e falhei! ";
        }
    }

    public static String bikeDown(String name){
        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        try {
            Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
            System.out.println("Conectei-me à réplica: " + (result + 1) + " no localhost 809" + (result + 1));
            Rec.WriteResponse writeResponse = stubs.get(result).write(writeRequest);
            Rec.UpdateRequest updateRequest = Rec.UpdateRequest.newBuilder().setInput(name).build();
            stubs.get(result).update(updateRequest);
            return writeResponse.getValue();
        } catch (StatusRuntimeException e) {
            return "Tentei conectar-me à réplica: " + (result + 1) + " e falhei! ";
        }
    }
}
package pt.tecnico.rec;

import pt.tecnico.rec.exceptions.BadEntrySpecificationException;
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
import java.util.concurrent.TimeUnit;

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

    public static Rec.ReadResponse checkTags(String input){
        Integer tag = 0;
        Integer id = 0;

        Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(input).build();
        Rec.ReadResponse readResponse = null;
        Rec.ReadResponse readResponseFinal = null;

        for(RecordServiceGrpc.RecordServiceBlockingStub stub: stubs){
            try {
                readResponse  = stub.withDeadlineAfter(2000, TimeUnit.MILLISECONDS).read(readRequest);
                if(readResponse.getVersion() >= tag) {
                    tag = readResponse.getVersion();
                    readResponseFinal = readResponse;
                    id = readResponse.getRid();

                }
            } catch (StatusRuntimeException e) {
                if (Status.DEADLINE_EXCEEDED.getCode() == e.getStatus().getCode()) {
                    continue;
                }
            }
        }
        System.out.println("Conectei-me à réplica " + id + " no localhost 809" + id);
        return readResponseFinal;
    }

    public static String ctrlPing(String ping){
        Rec.CtrlPingRequest pingRequest = Rec.CtrlPingRequest.newBuilder().setInput(ping).build();
        StringBuilder result = new StringBuilder();
        Integer i = 1;
        for(RecordServiceGrpc.RecordServiceBlockingStub stub: stubs) {
            try {
                Rec.CtrlPingResponse pingResponse = stub.ctrlPing(pingRequest);
                result.append("/grpc/bicloin/rec/" + i + " up\n");
                i++;
            } catch (StatusRuntimeException e) {
                result.append("/grpc/bicloin/rec/" + i + " down\n");
                i++;
            }
        }
        return result.toString().replaceAll("[\n\r]$", ""); //removes the last \n;
    }

    public static String info_station(String abbr){
        return checkTags(abbr+"/info").getValue();
    }

    public static String balance(String input){
        return checkTags(input).getValue();
    }

    public static String topUp(String name){
        String[] attributes = name.split("/");
        Integer id = checkTags(attributes[0] +"/balance").getRid();
        try {
            Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
            Rec.WriteResponse writeResponse = stubs.get(id-1).withDeadlineAfter(2000, TimeUnit.MILLISECONDS).write(writeRequest);
            String value = writeResponse.getValue();
            Integer newTag = writeResponse.getTag();
            Rec.UpdateRequest updateRequest = Rec.UpdateRequest.newBuilder()
                    .setInput("update:" + attributes[0]+":top_up:" +  value + ":" + newTag)
                    .build();
            //id-1 because replicas starts in 1, but they are recorded in a list, that starts in 0
            stubs.get(id-1).update(updateRequest);
            return value;
        } catch (StatusRuntimeException e) {
            if(Status.DEADLINE_EXCEEDED.getCode() == e.getStatus().getCode()){
                System.out.println("ERRO: Tempo de espera excedido. Tente outra vez!");
            }
        }
        return "";
    }

    public static String getUser(String name, Integer id){
        Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName("user/" + name).build();
        //id-1 because replicas starts in 1, but they are recorded in a list, that starts in 0
        Rec.ReadResponse readResponse = stubs.get(id-1).withDeadlineAfter(2000, TimeUnit.MILLISECONDS).read(readRequest);
        return readResponse.getValue();
    }

    public static String bikeUp(String name){
        //name = abbr/bike_up user
        String[] attributes = name.split("/");
        Integer id = checkTags(attributes[0] + "/info").getRid();

        //attributes[1] = bike_up user
        String[] method = attributes[1].split(" ");

        try {
            Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
            //id-1 because replicas starts in 1, but they are recorded in a list, that starts in 0
            Rec.WriteResponse writeResponse = stubs.get(id-1).withDeadlineAfter(2000, TimeUnit.MILLISECONDS).write(writeRequest);
            String value = writeResponse.getValue();
            Integer newTag = writeResponse.getTag();
            String stateBalance = getUser(method[1], id);
            Rec.UpdateRequest updateRequest = Rec.UpdateRequest.newBuilder()
                    .setInput("update:" + method[0] + ":" + attributes[0] + ":" + value + ":" + newTag + ":" + method[1] +
                            ":" + stateBalance).build();
            //id-1 because replicas starts in 1, but they are recorded in a list, that starts in 0
            stubs.get(id-1).update(updateRequest);
            return writeResponse.getValue();
        } catch (IllegalThreadStateException e) {
            System.out.println("Tentei conectar-me à réplica " + id + " e falhei! ");
        }
        return "";
    }

    public static String bikeDown(String name){
        //name = abbr/bike_down user
        String[] attributes = name.split("/");
        Integer id = checkTags(attributes[0] + "/info").getRid();

        //attributes[1] = bike_down user
        String[] method = attributes[1].split(" ");

        try {
            Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
            //id-1 because replicas starts in 1, but they are recorded in a list, that starts in 0
            Rec.WriteResponse writeResponse = stubs.get(id-1).withDeadlineAfter(2000, TimeUnit.MILLISECONDS).write(writeRequest);
            String value = writeResponse.getValue();
            Integer newTag = writeResponse.getTag();
            String stateBalance = getUser(method[1], id);
            Rec.UpdateRequest updateRequest = Rec.UpdateRequest.newBuilder()
                    .setInput("update:" + method[0] + ":" + attributes[0] + ":" + value + ":" + newTag + ":" + method[1] +
                            ":" + stateBalance).build();
            //id-1 because replicas starts in 1, but they are recorded in a list, that starts in 0
            stubs.get(id-1).update(updateRequest);
            return writeResponse.getValue();
        } catch (IllegalThreadStateException e) {
            System.out.println("Tentei conectar-me à réplica " + id + " e falhei! ");
        }
        return "";
    }
}
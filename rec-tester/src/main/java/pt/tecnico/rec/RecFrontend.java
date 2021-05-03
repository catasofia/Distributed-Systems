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

    public List<RecordServiceGrpc.RecordServiceBlockingStub> getStub(){
        return stubs;
    }

   /* public Integer getUpdatedStub(Integer id, String input) {
        RecordServiceGrpc.RecordServiceBlockingStub stub = stubs.get(id).getStub();
        Rec.getStubRequest stubRequest = Rec.getStubRequest.newBuilder().setInput(input).build();

        Rec.getStubResponse stubResponse = stubs.get(id).getStub(stubRequest);

        return stubResponse.getId();
    }*/
    public static Rec.ReadResponse checkTags(String input){
        Integer tag = 0;
        String value = "";
        Integer id = 0;
        Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(input).build();
        Rec.ReadResponse readResponse = null;

        for(RecordServiceGrpc.RecordServiceBlockingStub stub: stubs){
            try {
                readResponse  = stub.withDeadlineAfter(2000, TimeUnit.MILLISECONDS).read(readRequest);
                if(readResponse.getVersion() >= tag) {
                    tag = readResponse.getVersion();
                    value = readResponse.getValue();
                    id = readResponse.getRid();
                }
            } catch (StatusRuntimeException e) {
                if (Status.DEADLINE_EXCEEDED.getCode() == e.getStatus().getCode()) {
                    //System.out.println("ERRO: Tempo de espera excedido. Tente outra vez!");
                    continue;
                }
            }
        }
        System.out.println("Conectei-me à réplica " + id + " no localhost 809" + id);
        return readResponse;
    }

    public static String ctrlPing(String ping){
        Rec.CtrlPingRequest pingRequest = Rec.CtrlPingRequest.newBuilder().setInput(ping).build();

        Random r = new Random();
        int low = 0;
        int high = stubs.size();
        int result = r.nextInt(high - low) + low;

        System.out.println("Conectei-me à réplica " + (result+1) + " no localhost 809" + (result+1));

        Rec.CtrlPingResponse pingResponse = stubs.get(result).ctrlPing(pingRequest);

        return pingResponse.getOutput();
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
            Rec.UpdateRequest updateRequest = Rec.UpdateRequest.newBuilder().setInput(name).build();
            stubs.get(id-1).update(updateRequest);
            return writeResponse.getValue();
        } catch (StatusRuntimeException e) {
            if(Status.DEADLINE_EXCEEDED.getCode() == e.getStatus().getCode()){
                System.out.println("ERRO: Tempo de espera excedido. Tente outra vez!");
            }
        }
        return "";
    }

    public static String bikeUp(String name){
        String[] attributes = name.split(" ");
        Integer id = checkTags(attributes[1] + "/info").getRid();

        try {
            Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
            Rec.WriteResponse writeResponse = stubs.get(id-1).withDeadlineAfter(2000, TimeUnit.MILLISECONDS).write(writeRequest);
            Rec.UpdateRequest updateRequest = Rec.UpdateRequest.newBuilder().setInput(name).build();
            stubs.get(id-1).update(updateRequest);
            return writeResponse.getValue();
        } catch (IllegalThreadStateException e) {
            System.out.println("Tentei conectar-me à réplica " + id + " e falhei! ");
        }
        return "";
    }

    public static String bikeDown(String name){
        String[] attributes = name.split(" ");
        Integer id = checkTags(attributes[1] + "/info").getRid();

        try {
            Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
            Rec.WriteResponse writeResponse = stubs.get(id-1).withDeadlineAfter(2000, TimeUnit.MILLISECONDS).write(writeRequest);
            Rec.UpdateRequest updateRequest = Rec.UpdateRequest.newBuilder().setInput(name).build();
            stubs.get(id-1).update(updateRequest);
            return writeResponse.getValue();
        } catch (IllegalThreadStateException e) {
            System.out.println("Tentei conectar-me à réplica " + id + " e falhei! ");
        }
        return "";
    }
}
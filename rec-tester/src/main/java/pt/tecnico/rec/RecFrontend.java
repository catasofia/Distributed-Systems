package pt.tecnico.rec;

import pt.ulisboa.tecnico.sdis.zk.ZKRecord;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import io.grpc.*;
import pt.tecnico.rec.grpc.*;
import java.io.IOException;

public class RecFrontend{

    private String path = "/grpc/bicloin/rec/1";
    private static RecordServiceGrpc.RecordServiceBlockingStub stub;

    public RecFrontend(){}

    public ManagedChannel createChannel(String host, String port) throws ZKNamingException, IOException, InterruptedException {
        ZKNaming zkNaming = new ZKNaming(host, port);
        ZKRecord zkRecord = zkNaming.lookup(path);
        String target = zkRecord.getURI(); //host:port
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        stub = RecordServiceGrpc.newBlockingStub(channel);
        return channel;
    }

    public RecordServiceGrpc.RecordServiceBlockingStub getStub(){
        return stub;
    }

    public static String ctrlPing(String ping){
        Rec.CtrlPingRequest pingRequest = Rec.CtrlPingRequest.newBuilder().setInput(ping).build();
        Rec.CtrlPingResponse pingResponse = stub.ctrlPing(pingRequest);

        return pingResponse.getOutput();
    }

    public static String info_station(String abbr){
        Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(abbr+"/info").build();
        return stub.read(readRequest).getValue();
    }

    public static String balance(String input){
        Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(input).build();
        return stub.read(readRequest).getValue();
    }

    public static String topUp(String name){
        Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
        return stub.write(writeRequest).getValue();
    }

    public static String bikeUp(String name){
        Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
        return stub.write(writeRequest).getValue();
    }

    public static String bikeDown(String name){
        Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(name).build();
        return stub.write(writeRequest).getValue();
    }
}
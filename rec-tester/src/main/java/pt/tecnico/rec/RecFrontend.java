package pt.tecnico.rec;

import pt.ulisboa.tecnico.sdis.zk.ZKRecord;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import io.grpc.*;
import pt.tecnico.rec.grpc.RecordServiceGrpc;
import java.io.IOException;

public class RecFrontend{

    private String path = "/grpc/bicloin/rec/1";
    private RecordServiceGrpc.RecordServiceBlockingStub stub;

    public RecFrontend(){}

    public ManagedChannel createChannel(String host, String port) throws ZKNamingException, IOException, InterruptedException {
        ZKNaming zkNaming = new ZKNaming(host, port);
        ZKRecord zkRecord = zkNaming.lookup(path);
        String target = zkRecord.getURI(); //path:port
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        stub = RecordServiceGrpc.newBlockingStub(channel);

        return channel;
    }
}
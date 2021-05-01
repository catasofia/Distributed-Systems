package pt.tecnico.rec;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.tecnico.rec.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.nio.file.FileSystemNotFoundException;
import java.util.*;

public class ReplicaManager {
    private List<MutableUser> users = new ArrayList<>();
    private List<MutableStation> stations = new ArrayList<>();

    private String path;

    private Map<String, ManagedChannel> channels = new HashMap<>();
    private Map<String, RecordServiceGrpc.RecordServiceBlockingStub> stubs = new HashMap<>();

    private ZKNaming zkNaming;


    public ReplicaManager(String zooHost, String zooPort, String path) {
        zkNaming = new ZKNaming(zooHost, zooPort);
        this.path = path;
    }

    public void connect() throws ZKNamingException {
        Collection<ZKRecord> records = zkNaming.listRecords(path);
        for(ZKRecord record: records){
            String target = record.getURI();
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

            channels.putIfAbsent(record.getPath(), channel);
            stubs.putIfAbsent(record.getPath(), RecordServiceGrpc.newBlockingStub(channel));
        }
    }

    public void update(String changed) {
        try {
            connect();
        } catch (ZKNamingException e){
            System.out.println("erro");
        }
        System.out.println(changed);
        for(RecordServiceGrpc.RecordServiceBlockingStub stub: stubs.values()){
            Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(changed).build();
            stub.write(writeRequest);
        }
    }

}

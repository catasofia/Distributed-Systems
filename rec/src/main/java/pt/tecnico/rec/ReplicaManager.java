package pt.tecnico.rec;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.tecnico.rec.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.nio.file.FileSystemNotFoundException;
import java.util.*;

public class ReplicaManager {
    private List<MutableUser> users = new ArrayList<>();
    private List<MutableStation> stations = new ArrayList<>();

    private static String path;
    private static Integer id;

    private Map<String, ManagedChannel> channels = new HashMap<>();
    private Map<String, RecordServiceGrpc.RecordServiceBlockingStub> stubs = new HashMap<>();

    private ZKNaming zkNaming;


    public ReplicaManager(String zooHost, String zooPort, String path, Integer id) {
        zkNaming = new ZKNaming(zooHost, zooPort);
        this.path = path;
        this.id = id;
    }

    public static Integer getId(){
        return id;
    }

    public void connect() throws ZKNamingException {
        Collection<ZKRecord> records = zkNaming.listRecords(path);
        for(ZKRecord record: records){
            if(record.getPath().equals(path + "/" + id)) continue;
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
            e.printStackTrace();
        }
        for(RecordServiceGrpc.RecordServiceBlockingStub stub: stubs.values()){
            try {
                Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(changed).build();
                stub.write(writeRequest);
            }catch (StatusRuntimeException e) {
                continue;
            }
        }
    }

    public void initializeReplicasStations(String abbr, int docks, int bikes) {
        try {
            connect();
        } catch (ZKNamingException e){
            e.printStackTrace();
        }
        for(RecordServiceGrpc.RecordServiceBlockingStub stub: stubs.values()){
            Rec.initializeRequest initializeRequest = Rec.initializeRequest.newBuilder().setAbbr(abbr).setDocks(docks).setBikes(bikes).build();
            stub.initialize(initializeRequest);
        }
    }

    public RecordServiceGrpc.RecordServiceBlockingStub checkTags(String input){
        try {
            connect();
        } catch (ZKNamingException e){
            e.printStackTrace();
        }

        Integer tag = 0;
        RecordServiceGrpc.RecordServiceBlockingStub updatedStub = null;
        for(RecordServiceGrpc.RecordServiceBlockingStub stub: stubs.values()){
            Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(input).build();
            Rec.ReadResponse response = stub.read(readRequest);
            if(response.getVersion() > tag){
                tag = response.getVersion();
                updatedStub = stub;
            }
        }
        return updatedStub;
    }

}

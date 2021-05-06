package pt.tecnico.rec;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.tecnico.rec.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ReplicaManager {
    private List<MutableUser> users = new ArrayList<>();
    private List<MutableStation> stations = new ArrayList<>();

    private static String path;
    private Integer id;

    private Map<String, ManagedChannel> channels = new HashMap<>();
    private Map<String, RecordServiceGrpc.RecordServiceBlockingStub> stubs = new HashMap<>();
    RecServerImplOperations operations;

    private ZKNaming zkNaming;


    public ReplicaManager(String zooHost, String zooPort, String path, Integer id, RecServerImplOperations ops) {
        zkNaming = new ZKNaming(zooHost, zooPort);
        this.path = path;
        this.id = id;
        operations = ops;
    }

    public Integer getId(){
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
                stub.withDeadlineAfter(2000, TimeUnit.MILLISECONDS).write(writeRequest);
            }catch (StatusRuntimeException e) {
                continue;
            }
        }
    }
}

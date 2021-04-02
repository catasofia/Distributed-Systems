package pt.tecnico.bicloin.hub;

import pt.ulisboa.tecnico.sdis.zk.*;
import io.grpc.*;
import pt.tecnico.bicloin.hub.grpc.HubServiceGrpc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HubFrontend{

    private String path = "/grpc/bicloin/hub/";
    private List<HubServiceGrpc.HubServiceBlockingStub> stubs = new ArrayList<>();

    public HubFrontend(){}

    public List<ManagedChannel> createChannels(String host, String port) throws ZKNamingException, IOException, InterruptedException {
        ZKNaming zkNaming = new ZKNaming(host, port);
        Collection<ZKRecord> records = zkNaming.listRecords(path);
        List<ManagedChannel> channels = new ArrayList<>();

        for(ZKRecord zkRecord: records) {
            String target = zkRecord.getURI(); //host:port
            final ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            channels.add(channel);
            stubs.add(HubServiceGrpc.newBlockingStub(channel));
        }

        return channels;
    }
}
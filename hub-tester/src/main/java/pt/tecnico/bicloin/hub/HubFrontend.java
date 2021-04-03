package pt.tecnico.bicloin.hub;

import pt.ulisboa.tecnico.sdis.zk.*;
import io.grpc.*;
import pt.tecnico.bicloin.hub.grpc.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

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

    public String ctrlPing(String ping){
        try{
            Hub.CtrlPingRequest pingRequest = Hub.CtrlPingRequest.newBuilder().setInput(ping).build();
            Random r = new Random();
            int low = 0;
            int high = stubs.size();
            int result = r.nextInt(high - low) + low;
            Hub.CtrlPingResponse pingResponse = stubs.get(result).ctrlPing(pingRequest);
            
            return pingResponse.getOutput();
            
        }catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }
        return "";
    }
}
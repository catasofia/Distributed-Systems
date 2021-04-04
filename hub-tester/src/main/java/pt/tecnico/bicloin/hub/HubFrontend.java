package pt.tecnico.bicloin.hub;

import pt.tecnico.rec.grpc.Rec;
import pt.tecnico.rec.grpc.RecordServiceGrpc;
import pt.ulisboa.tecnico.sdis.zk.*;
import pt.tecnico.rec.RecFrontend;
import io.grpc.*;
import pt.tecnico.bicloin.hub.grpc.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class HubFrontend{

    private String path = "/grpc/bicloin/hub";
    private static List<HubServiceGrpc.HubServiceBlockingStub> stubs = new ArrayList<>();
    private RecFrontend rec;

    public HubFrontend(){
        rec = new RecFrontend();
    }




    public List<ManagedChannel> createChannels(String host, String port) throws ZKNamingException, IOException, InterruptedException {
        ZKNaming zkNaming = new ZKNaming(host, port);
        Collection<ZKRecord> records = zkNaming.listRecords(path);
        List<ManagedChannel> channels = new ArrayList<>();

        for(ZKRecord zkRecord: records) {
            String target = zkRecord.getURI(); //host:port
            System.out.println(target);
            ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
            channels.add(channel);
            stubs.add(HubServiceGrpc.newBlockingStub(channel));
        }
        return channels;
    }

    public static String ctrlPing(String ping) throws StatusRuntimeException{
        try{
            Hub.CtrlPingRequest pingRequest = Hub.CtrlPingRequest.newBuilder().setInput(ping).build();
            Random r = new Random();
            int low = 0;
            int high = stubs.size();
            int result = r.nextInt(high - low) + low;
            Hub.CtrlPingResponse pingResponse = stubs.get(result).ctrlPing(pingRequest);

            return pingResponse.getOutput();

        }catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description: " +
                    e.getStatus().getDescription());
        }
        return "";
    }

    /*public String sys_status(String status, String host, String port) throws ZKNamingException{
        String result = "";
        for (HubServiceGrpc.HubServiceBlockingStub stub: stubs){
            int x = 1;
            try{
            Hub.CtrlPingRequest pingRequest = Hub.CtrlPingRequest.newBuilder().setInput(status).build();
            stub.ctrlPing(pingRequest);
            result = result + "/grpc/bicloin/hub/" + x + " up\n";
            } catch (StatusRuntimeException e) {
                result = result + "/grpc/bicloin/hub/" + x + " down\n";
            }
        }
        ZKNaming zkNaming = new ZKNaming(host, port);
        ZKRecord recRecord = zkNaming.lookup("/grpc/bicloin/rec/1");
        RecordServiceGrpc.RecordServiceBlockingStub recStub = rec.getStub();
        try{
            Rec.CtrlPingRequest pingRequest = Rec.CtrlPingRequest.newBuilder().setInput(status).build();
            recStub.ctrlPing(pingRequest);
            result = result + "/grpc/bicloin/rec/1 up\n";
        } catch(StatusRuntimeException e) {
            result = result + "/grpc/bicloin/rec/1 down\n";
        }
        return result;
    }*/
}
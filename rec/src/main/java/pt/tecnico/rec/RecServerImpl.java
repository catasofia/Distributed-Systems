package pt.tecnico.rec;

import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.*;
import static io.grpc.Status.INVALID_ARGUMENT;
import pt.tecnico.rec.exceptions.BadEntrySpecificationException;

public class RecServerImpl extends RecordServiceGrpc.RecordServiceImplBase {

    private static RecServerImplOperations operations;
    private ReplicaManager replicaManager;
    private Integer tag;

    public RecServerImpl(String zooHost, String zooPort, Integer id){
        operations = new RecServerImplOperations(zooHost, zooPort);
        replicaManager = new ReplicaManager(zooHost, zooPort, "/grpc/bicloin/rec", id);
        tag = 0;
    }

    public static RecServerImplOperations getRecOperations(){
        return operations;
    }

    @Override
    public void read(Rec.ReadRequest request, StreamObserver<Rec.ReadResponse> responseObserver){
        try{
            String responseInput = operations.read(request.getName());
            Rec.ReadResponse response = Rec.ReadResponse.newBuilder().setValue(responseInput).setVersion(tag).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getEntrySpecification()).asRuntimeException());
        }
    }

    @Override
    public void write(Rec.WriteRequest request, StreamObserver<Rec.WriteResponse> responseObserver){
        try{
            String responseInput = operations.write(request.getName());
            Rec.WriteResponse response = Rec.WriteResponse.newBuilder().setValue(responseInput).build();
            //tag++;
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            System.out.println("Apanhei exceção");
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getEntrySpecification()).asRuntimeException());
        }
    }

    @Override
    public void ctrlPing(Rec.CtrlPingRequest request, StreamObserver<Rec.CtrlPingResponse> responseObserver){
        try{
            String responseInput = operations.ping(request.getInput());
            Rec.CtrlPingResponse response = Rec.CtrlPingResponse.newBuilder().setOutput(responseInput).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getEntrySpecification()).asRuntimeException());
        }
    }

    @Override
    public void initialize(Rec.initializeRequest request, StreamObserver<Rec.initializeResponse> responseStreamObserver){
            String abbr = request.getAbbr();
            int docks = request.getDocks();
            int bikes = request.getBikes();
            operations.initializeStations(abbr, docks, bikes);
            responseStreamObserver.onNext(Rec.initializeResponse.newBuilder().build());
            responseStreamObserver.onCompleted();
    }

    @Override
    public void update(Rec.UpdateRequest request, StreamObserver<Rec.UpdateResponse> responseObserver) {
        replicaManager.update(request.getInput());
        Rec.UpdateResponse response = Rec.UpdateResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void initializeReplicas(Rec.InitializeReplicasRequest request, StreamObserver<Rec.InitializeReplicasResponse> responseStreamObserver){
        String abbr = request.getAbbr();
        int docks = request.getDocks();
        int bikes = request.getBikes();
        replicaManager.initializeReplicasStations(abbr, docks, bikes);
        responseStreamObserver.onNext(Rec.InitializeReplicasResponse.newBuilder().build());
        responseStreamObserver.onCompleted();
    }
}
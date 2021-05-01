package pt.tecnico.rec;

import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.*;
import static io.grpc.Status.INVALID_ARGUMENT;
import pt.tecnico.rec.exceptions.BadEntrySpecificationException;

public class RecServerImpl extends RecordServiceGrpc.RecordServiceImplBase {

    private static RecServerImplOperations operations;
    private ReplicaManager replicaManager;

    public RecServerImpl(String zooHost, String zooPort){
        operations = new RecServerImplOperations(zooHost, zooPort);
        replicaManager = new ReplicaManager(zooHost, zooPort, "/grpc/bicloin/rec");
    }

    public static RecServerImplOperations getRecOperations(){
        return operations;
    }

    @Override
    public void read(Rec.ReadRequest request, StreamObserver<Rec.ReadResponse> responseObserver){
        try{
            String responseInput = operations.read(request.getName());
            Rec.ReadResponse response = Rec.ReadResponse.newBuilder().setValue(responseInput).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }
    }

    @Override
    public void write(Rec.WriteRequest request, StreamObserver<Rec.WriteResponse> responseObserver){
        try{
            String responseInput = operations.write(request.getName());
            Rec.WriteResponse response = Rec.WriteResponse.newBuilder().setValue(responseInput).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
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
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
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
}
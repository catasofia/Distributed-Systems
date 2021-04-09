package pt.tecnico.rec;

import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.*;
import static io.grpc.Status.INVALID_ARGUMENT;
import pt.tecnico.rec.exceptions.BadEntrySpecificationException;

public class RecServerImpl extends RecordServiceGrpc.RecordServiceImplBase {

    RecServerImplOperations operations = new RecServerImplOperations();

    @Override
    public void read(Rec.ReadRequest request, StreamObserver<Rec.ReadResponse> responseObserver){
        try{
            String responseInput = operations.read(request.getName());
            Rec.ReadResponse response = Rec.ReadResponse.newBuilder().setValue(Integer.parseInt(responseInput)).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }
    }

    @Override
    public void write(Rec.WriteRequest request, StreamObserver<Rec.WriteResponse> responseObserver){
        //TODO
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
}
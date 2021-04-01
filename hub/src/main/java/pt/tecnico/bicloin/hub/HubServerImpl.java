package pt.tecnico.bicloin.hub;

import io.grpc.stub.StreamObserver;
import pt.tecnico.bicloin.hub.grpc.*;
import static io.grpc.Status.INVALID_ARGUMENT;
import pt.tecnico.bicloin.hub.exceptions.BadEntrySpecificationException;



public class HubServerImpl extends HubServiceGrpc.HubServiceImplBase {

    HubServerImplOperations operations = new HubServerImplOperations();
    

    @Override
    public void balance(Hub.BalanceRequest request, StreamObserver<Hub.BalanceResponse> responseObserver){
        //TODO
    }

    @Override
    public void topUp(Hub.TopUpRequest request, StreamObserver<Hub.TopUpResponse> responseObserver){
        //TODO
    }

    @Override
    public void infoStation(Hub.InfoStationRequest request, StreamObserver<Hub.InfoStationResponse> responseObserver){
        //TODO
    }

    @Override
    public void locateStation(Hub.LocateStationRequest request, StreamObserver<Hub.LocateStationResponse> responseObserver){
        //TODO
    }

    @Override
    public void bikeUp(Hub.BikeUpRequest request, StreamObserver<Hub.BikeUpResponse> responseObserver){
        //TODO
    }

    @Override
    public void bikeDown(Hub.BikeDownRequest request, StreamObserver<Hub.BikeDownResponse> responseObserver){
        //TODO
    }

    @Override
    public void ctrlPing(Hub.CtrlPingRequest request, StreamObserver<Hub.CtrlPingResponse> responseObserver){
        try{
            String responseInput = operations.ping(request.getInput());
            Hub.CtrlPingResponse response = Hub.CtrlPingResponse.newBuilder().setOutput(responseInput).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }
    }

    @Override
    public void sysStatus(Hub.SysStatusRequest request, StreamObserver<Hub.SysStatusResponse> responseObserver){
        /*try{

        }catch(){}*/
    }
}
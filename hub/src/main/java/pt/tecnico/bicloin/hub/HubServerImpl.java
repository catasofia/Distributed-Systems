package pt.tecnico.bicloin.hub;

import io.grpc.stub.StreamObserver;
import pt.tecnico.bicloin.hub.grpc.*;
import static io.grpc.Status.INVALID_ARGUMENT;
import pt.tecnico.bicloin.hub.exceptions.BadEntrySpecificationException;

import java.util.List;


public class HubServerImpl extends HubServiceGrpc.HubServiceImplBase {

    HubServerImplOperations operations = new HubServerImplOperations();
    

    @Override
    public void balance(Hub.BalanceRequest request, StreamObserver<Hub.BalanceResponse> responseObserver){
       Integer balance = operations.balance(request.getName());
       Hub.BalanceResponse response = Hub.BalanceResponse.newBuilder().setBalance(balance).build();
       responseObserver.onNext(response);
       responseObserver.onCompleted();
    }

    @Override
    public void topUp(Hub.TopUpRequest request, StreamObserver<Hub.TopUpResponse> responseObserver){
        //TODO
    }

    @Override
    public void infoStation(Hub.InfoStationRequest request, StreamObserver<Hub.InfoStationResponse> responseObserver){
        try {
            List<String> result = operations.info_station(request.getAbbr());
            Hub.InfoStationResponse response = Hub.InfoStationResponse.newBuilder().setName(result.get(0))
                    .setLatitude(Double.parseDouble(result.get(1))).setLongitude(Double.parseDouble(result.get(2)))
                    .setDocksNr(Integer.parseInt(result.get(3))).setPrize(Integer.parseInt(result.get(4)))
                    .setBikesNr(Integer.parseInt(result.get(5))).setStatistics(Integer.parseInt(result.get(6))).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }
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
        try{
            String responseStatus = operations.sys_status(request.getInput());
            Hub.SysStatusResponse response = Hub.SysStatusResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch(BadEntrySpecificationException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.toString()).asRuntimeException());
        }
    }
}
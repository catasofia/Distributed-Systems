package pt.tecnico.bicloin.hub;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import pt.tecnico.bicloin.hub.grpc.*;
import static io.grpc.Status.INVALID_ARGUMENT;
import pt.tecnico.bicloin.hub.exceptions.BadEntrySpecificationException;

import java.util.List;


public class HubServerImpl extends HubServiceGrpc.HubServiceImplBase {

    HubServerImplOperations operations = new HubServerImplOperations();

    @Override
    public void balance(Hub.BalanceRequest request, StreamObserver<Hub.BalanceResponse> responseObserver){
       String balance = operations.balance(request.getName());
       Hub.BalanceResponse response = Hub.BalanceResponse.newBuilder().setBalance(balance).build();
       responseObserver.onNext(response);
       responseObserver.onCompleted();
    }

    @Override
    public void topUp(Hub.TopUpRequest request, StreamObserver<Hub.TopUpResponse> responseObserver){
        try {
            String balance = operations.topUp(request.getName(), request.getAmount(), request.getPhone());
            Hub.TopUpResponse response = Hub.TopUpResponse.newBuilder().setBalance(balance).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch(BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getEntrySpecification().label).asRuntimeException());
        }
    }

    @Override
    public void infoStation(Hub.InfoStationRequest request, StreamObserver<Hub.InfoStationResponse> responseObserver){
        try {
            List<String> result = operations.info_station(request.getAbbr());
            Hub.InfoStationResponse response = Hub.InfoStationResponse.newBuilder().setName(result.get(0))
                    .setLatitude(Double.parseDouble(result.get(1))).setLongitude(Double.parseDouble(result.get(2)))
                    .setDocksNr(Integer.parseInt(result.get(3))).setPrize(Integer.parseInt(result.get(4))).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getEntrySpecification().label).asRuntimeException());
        }
    }

    @Override
    public void locateStation(Hub.LocateStationRequest request, StreamObserver<Hub.LocateStationResponse> responseObserver){
        String result = operations.locate_station(request.getLatitude(), request.getLongitude(),request.getK());
        Hub.LocateStationResponse response = Hub.LocateStationResponse.newBuilder().setAbbrs(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void bikeUp(Hub.BikeUpRequest request, StreamObserver<Hub.BikeUpResponse> responseObserver){
        try {
            String result = operations.bikeUp(request.getName(), request.getLatitude(), request.getLongitude(), request.getAbbr());
            Hub.BikeUpResponse response = Hub.BikeUpResponse.newBuilder().setResponse(result).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch(BadEntrySpecificationException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getEntrySpecification().label).asRuntimeException());
        }
    }

    @Override
    public void bikeDown(Hub.BikeDownRequest request, StreamObserver<Hub.BikeDownResponse> responseObserver){
        try {
            String result = operations.bikeDown(request.getName(), request.getLatitude(), request.getLongitude(), request.getAbbr());
            Hub.BikeDownResponse response = Hub.BikeDownResponse.newBuilder().setResponse(result).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch(BadEntrySpecificationException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getEntrySpecification().label).asRuntimeException());
        }
    }

    @Override
    public void ctrlPing(Hub.CtrlPingRequest request, StreamObserver<Hub.CtrlPingResponse> responseObserver){
        try{
            String responseInput = operations.ping(request.getInput());
            Hub.CtrlPingResponse response = Hub.CtrlPingResponse.newBuilder().setOutput(responseInput).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (BadEntrySpecificationException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getEntrySpecification().label).asRuntimeException());
        }
    }

    @Override
    public void sysStatus(Hub.SysStatusRequest request, StreamObserver<Hub.SysStatusResponse> responseObserver){
        try{
            String responseStatus = operations.sys_status(request.getInput());
            Hub.SysStatusResponse response = Hub.SysStatusResponse.newBuilder().setOutput(responseStatus).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch(BadEntrySpecificationException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getEntrySpecification().label).asRuntimeException());
        }
    }

}
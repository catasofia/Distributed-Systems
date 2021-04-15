package pt.tecnico.bicloin.hub;


import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import pt.tecnico.bicloin.hub.grpc.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;
import java.util.List;

public class HubTester {
	private static List<ManagedChannel> channels;

	public static void main(String[] args) throws ZKNamingException, IOException, InterruptedException{
		System.out.println(HubTester.class.getSimpleName());

		// receive and print arguments

		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		HubFrontend hubFrontend = new HubFrontend();
		channels = hubFrontend.createChannels("localhost", "2181");
		ctrl_ping(hubFrontend);

		for(ManagedChannel channel: channels) {
			channel.shutdownNow();
		}
		System.exit(0);
	}

	public static void ctrl_ping(HubFrontend frontend) {
		try {
			Hub.CtrlPingRequest request = Hub.CtrlPingRequest.newBuilder().setInput("ola ping!").build();
			String response = frontend.ctrlPing(request.getInput());
			System.out.println(response);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
					e.getStatus().getDescription());
		}
	}
}

package pt.tecnico.bicloin.app;

import io.grpc.ManagedChannel;
import pt.tecnico.bicloin.hub.HubFrontend;
import pt.tecnico.bicloin.hub.grpc.Hub;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class AppMain {

	private static HubFrontend hubFrontend;
	private static List<ManagedChannel> channels;

	public static void main(String[] args) throws ZKNamingException, IOException, InterruptedException {
		System.out.println(AppMain.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		hubFrontend = new HubFrontend();
		System.out.println(args[0] + args[1]);
		channels = hubFrontend.createChannels(args[0], args[1]);

		//String response = hubFrontend.ctrlPing("friend");
		//System.out.println(response);

		for(ManagedChannel channel: channels){
			channel.shutdownNow();
		}
	}
}

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
		try{
			Double.parseDouble(args[4]);
			Double.parseDouble(args[5]);
		} catch (NumberFormatException e){
			System.out.println("Latitude and longitude should be both doubles.");
		}

		String user = args[2];

		hubFrontend = new HubFrontend();
		channels = hubFrontend.createChannels(args[0], args[1]);

		Double latitude = Double.parseDouble(args[4]);
		Double longitude = Double.parseDouble(args[5]);

		System.out.println("Trying ping:");
		String response = hubFrontend.ctrlPing("friend");
		System.out.println(response);

		try(Scanner scanner = new Scanner(System.in)){
			System.out.println("\n" + user + ", welcome to the app!!\n");
			do{
				String command = scanner.nextLine();
				if (command.equals("balance")) {
					System.out.println(hubFrontend.balance(user));
				}
				else if (command.startsWith("info")){
					String[] attributes = command.split(" ");
					System.out.println(hubFrontend.info_station(attributes[1]));
				}
				else if (command.startsWith("top-up")){
					String[] attributes = command.split(" ");
					System.out.println(user + " " + hubFrontend.topUp(user, Integer.parseInt(attributes[1])) + " BIC");
				}
				else {
					System.out.println("The command you entered is not valid. Please, try again!");
				}
			} while(scanner.hasNextLine());
		}
		for(ManagedChannel channel: channels) {
			channel.shutdownNow();
		}
	}
}

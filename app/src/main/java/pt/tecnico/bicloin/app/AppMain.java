package pt.tecnico.bicloin.app;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import pt.tecnico.bicloin.hub.HubFrontend;
import pt.tecnico.bicloin.hub.grpc.Hub;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
			System.out.println("Latitude e longitude têm de ser valores Double.");
		}

		Map<String, String> tags = new HashMap<>();
		String user = args[2];



		Double latitude = Double.parseDouble(args[4]);
		Double longitude = Double.parseDouble(args[5]);

		try(Scanner scanner = new Scanner(System.in)){
			hubFrontend = new HubFrontend();
			channels = hubFrontend.createChannels(args[0], args[1]);
			System.out.println("\n" + user + ", bem-vindo à APP!!\n");
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
					try {
						String[] attributes = command.split(" ");
						System.out.println(user + " " + hubFrontend.topUp(user, Integer.parseInt(attributes[1]), args[3]) + " BIC");
					} catch (StatusRuntimeException e){
						System.out.println("ERRO: " + e.getMessage());
					}
				}
				else if(command.startsWith("tag")){
					String[] attributes = command.split(" ");
					try{
						Double.parseDouble(attributes[1]);
						Double.parseDouble(attributes[2]);
					} catch (NumberFormatException e){
						System.out.println("ERRO: Impossivel criar uma tag com os valores:" +
								attributes[1] + " e " + attributes[2]);
						continue;
					}
					tags.put(attributes[3], attributes[1] + " " + attributes[2]);
					System.out.println("OK");
				}
				else if(command.startsWith("move")){
					String[] attributes = command.split(" ");
					if(tags.get(attributes[1]) == null){
						System.out.println("ERRO: Não existe nenhuma tag com o nome: " + attributes[1]);
					}
					else{
						String position = tags.get(attributes[1]);
						String[] lat_long = position.split(" ");
						latitude = Double.parseDouble(lat_long[0]);
						longitude = Double.parseDouble(lat_long[1]);
						System.out.println(user + " em https://www.google.com/maps/place/" + latitude + "," + longitude);
					}
				}
				else if(command.startsWith("at")){
					System.out.println(user + " em https://www.google.com/maps/place/" + latitude + "," + longitude);
				}
				else if(command.startsWith("scan")){
					String[] attributes = command.split(" ");
					String stations = hubFrontend.locate_station(latitude, longitude, Integer.parseInt(attributes[1]));
					String[] dividedStations = stations.split("\n");
					for (String dividedStation : dividedStations) {
						System.out.println(dividedStation + " " + hubFrontend.scan(dividedStation, latitude, longitude));
					}
				}
				else if(command.startsWith("bike-up")){
					String[] attributes = command.split(" ");
					try{
						hubFrontend.bikeUp(user, latitude, longitude, attributes[1]);
						System.out.println("OK");
					} catch(StatusRuntimeException e){
						System.out.println("ERRO: " + e.getMessage());
					}
				}
				else if(command.startsWith("bike-down")){
					String[] attributes = command.split(" ");
					try{
						hubFrontend.bikeDown(user, latitude, longitude, attributes[1]);
						System.out.println("OK");
					} catch(StatusRuntimeException e){
						System.out.println("ERRO: " + e.getMessage());
					}
				}
				else if(command.startsWith("ping")){
					System.out.println(hubFrontend.ctrlPing("ping"));
				}
				else if(command.startsWith("sys_status")){
					System.out.println(hubFrontend.sys_status("status"));
				}
				else if(command.startsWith("zzz")){
					String[] attributes = command.split(" ");
					TimeUnit.MILLISECONDS.sleep(Integer.parseInt(attributes[1]));
					System.out.println("Dormi durante " + attributes[1] + " milissegundos!\n");
				}
				else if(command.startsWith("#")){
					continue;
				}
				else if(command.startsWith("exit")){
					for(ManagedChannel channel: channels) {
						channel.shutdownNow();
					}
					System.exit(0);
				}
				else if(command.startsWith("help")){
					//TODO
					System.out.println("\nComandos adicionais:");
					System.out.println("exit para sair da app");
				}
				else {
					System.out.println("O comando inserido não é válido. Por favor, tente de novo!");
				}
			} while(scanner.hasNextLine());
		}
		for(ManagedChannel channel: channels) {
			channel.shutdownNow();
		}
	}
}

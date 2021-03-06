package pt.tecnico.bicloin.app;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.bicloin.hub.HubFrontend;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AppMain {
	private static HubFrontend hubFrontend;
	private static ManagedChannel channel;

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
			channel = hubFrontend.createChannel(args[0], args[1]);
			System.out.println("\n" + user + ", bem-vindo à APP!!\n");
			do{
				String command = scanner.nextLine();
				if (command.equals("balance")) {
					System.out.println(user + " " + hubFrontend.balance(user));
				}
				else if (command.startsWith("info")){
					if(command.equals("info")) {
						System.out.println("Má utilização do comando info.");
						continue;
					}
					try {
						String[] attributes = command.split(" ");
						System.out.println(hubFrontend.info_station(attributes[1]));
					} catch (StatusRuntimeException e){
						if(e.getMessage().equals("UNAVAILABLE: io exception")) {
							System.out.println("ERRO: Falha na conexão.");
						}
						else{
							System.out.println("ERRO: " + e.getMessage());
						}
					}
				}
				else if (command.startsWith("top-up")){
					if(command.equals("top-up")) {
						System.out.println("Má utilização do comando top-up.");
						continue;
					}
					try {
						String[] attributes = command.split(" ");
						System.out.println(user + " " + hubFrontend.topUp(user, Integer.parseInt(attributes[1]), args[3]) + " BIC");
					} catch (StatusRuntimeException e){
						System.out.println("ERRO: " + e.getMessage());
					}
				}
				else if(command.startsWith("tag")){
					if(command.equals("tag")) {
						System.out.println("Má utilização do comando tag.");
						continue;
					}
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
					if(command.equals("move")) {
						System.out.println("Má utilização do comando move.");
						continue;
					}
					String[] attributes = command.split(" ");
					if(attributes.length == 2){
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
					} else if(attributes.length == 3){
						try{
						Double.parseDouble(attributes[1]);
						Double.parseDouble(attributes[2]);
						latitude = Double.parseDouble(attributes[1]);
						longitude = Double.parseDouble(attributes[2]);
						System.out.println(user + " em https://www.google.com/maps/place/" + latitude + "," + longitude);
						} catch (NumberFormatException e){
							System.out.println("ERRO: Impossivel mover  para coordenadas com valores:" +
									attributes[1] + " e " + attributes[2]);
							continue;
						}
					} else {
						System.out.println("ERRO: prima \"help\" para saber como utilizar o comando \"move\".");
					}
				}
				else if(command.equals("at")){
					System.out.println(user + " em https://www.google.com/maps/place/" + latitude + "," + longitude);
				}
				else if(command.startsWith("scan")){
					if(command.equals("scan")) {
						System.out.println("Má utilização do comando scan.");
						continue;
					}
					String[] attributes = command.split(" ");
					String stations = hubFrontend.locate_station(latitude, longitude, Integer.parseInt(attributes[1]));
					String[] dividedStations = stations.split("\n");
					for (String dividedStation : dividedStations) {
						System.out.println(dividedStation + " " + hubFrontend.scan(dividedStation, latitude, longitude));
					}
				}
				else if(command.startsWith("bike-up")){
					if(command.equals("bike-up")) {
						System.out.println("Má utilização do comando bike-up.");
						continue;
					}
					String[] attributes = command.split(" ");
					try{
						hubFrontend.bikeUp(user, latitude, longitude, attributes[1]);
						System.out.println("OK");
					} catch(StatusRuntimeException e){
						if(e.getMessage().equals("UNAVAILABLE: io exception")) {
							System.out.println("ERRO: Falha na conexão.");
						}
						if(Status.DEADLINE_EXCEEDED.getCode() == e.getStatus().getCode()){
							System.out.println("ERRO: Tempo de espera excedido. Tente outra vez!");
						}
						else{
							System.out.println("ERRO: " + e.getMessage());
						}
					}
				}
				else if(command.startsWith("bike-down")){
					if(command.equals("bike-down")) {
						System.out.println("Má utilização do comando bike-down.");
						continue;
					}
					String[] attributes = command.split(" ");
					try{
						hubFrontend.bikeDown(user, latitude, longitude, attributes[1]);
						System.out.println("OK");
					} catch(StatusRuntimeException e){
						if(e.getMessage().equals("UNAVAILABLE: io exception")) {
							System.out.println("ERRO: Falha na conexão.");
						}
						if(Status.DEADLINE_EXCEEDED.getCode() == e.getStatus().getCode()){
							System.out.println("ERRO: Tempo de espera excedido. Tente outra vez!");
						}
						else{
							System.out.println("ERRO: " + e.getMessage());
						}
					}
				}
				else if(command.startsWith("ping")){
					if(command.equals("ping")){
						try{
							System.out.println(hubFrontend.ctrlPing(""));
						} catch(StatusRuntimeException e){
							System.out.println("ERRO: " + e.getMessage());
						}
					} else{
						String[] attributes = command.split(" ");
						try{
							System.out.println(hubFrontend.ctrlPing(attributes[1]));
						} catch(StatusRuntimeException e){
							System.out.println("ERRO: " + e.getMessage());
						}
					}
				}
				else if(command.startsWith("sys_status")){
					if(command.equals("sys_status")){
						try{
							System.out.println(hubFrontend.sys_status(""));
						} catch(StatusRuntimeException e){
							System.out.println("ERRO: " + e.getMessage());
						}
					} else{
						String[] attributes = command.split(" ");
						try {
							System.out.println(hubFrontend.sys_status(attributes[1]));
						}catch (StatusRuntimeException e){
							System.out.println("ERRO: " + e.getMessage());
						}
					}
				}
				else if(command.startsWith("zzz")){
					if(command.equals("zzz")) {
						System.out.println("Má utilização do comando zzz.");
						continue;
					}
					String[] attributes = command.split(" ");
					TimeUnit.MILLISECONDS.sleep(Integer.parseInt(attributes[1]));
					System.out.println("Dormi durante " + attributes[1] + " milissegundos!");
				}
				else if(command.startsWith("#")){
					continue;
				}
				else if(command.equals("exit")){
					channel.shutdownNow();
					System.out.println("Até à próxima!!!");
					System.exit(0);
				}
				else if(command.equals("help")){
					System.out.println("\n- Comando \"at\": Devolve a posição atual do utilizador.\nNão recebe argumentos.\n");

					System.out.println("- Comando \"balance\": Devolve o saldo da conta do utilizador.\nNão recebe argumentos.\n");

					System.out.println("- Comando \"bike-down\": Comando para o utilizador devolver uma bicicleta.\nRecebe um argumento:" +
							" Abreviatura da estação na qual se pertende devolver a bicicleta.\n");

					System.out.println("- Comando \"bike-up\": Comando para o utilizador alugar uma bicicleta.\nRecebe um argumento:" +
							" Abreviatura da estação de onde se pertende alugar a bicicleta.\n");

					System.out.println("- Comando \"exit\": Comando para desligar a aplicação.\nNão recebe argumentos.\n");

					System.out.println("- Comando \"info\": Comando que devolve a informação de uma estação.\nRecebe um " +
							"argumento: abreviatura da estação pretendida.\n");

					System.out.println("- Comando \"tag\": Comando que permite criar uma tag de localização.\nRecebe 3 argumentos: " +
							"Latitude, longitude e nome da tag.\n");

					System.out.println("- Comando \"move\": Comando para mover o utilizador.\nRecebe 2 argumentos:" +
							" Latitude Longitude || Recebe 1 argumento: Nome da tag\n");

					System.out.println("- Comando \"ping\": Comando que devolve o estado do servidor.\nRecebe um argumento: input.\n");

					System.out.println("- Comando \"scan\": Comando que devolve a informação das estações mais próximas.\nRecebe " +
							"um argumento: Número de estações que deseja ver a informação.\n");

					System.out.println("- Comando \"sys_status\": Comando que devolve o estado dos servidores atuais indicando " +
							"o seu path e se está a responder(up) ou não(down).\nRecebe um argumento: input\n");

					System.out.println("Comando \"top-up\": Comando que permite carregar o saldo do utilizador.\nRecebe um argumento: " +
							"Valor (inteiro) a carregar.\n");

					System.out.println("- Comando \"zzz\": Comando para adormecer a aplicação.\nRecebe um argumento: " +
							"Número de milissegundos para adormecer.\n");

					System.out.println("- Comando que comece com \"#\" é ignorado.\n");

					System.out.println("\n****************Comandos adicionais criados:****************");

					System.out.println("- Comando \"exit\": Comando para sair da aplicação.\nNão recebe argumentos.");
				}
				else {
					System.out.println("O comando inserido não é válido. Por favor, tente de novo! Prima \"help\" " +
							"para saber os comandos disponíveis e como utilizar.");
				}
			} while(scanner.hasNextLine());
		}
		channel.shutdownNow();
	}
}

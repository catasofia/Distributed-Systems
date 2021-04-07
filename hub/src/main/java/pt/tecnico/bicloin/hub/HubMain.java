package pt.tecnico.bicloin.hub;

import pt.ulisboa.tecnico.sdis.zk.*;
import io.grpc.*;
import pt.tecnico.bicloin.hub.HubServerImpl;
import pt.tecnico.bicloin.hub.*;
import pt.tecnico.bicloin.hub.Station;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class HubMain {

	private static ZKNaming zkNaming;
	private static HubServerImplOperations ops = new HubServerImplOperations();
	
	public static void main(String[] args) throws ZKNamingException, IOException, InterruptedException {
		System.out.println(HubMain.class.getSimpleName());
		
		if(args.length != 7 || args.length != 8){
			System.out.printf("Expected 7 or 8 arguments\n");
		}
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		zkNaming = null;

		String zooHost = args[0];
		String zooPort = args[1];
		String host = args[2];
		String port = args[3];
		String path = "/grpc/bicloin/hub/";
		path += args[4];

		HubServerImpl impl = new HubServerImpl();

		List<User> users = readUsersFromCSV(args[5]);
		List<Station> stations = readStationsFromCSV(args[6]);


		try{
			zkNaming = new ZKNaming(zooHost, zooPort);
			//publish
			zkNaming.rebind(path, host, port);
			Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService(impl).build();
			server.start();
			server.awaitTermination();
		}catch(ZKNamingException e){
			e.printStackTrace();
		}finally{
			if(zkNaming != null){
				zkNaming.unbind(path, host, port);
			}
		}
	}

	public static List<User> readUsersFromCSV(String fileName){
		List<User> users = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			String line = br.readLine();
			while (line != null) {
				String[] attributes = line.split(" \t");
				User user = new User(attributes[0], attributes[1], attributes[2]);
				users.add(user);
				line = br.readLine();
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		return users;
	}

	public static List<Station> readStationsFromCSV(String fileName){
		List<Station> stations = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			String line = br.readLine();
			while (line != null) {
				String[] attributes = line.split(" \t");
				Station station = new Station(attributes[0], attributes[1], Double.parseDouble(attributes[2]),
						Double.parseDouble(attributes[3]), Integer.parseInt(attributes[4]),
						Integer.parseInt(attributes[5]), Integer.parseInt(attributes[6]));
				stations.add(station);
				line = br.readLine();
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		return stations;
	}
}

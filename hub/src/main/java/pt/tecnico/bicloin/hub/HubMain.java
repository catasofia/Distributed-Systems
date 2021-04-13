package pt.tecnico.bicloin.hub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.rec.RecServerImpl;
import pt.tecnico.rec.RecServerImplOperations;
import pt.tecnico.rec.grpc.Rec;
import pt.tecnico.rec.grpc.RecordServiceGrpc;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;*/

public class HubMain {

	private static ZKNaming zkNaming;
	private static Map<String, User> users =  new HashMap<>();
	private static Map<String, Station> stations = new HashMap<>();

	private static ManagedChannel rec_channel;
	private static RecordServiceGrpc.RecordServiceBlockingStub rec_stub;
	
	public static void main(String[] args) throws ZKNamingException, IOException, InterruptedException {
		System.out.println(HubMain.class.getSimpleName());
		
		if(args.length != 7 && args.length != 8){
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

		try{
			zkNaming = new ZKNaming(zooHost, zooPort);
			//publish
			zkNaming.rebind(path, host, port);
			Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService(impl).build();
			server.start();
			readUsersFromCSV(args[5]);
			readStationsFromCSV(args[6], args.length == 8);
			server.awaitTermination();
		}catch(ZKNamingException e){
			e.printStackTrace();
		}finally{
			if(zkNaming != null){
				zkNaming.unbind(path, host, port);
			}
		}

	}

	public static Map<String, User> getUsers(){
		return users;
	}

	public static Map<String, Station> getStations(){
		return stations;
	}

	public static void initializeRec(String abbr, Integer docksNr, Integer bikesNr)throws ZKNamingException, IOException, InterruptedException{
		ZKRecord zkRecord = zkNaming.lookup("/grpc/bicloin/rec/1");
		String uri = zkRecord.getURI();
		rec_channel = ManagedChannelBuilder.forTarget(uri).usePlaintext().build();
		rec_stub = RecordServiceGrpc.newBlockingStub(rec_channel);

		Rec.initializeRequest request = Rec.initializeRequest.newBuilder().setAbbr(abbr)
				.setDocks(docksNr).setBikes(bikesNr).build();
		rec_stub.initialize(request);
		//RecServerImp.getRecOperations().initializeStations(abbr, docksNr, bikesNr);
	}

	public static void readUsersFromCSV(String fileName){
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			String line = br.readLine();
			while (line != null) {
				String[] attributes = line.split(" \t");
				if (attributes[0].length() < 3 || attributes[0].length() > 10) {
					System.out.println("The user should be between 3 and 10 characters. " + attributes[0] + " doesn't have the right length.");
					continue;
				}
				if (attributes[1].length() > 30) {
					System.out.println("The name can't have more than 30 characters. " + attributes[1] + " doesn't have the right length.");
					continue;
				}
				/*PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
				try{
					PhoneNumber phoneNumber = phoneUtil.parse(attributes[2], PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
					if (!phoneUtil.isValidNumber(phoneNumber)){
						System.out.println("Invalid mobile number.");
						continue;
					}
				} catch (NumberParseException e){
					System.out.println("Exception: Impossible to parse number.");
				}*/
				User user = new User(attributes[0], attributes[1], attributes[2]);
				users.put(attributes[0], user);
				line = br.readLine();
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void readStationsFromCSV(String fileName, boolean bool){
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
			String line = br.readLine();
			while (line != null) {
				String[] attributes = line.split(" \t");
				if (attributes[1].length() != 4) {
					System.out.println("The abbreviation must have 4 characters. " + attributes[1] + " has" + attributes[1].length());
					continue;
				}
				try{
					Double.parseDouble(attributes[2]);
					Double.parseDouble(attributes[3]);
				} catch (NumberFormatException e){
					System.out.println("Latitude and longitude should be both doubles.");
					continue;
				}
				Station station = new Station(attributes[0], attributes[1], Double.parseDouble(attributes[2]),
					Double.parseDouble(attributes[3]), Integer.parseInt(attributes[4]), Integer.parseInt(attributes[6]));
				stations.put(attributes[1], station);
				if(bool){
					try {
						initializeRec(attributes[1], Integer.parseInt(attributes[4]), Integer.parseInt(attributes[5]));

					} catch (ZKNamingException e){
						System.out.println("ZK Naming exception.");
					} catch (IOException e){
					System.out.println("IO Exception");
					} catch (InterruptedException e){
						System.out.println("Interrupted exception.");
					}
				}
				line = br.readLine();
			}
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}

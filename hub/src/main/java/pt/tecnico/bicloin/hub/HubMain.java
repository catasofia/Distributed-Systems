package pt.tecnico.bicloin.hub;

import pt.ulisboa.tecnico.sdis.zk.*;
import io.grpc.*;
import pt.tecnico.bicloin.hub.HubServerImpl;
import java.io.IOException;


public class HubMain {

	private static ZKNaming zkNaming;
	private static HubServerImplOperations ops = new HubServerImplOperations();
	
	public static void main(String[] args) throws ZKNamingException, IOException, InterruptedException {
		System.out.println(HubMain.class.getSimpleName());
		
		if(args.length != 8 || args.length != 9){
			System.out.printf("Expected 8 or 9 arguments\n");
		}
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		zkNaming = null;

		String zooHost = args[1];
		String zooPort = args[2];
		String host = args[3];
		String port = args[4];
		String path = "/grpc/bicloin/hub/";
		path += args[5];

		HubServerImpl impl = new HubServerImpl();

		try{
			zkNaming = new ZKNaming(zooHost, zooPort);
			//publish
			zkNaming.rebind(path, host, port);
			Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService(impl).build();
			ops.addServer(server);
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
	
}

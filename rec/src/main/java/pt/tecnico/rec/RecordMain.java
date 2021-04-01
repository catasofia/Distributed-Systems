package pt.tecnico.rec;

import pt.ulisboa.tecnico.sdis.zk.*;
import io.grpc.*;
import pt.tecnico.rec.RecServerImpl;
import java.io.IOException;

public class RecordMain {
	
	private static ZKNaming zkNaming;

	public static void main(String[] args) throws ZKNamingException, IOException, InterruptedException {
		System.out.println(RecordMain.class.getSimpleName());
		
		if(args.length != 6){
			System.err.printf("Expected 6 arguments\n");
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
		String path = "/grpc/bicloin/rec/1";

		RecServerImpl impl = new RecServerImpl();

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
	
}

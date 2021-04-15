package pt.tecnico.rec;


import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.tecnico.rec.grpc.*;
import java.io.IOException;

public class RecordTester {

	private static ManagedChannel channel;
	public static void main(String[] args) throws ZKNamingException, IOException, InterruptedException{
		System.out.println(RecordTester.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		RecFrontend recFrontend = new RecFrontend();
		channel = recFrontend.createChannel("localhost", "2181");
		ctrl_ping(recFrontend);
		channel.shutdownNow();
		System.exit(0);
	}

	public static void ctrl_ping(RecFrontend frontend) {
		try {
			Rec.CtrlPingRequest request = Rec.CtrlPingRequest.newBuilder().setInput("ola ping!").build();
			String response = frontend.ctrlPing(request.getInput());
			System.out.println(response);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " +
					e.getStatus().getDescription());
		}
	}
}

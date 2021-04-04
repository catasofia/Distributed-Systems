package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;

import pt.tecnico.bicloin.hub.HubFrontend;
import pt.tecnico.bicloin.hub.grpc.Hub;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import static io.grpc.Status.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HubIT {

	private static HubFrontend hubFrontend;
	private static List<ManagedChannel> channels;
	// static members
	// TODO
	
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp() throws ZKNamingException, IOException, InterruptedException{
		hubFrontend = new HubFrontend();
		channels = hubFrontend.createChannels("localhost", "2181");
	}
	
	@AfterAll
	public static void oneTimeTearDown() {
	}
	
	// initialization and clean-up for each test
	
	@BeforeEach
	public void setUp() {
		
	}
	
	@AfterEach
	public void tearDown() {
		
	}
		
	// tests 
	
	@Test
	public void test() {
		
		
	}

	@Test
	public void pingOKTest(){
		Hub.CtrlPingRequest request = Hub.CtrlPingRequest.newBuilder().setInput("friend").build();
		String response = HubFrontend.ctrlPing("friend");
		assertEquals("friend", response);
	}

	@Test
	public void emptyPingTest(){
		Hub.CtrlPingRequest request = Hub.CtrlPingRequest.newBuilder().setInput("").build();
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->hubFrontend.ctrlPing(""))
				.getStatus()
				.getCode());
	}
}

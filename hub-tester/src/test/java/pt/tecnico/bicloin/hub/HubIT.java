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
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp() throws ZKNamingException, IOException, InterruptedException {
		hubFrontend = new HubFrontend();
		channels = hubFrontend.createChannels("localhost", "2181");
	}
	
	@AfterAll
	public static void oneTimeTearDown() {
		for(ManagedChannel channel: channels) {
			channel.shutdownNow();
		}
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
	public void topUpOK(){
		String response = HubFrontend.topUp("diana", 12, "+34010203");
		assertEquals("120", response);
	}
		

	@Test
	public void balanceOK(){
		String response = HubFrontend.balance("maria");
		assertEquals("0 BIC", response);
	}

	@Test
	public void pingOKTest(){
		Hub.CtrlPingRequest request = Hub.CtrlPingRequest.newBuilder().setInput("friend").build();
		String response = HubFrontend.ctrlPing("friend");
		assertEquals("friend", response);
	}

	@Test
	public void emptyPingTest(){
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->hubFrontend.ctrlPing(""))
				.getStatus()
				.getCode());
	}



	@Test
	public void topUpErrorPhone(){
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->hubFrontend.
				topUp("alice", 12, "+35196502030"))
				.getStatus()
				.getCode());
	}

	@Test
	public void topUpErrorMoney(){
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->hubFrontend.
				topUp("alice", 23, "+35191102030"))
				.getStatus()
				.getCode());
	}

	@Test
	public void locate_stationOK(){
		String response = HubFrontend.locate_station(38.7380, -9.3000, 2);
		assertEquals("istt\nstao", response);
	}

	@Test
	public void info_stationOK(){
		String response = HubFrontend.info_station("cate");
		assertEquals("Sé Catedral, lat 38.7097, -9.1336 long, 10 docas, 2 BIC prémio, 0 bicicletas, 10 levantamentos, " +
				"0 devoluções, https://www.google.com/maps/place/38.7097,-9.1336", response);
	}

	@Test
	public void info_stationError(){
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->hubFrontend.
				info_station("amadora"))
				.getStatus()
				.getCode());
	}

	@Test
	public void bikeUpAndBikeDownOK(){
		HubFrontend.topUp("carlos", 12, "+34203040");
		HubFrontend.bikeUp("carlos", 38.7372, -9.3023, "istt");
		HubFrontend.bikeDown("carlos", 38.7372, -9.3023, "istt");
	}


	@Test
	public void bikeUpErrorDistance(){
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->hubFrontend.
				bikeUp("alice", 38.7376, -9.3031, "jero"))
				.getStatus()
				.getCode());
	}

	@Test
	public void bikeDownErrorDistance(){
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->hubFrontend.
				bikeDown("alice", 38.7376, -9.3031, "jero"))
				.getStatus()
				.getCode());
	}

}

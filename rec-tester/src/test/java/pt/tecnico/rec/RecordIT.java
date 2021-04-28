package pt.tecnico.rec;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import pt.tecnico.rec.RecFrontend;
import pt.tecnico.rec.grpc.*;
import static io.grpc.Status.INVALID_ARGUMENT;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import static io.grpc.Status.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordIT {

	private static RecFrontend recFrontend;
	private static List<ManagedChannel> channels = new ArrayList<>();
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp() throws ZKNamingException, IOException, InterruptedException{
		recFrontend = new RecFrontend();
		channels = recFrontend.createChannels("localhost", "2181");
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
	public void pingOKTest(){
		Rec.CtrlPingRequest request = Rec.CtrlPingRequest.newBuilder().setInput("friend").build();
		String response = recFrontend.ctrlPing("friend");
		assertEquals("friend", response);
	}

	@Test
	public void emptyPingTest(){
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->recFrontend.ctrlPing(""))
				.getStatus()
				.getCode());
	}



	@Test
	public void topUpOK(){
		String response = recFrontend.topUp("alice/top_up 15");
		assertEquals("150", response);
	}

	@Test
	public void bikeUpOK(){
		recFrontend.topUp("eva/top_up 15");
		String response = recFrontend.bikeUp("istt/bike_up eva");
		assertEquals("10", response);
	}

	@Test
	public void bikeUpStateError(){
		recFrontend.bikeUp("istt/bike_up alice");

		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->recFrontend
				.bikeUp("istt/bike_up alice"))
				.getStatus()
				.getCode());
	}

	@Test
	public void bikeUpNoBikesError(){

		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->recFrontend
				.bikeUp("cate/bike_up alice"))
				.getStatus()
				.getCode());
	}

	@Test
	public void bikeUpBalanceError(){

		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->recFrontend
				.bikeUp("istt/bike_up carlos"))
				.getStatus()
				.getCode());
	}

	@Test
	public void bikeDownOK(){
		recFrontend.topUp("bruno/top_up 15");
		recFrontend.bikeUp("cais/bike_up bruno");
		String response = recFrontend.bikeDown("cais/bike_down bruno 1");
		assertEquals("1", response);
	}

	@Test
	public void bikeDownNoBikeError(){

		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->recFrontend
				.bikeDown("istt/bike_down diana 4"))
				.getStatus()
				.getCode());
	}

	@Test
	public void bikeDownNoDocksError(){

		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(StatusRuntimeException.class, ()->recFrontend
				.bikeDown("gulb/bike_down alice 2"))
				.getStatus()
				.getCode());
	}

	@Test
	public void infoStationOK(){
		String response = recFrontend.info_station("istt");
		assertEquals("10 bicicletas, 10 levantamentos, 0 devoluções, ", response);
	}

	@Test
	public void balanceOK(){
		String response = recFrontend.balance("alice/balance");
		assertEquals("140 BIC", response);
	}
}

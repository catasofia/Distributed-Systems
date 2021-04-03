package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.*;
import pt.tecnico.bicloin.hub.grpc.Hub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HubIT {
	
	// static members
	// TODO
	
	
	// one-time initialization and clean-up
	@BeforeAll
	public static void oneTimeSetUp(){
		
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

}

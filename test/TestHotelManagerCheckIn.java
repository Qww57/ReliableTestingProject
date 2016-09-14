import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hms.main.HotelManager;
import hms.model.Room;

/**
 * Class used in order to all the cases that could happen when using the check in function
 * from the class {@link HotelManager}.
 * 
 * This class is a parameterized class performing three tests (one with a presidential room, one with
 * a standard room and one with null input) on a set of 15 inputs.
 * 
 * @author Quentin
 *
 */
@RunWith(Parameterized.class)
public class TestHotelManagerCheckIn{
	
	public static HotelManager manager;
	
	public static Date dateIn = new GregorianCalendar(2016, Calendar.DECEMBER, 8).getTime();
	
	public static Date dateOut = new GregorianCalendar(2016, Calendar.DECEMBER, 15).getTime();
	
	@BeforeClass
	public static void  initialize(){
		manager = new HotelManager();
	}
	
	@Before
	public void setUp(){
		manager = new HotelManager();
	}
	
	@After
	public void tearDown(){
		manager = null;
	}
	
	@AfterClass
	public static void clean(){
		manager = null;
		dateIn = null;
		dateOut = null;
	}
	
	/**
	 * These parameters are used to cover all the different input cases when performing a check in.
	 * 
	 * Rooms couldn't be used here as a parameter since rooms are only instantiate at the same time 
	 * than the manager.
	 * 
	 * @return set of input data for the tests
	 */
	@SuppressWarnings("boxing")
	@Parameters(name = "{index}:{0},{1},{2},{3},{4},{5},{6},{7}")
	public static Collection<Object[]> parameters(){
		return Arrays.asList(new Object[][]
			{
				// Well formated data sets
				{"X12345678", "Gates", "Standard", "Microsoft", dateIn, false, "", "Success"},
				{"X12345678", "Gates", "Business", "Microsoft", dateIn, true, "01:23:45:67:89:ab", "Success"},
				
				// Data sets with not allowed null inputs
				{null, "Gates", "Business", "Microsoft", dateIn, false, "", "No input can be null"}, 
				{"X12345678", null, "Business", "Microsoft", dateIn, false, "", "No input can be null"},
				{"X12345678", "Gates", null, "Microsoft", dateIn, false, "", "No input can be null"},
				{"X12345678", "Gates", "Business", null, dateIn, false, "", "No input can be null"},
				{"X12345678", "Gates", "Standard", "Microsoft", null, false, "", "No input can be null"},
				{"X12345678", "Gates", "Standard", "Microsoft", dateIn, false, null, "No input can be null"},
				
				// Data sets with not allowed empty inputs
				{"", "Gates", "Business", "Microsoft", dateIn, false, "", "ID, name, and company cannot be empty"},
				{"X12345678", "", "Business", "Microsoft", dateIn, false, "", "ID, name, and company cannot be empty"},
				{"X12345678", "Gates", "Business", "", dateIn, false, "", "ID, name, and company cannot be empty"},
				
				// Data set with inconsistent information
				{"X12345678", "Gates", "Standard", "Microsoft", dateIn, true, "", "No data service for standard occupants"},
				
				// Data set with invalid room type
				{"X12345678", "Gates", "Luxuous", "Microsoft", dateIn, false, "", "Invalid type"},
				
				// Data set with invalid ID
				{"X12345", "Gates", "Business", "Microsoft", dateIn, false, "", "The format of the inputted ID is invalid"},
				
				// Data set with invalid ethernetAdress
				{"X12345678", "Gates", "Business", "Microsoft", dateIn, true, "XXX", "The format of the inputted "
						+ "ethernetAddress" + " is invalid"}			
			});
	}
	
	@Parameter(value=0)
	public String ID;
	
	@Parameter(value=1)
	public String name;
	
	@Parameter(value=2)
	public String type;
	
	@Parameter(value=3)
	public String company;
	
	@Parameter(value=4)
	public Date checkInDate;
	
	@Parameter(value=5)
	public boolean dataServiceRequired;
	
	@Parameter(value=6)
	public String ethernetAddress;
	
	@Parameter(value=7)
	public String expectedMessage;
	
	/**
	 * Tests made in case of a presidential room, that is allowed to get internet.
	 * 
	 * This test is a bit longer than the other ones since it's also verifying that the check in has been done and registered
	 * correctly.
	 */
	@Test
	public void testCheckIn_PresidentialRoom(){
		
		// Presidential room according to the XML description
		Room room = manager.getRoom(0,0);
		
		// Testing all the check in cases with a room
		String checkin = manager.checkIn(ID, name, type, company, checkInDate, dataServiceRequired, ethernetAddress, room);
		assertEquals(expectedMessage + " has failed", expectedMessage, checkin);
		
		if (checkin == "Success"){
			// Testing the availability of the rooms
			assertEquals("occupation failed", 1, manager.listAllOccupiedRooms().size());
			assertEquals("availability failed", 5, manager.listAllAvailableRooms().size());
			assertFalse("room still available", room.isAvailable());
			
			// Testing the checkout of the room
			String checkout = manager.checkOut(dateOut, room);
			
			if (checkout == "Success"){
				// Testing the availability of the rooms
				assertEquals("occupation failed", 0, manager.listAllOccupiedRooms().size());
				assertEquals("availability failed", 6, manager.listAllAvailableRooms().size());
				assertTrue("room still non available", room.isAvailable());
			}
		}
	}
	
	/**
	 * Test made in case of a standard room that is not allowed to get internet.
	 */
	@Test
	public void testCheckIn_StandardRoom(){
		
		// Standard room according to the XML description
		Room room = manager.getRoom(2,0);
		
		// Testing all the check in cases with a room
		String checkin = manager.checkIn(ID, name, type, company, checkInDate, dataServiceRequired, ethernetAddress, room);
		if (dataServiceRequired == false){
			assertEquals(expectedMessage + " has failed", expectedMessage, checkin);
		}else{
			String message = "No data service for standard rooms";
			assertEquals(message + "has failed", message, checkin);
		}
	}
	
	/**
	 * Test made in case of a null input instead of a room.
	 */
	@Test
	public void testCheckIn_nullRoom(){	
		
		Room room = null;
		
		String checkin = manager.checkIn(ID, name, type, company, checkInDate, dataServiceRequired, ethernetAddress, room);
		String expected = "No input can be null";
		assertEquals(expected + "has failed", expected, checkin);
	}

	
}
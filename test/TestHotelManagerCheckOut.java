import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hms.main.HotelManager;
import hms.model.Room;

@RunWith(Parameterized.class)
public class TestHotelManagerCheckOut{
	
	public static HotelManager manager;	
	public static Date dateIn = new GregorianCalendar(2016, Calendar.DECEMBER, 8).getTime();
	public static Date dateOut = new GregorianCalendar(2016, Calendar.DECEMBER, 15).getTime();
	public static Date wdateOut = new GregorianCalendar(2016, Calendar.DECEMBER, 7).getTime();
	
	@Before
	public void setUp(){
		manager = new HotelManager();
	}
	
	@AfterClass
	public static void clean(){
		dateIn = null; 
		dateOut = null;
		wdateOut = null;
	}
	
	@Parameters(name = "{index}:{0},{1}")
	public static Collection<Object[]> parameters(){
		return Arrays.asList(new Object[][]
			{
				// Well formated data sets
				{dateOut, "Success"},
				// Not well formated data sets
				{null, "No input can be null"},
				{wdateOut, "Check-out date must be after check-in date"}
			});
	}

	@Parameter(value=0)
	public Date checkOut;
	
	@Parameter(value=1)
	public String expectedMessage;

	@Test
	public void testCheckOut_OccupiedRoom(){
		
		// Check in with well formated data set
		Room room = manager.getRoom(0, 0);
		String checkin = manager.checkIn("X12345678", "Gates", "Standard", "Microsoft", dateIn, false, "", room);
		assertEquals("Success", checkin);
		
		// Testing the availability of the rooms
		assertEquals("occupation failed", 1, manager.listAllOccupiedRooms().size());
		assertEquals("availability failed", 5, manager.listAllAvailableRooms().size());
		
		// Testing the check out
		String checkout = manager.checkOut(checkOut, room);
		assertEquals(expectedMessage + " has failed", expectedMessage, checkout);
		
		// Testing the availability of the rooms
		if (checkout == "Success"){
			assertEquals("occupation failed", 0, manager.listAllOccupiedRooms().size());
			assertEquals("availability failed", 6, manager.listAllAvailableRooms().size());
		}else{
			assertEquals("occupation failed", 1, manager.listAllOccupiedRooms().size());
			assertEquals("availability failed", 5, manager.listAllAvailableRooms().size());
		}
	}
	
	@Test
	public void testCheckOut_EmptyRoom(){
		
		// Check in with well formated data set
		Room room = manager.getRoom(0, 0);
		
		// Testing the availability of the rooms
		assertEquals("occupation failed", 0, manager.listAllOccupiedRooms().size());
		assertEquals("availability failed", 6, manager.listAllAvailableRooms().size());
		
		// Testing the check out
		String checkout = manager.checkOut(checkOut, room);	
		if (expectedMessage ==  "No input can be null"){
			// This message has priority over the expected one			
			assertEquals(expectedMessage + " has failed", expectedMessage, checkout);
		}else{
			String expected = "The room has no occupant";
			assertEquals(expected + " has failed", expected, checkout);
		}
	}
	
	@Test
	public void testCheckOut_NullRoom(){
		
		// Check in with well formated data set
		Room room = null;
		
		// Testing the availability of the rooms
		assertEquals("occupation failed", 0, manager.listAllOccupiedRooms().size());
		assertEquals("availability failed", 6, manager.listAllAvailableRooms().size());
		
		// Testing the check out
		String checkout = manager.checkOut(checkOut, room);
		String expected = "No input can be null";
		assertEquals(expected + " has failed", expected, checkout);
	}
}
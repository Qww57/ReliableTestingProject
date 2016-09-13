import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import hms.model.Occupant;
import hms.model.Occupation;
import hms.model.Room;

@RunWith(Parameterized.class)
public class TestRoom {

	@SuppressWarnings("boxing")
	@Parameters(name = "{index}:{0},{1},{2},{3},{4},{5}, {6}")
	public static Collection<Object[]> parameters(){
		return Arrays.asList(new Object[][]
			{
				// well formated input
				{0, 0, 2, ((short) 1), 150.0, "Standard", false},
				{1, 2, 2, ((short) 2), 250.0, "Executive", false},
				{2, 2, 1, ((short) 3), 350.0, "Presidential", true},
				
				// wrong type entered
				{2, 2, 1, ((short) 0), 350.0, null, false},
				{2, 2, 1, ((short) 0), 350.0, null, true}
			});
	}
	
	@Parameter(value=0)
	public int floorNo;
	
	@Parameter(value=1)
	public int roomNo;
	
	@Parameter(value=2)
	public int capacity;
	
	@Parameter(value=3)
	public short type;
	
	@Parameter(value=4)
	public double rate;
	
	@Parameter(value=5)
	public String typeString;
	
	@Parameter(value=6)
	public boolean dataService;
	
	private static Occupation occupation;
	
	@Before
	public void setUp(){
		// Creation of the input of the occupation
		Occupant occupant = new Occupant("X12345678", "Business", "Gates", "Microsoft");
		Calendar c = GregorianCalendar.getInstance();
		c.set(2016, 11, 8); // 8th of December 2016
		Date dateIn = c.getTime();
		String ethernetService = "01:23:45:67:89:ab";
		
		// Initialization of the occupation
		occupation = new Occupation(dateIn, dataService, ethernetService, occupant);
	}
	
	@Test
	public void test() {
		Room room = new Room(floorNo, roomNo, capacity, type, rate);
		
		// Initiating a new room and testing it
		assertEquals("floorNo has failed", floorNo, room.getFloorNo());
		assertEquals("roomNo has failed", roomNo, room.getRoomNo());
		assertEquals("capacity has failed", capacity, room.getCapacity());
		assertEquals("type has failed", type, room.getType());
		assertEquals("typeString has failed", typeString, room.getTypeString());
		assertEquals("rate has failed", rate, room.getRate(), 0.001);
		assertTrue(room.isAvailable());
		
		// Changing the rate
		double newRate = 100;
		room.setRate(newRate);
		assertEquals("rate has failed", newRate, room.getRate(), 0.001);
		
		// Testing that toString is showing all important information 
		if (typeString != null){
			String toString = room.toString();
			
			// Testing toString method when no occupant is defined
			assertTrue(toString.contains(String.valueOf(roomNo)));
			assertTrue(toString.contains(String.valueOf(floorNo)));
			assertTrue(toString.contains(String.valueOf(capacity)));
			assertTrue(toString.contains(room.getTypeString()));
			assertTrue(toString.contains(String.valueOf(newRate)));
			if (typeString != "Standard"){
				assertTrue(toString.contains("Data service: Not in used"));
			}
			
			// Adding an occupation to this room
			room.setOccupation(occupation);
			assertEquals("occupation has failed", occupation, room.getOccupation());
			assertFalse(room.isAvailable());

			// Testing toString method when an occupant has been added
			toString = room.toString();
			assertTrue(toString.contains(occupation.getOccupant().getName()));
			if (typeString != "Standard" && dataService == true){
				// Checking that the internet service is described
				assertTrue(toString.contains("Data service: In used"));
				assertTrue(toString.contains(occupation.getEthernetAddress()));
			}
		}	
	}
}

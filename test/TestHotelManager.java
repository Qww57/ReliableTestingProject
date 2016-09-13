import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import hms.main.HotelManager;
import hms.model.Occupant;
import hms.model.Room;

/**
 * Class used in order to test most of the functions from {@link HotelManager}.
 * 
 * All the cases of CheckIn and CheckOut are tested in detail in the parameterized classes
 * {@link TestHotelManagerCheckIn} and {@link TestHotelManagerCheckOut}. 
 * 
 * @author Quentin
 *
 */
public class TestHotelManager {
	
	public HotelManager manager;

	@Before
	public void setUp(){ manager = new HotelManager();}
	
	@After
	public void tearDown(){manager = null;}
	
	/*
	 * Testing the initialization of the hotel manager
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHotelManager_HotelInitialization() {
		
		// Checking the name of the hotel
		assertNotNull(manager.getHotelName());
		
		// All rooms should be available
		ArrayList<Room> ocRooms = manager.listAllOccupiedRooms();
		assertEquals("occupation failed", 0, ocRooms.size());
		
		ArrayList<Room> avRooms = manager.listAllAvailableRooms();
		assertEquals("availability failed", 6, avRooms.size());
		for (int i = 0; i < avRooms.size(); i++)
			assertTrue(avRooms.get(i).isAvailable());
		
		// Testing the initialization of the UI
		assertNotNull(manager.getUI());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHotelManager_findingOccupant(){
		
		// Performing the check-in
		Room room = manager.getRoom(0, 0);
		Date dateIn = new GregorianCalendar(2016, Calendar.DECEMBER, 8).getTime();
		String checkin = manager.checkIn("X12345678", "Gates", "Standard", "Microsoft", dateIn, false, "", room);
		assertEquals("Success", checkin);
		
		// Testing the availability of the rooms
		assertEquals("occupation failed", 1, manager.listAllOccupiedRooms().size());
		assertEquals("availability failed", 5, manager.listAllAvailableRooms().size());
		
		// Getting the occupant of the room by different ways and putting correct values
		ArrayList<Room> rooms = manager.findOccupant("Name", "Gates");
		Occupant occupant = rooms.get(0).getOccupation().getOccupant();
		assertEquals("Gates", occupant.getName());	
		rooms = manager.findOccupant("ID", "X12345678");
		occupant = rooms.get(0).getOccupation().getOccupant();
		assertEquals("Gates", occupant.getName());
		rooms = manager.findOccupant("Company", "Microsoft");
		occupant = rooms.get(0).getOccupation().getOccupant();
		assertEquals("Gates", occupant.getName());	
		rooms = manager.findOccupant("Type", "Standard");
		occupant = rooms.get(0).getOccupation().getOccupant();
		assertEquals("Gates", occupant.getName());
		
		// Getting the occupant of the room by different ways and putting wrong values
		rooms = manager.findOccupant("Name", "Jobs");
		assertEquals(0, rooms.size());	
		rooms = manager.findOccupant("ID", "A12345678");
		assertEquals(0, rooms.size());		
		rooms = manager.findOccupant("Company", "Apple");
		assertEquals(0, rooms.size());	
		rooms = manager.findOccupant("Type", "Business");
		assertEquals(0, rooms.size());
		
		// Performing the check-out
		Date dateOut = new GregorianCalendar(2016, Calendar.DECEMBER, 12).getTime();
		String checkout = manager.checkOut(dateOut, room);
		assertEquals("Success", checkout);
		
		// Testing the availability of the rooms
		assertEquals("occupation failed", 0, manager.listAllOccupiedRooms().size());
		assertEquals("availability failed", 6, manager.listAllAvailableRooms().size());
		
		// Getting the occupant of the room by different ways
		rooms = manager.findOccupant("Name", "Gates");
		assertEquals(0, rooms.size());	
		rooms = manager.findOccupant("ID", "X12345678");
		assertEquals(0, rooms.size());		
		rooms = manager.findOccupant("Company", "Microsoft");
		assertEquals(0, rooms.size());		
		rooms = manager.findOccupant("Type", "Standard");
		assertEquals(0, rooms.size());
	}
	
	@Test
	public void testUpdateRoom(){
		
		Room room = manager.getRoom(0, 0);
		double rate = room.getRate();
		
		double newRate = 12;
		assertFalse(rate == newRate);
		manager.updateRoomRate(room, newRate);
		
		assertEquals(newRate, room.getRate(), 0.0001);
	}
	
	@Test
	public void testUpdateRoom_NullRoom(){
		
		Room room = null;
		try{
			manager.updateRoomRate(room, 12);
			fail("Should have failed since room is null");
		}catch (Exception e){
			// Expected behavior
			System.out.println(e.getMessage());
		}
	}
}


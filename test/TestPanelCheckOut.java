import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JTable;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import hms.command.CheckOutCommand;
import hms.gui.CheckOutPanel;
import hms.main.HotelManager;
import hms.model.Room;


/**
 * Class used in order to test all the cases that could happen when performing a check out with the 
 * user interface. 
 * 
 * The tests performed are similar to the ones in {@link TestHotelManagerCheckOut}.
 * This class is a parameterized class performing three tests (one with an occupied room with data,
 * one occupied without data, one with an empty room and one with null input) on a set of 3 inputs. 
 *  
 * @author Quentin
 *
 */
public class TestPanelCheckOut{
	
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
	
	@Test
	public void testCheckOut_occupied_room_without_data(){

		// Check in with well formated data set
		Room room = manager.getRoom(0, 0); // Presidential room
		String checkin = manager.checkIn("X12345678", "Gates", "Standard", "Microsoft", dateIn, false, "", room);
		assertEquals("Success", checkin);
		
		// Testing the availability of the rooms
		assertEquals("occupation failed", 1, manager.listAllOccupiedRooms().size());
		assertEquals("availability failed", 5, manager.listAllAvailableRooms().size());	
		
		//Create a CheckOutPanel object
		CheckOutCommand cmd = new CheckOutCommand(manager);
		CheckOutPanel checkOutPanel = new CheckOutPanel(cmd, manager);
		
		assertNotNull(checkOutPanel.hotelManager);
		assertNotNull(checkOutPanel.command);
		assertNotNull(checkOutPanel.checkOutButton);
		assertNotNull(checkOutPanel.checkOutDateField);
		assertNotNull(checkOutPanel.occupiedRooms);
		assertNotNull(checkOutPanel.occupiedRoomTable);
		
		// Checking that the command only has the default values
		assertEquals("", checkOutPanel.command.getID());
		assertEquals("", checkOutPanel.command.getName());
		assertEquals("", checkOutPanel.command.getCompany());
		assertEquals("", checkOutPanel.command.getType());
		assertEquals(checkOutPanel.command.getCurrentDate(), checkOutPanel.command.getCheckInDate());
		assertEquals(checkOutPanel.command.getCurrentDate(), checkOutPanel.command.getCheckOutDate());
		assertFalse(checkOutPanel.command.isDataServiceRequired());
		assertEquals("00:00:00:00:00:00", checkOutPanel.command.getEthernetAddress());
		
		// Selecting a room to check out
		JTable table = checkOutPanel.occupiedRoomTable;
		assertEquals(1, table.getRowCount()); // Checking that the table contains at least the booked room
		assertEquals(-1, table.getSelectedRow()); // -1 is the default value when nothing has been selected
		assertTrue(table.getRowSelectionAllowed()); // Checking that we can select a room
		table.setRowSelectionInterval(0, 0); // Selecting the first line of the table
		assertEquals(0, table.getSelectedRow()); // Testing that the first line as been selected
		
		// Selecting a check out date
		String date = new SimpleDateFormat("dd-MM-yyyy").format(dateOut);
		checkOutPanel.checkOutDateField.setText(date);

		// Checking that the command has been updated according to the values
		assertEquals(dateIn, checkOutPanel.command.getCheckInDate());
		assertEquals("Gates", checkOutPanel.command.getName());
		assertEquals("Microsoft", checkOutPanel.command.getCompany());
		assertEquals("X12345678", checkOutPanel.command.getID());
		assertEquals("Standard", checkOutPanel.command.getType());
		assertEquals("", checkOutPanel.command.getEthernetAddress());
		assertFalse(checkOutPanel.command.isDataServiceRequired());
		assertEquals(checkOutPanel.command.getCurrentDate(), checkOutPanel.command.getCheckOutDate()); // Value is never changed in code
		
		//Mock click action of checkOutPanel.checkOutButton
		checkOutPanel.checkOutButton.doClick();
		
		// Checking that the room has been released
		assertEquals(0, checkOutPanel.occupiedRoomTable.getRowCount());
		assertEquals(0, manager.listAllOccupiedRooms().size());
		assertEquals(6, manager.listAllAvailableRooms().size());
	}
	
	@Test
	public void testCheckOut_occupied_room_with_data(){

		// Check in with well formated data set
		Room room = manager.getRoom(0, 0); // Presidential room
		String checkin = manager.checkIn("X12345678", "Gates", "Business", "Microsoft", dateIn, true, "ab:12:34:56:78:90", room);
		assertEquals("Success", checkin);
		
		// Testing the availability of the rooms
		assertEquals("occupation failed", 1, manager.listAllOccupiedRooms().size());
		assertEquals("availability failed", 5, manager.listAllAvailableRooms().size());	
		
		//Create a CheckOutPanel object
		CheckOutCommand cmd = new CheckOutCommand(manager);
		CheckOutPanel checkOutPanel = new CheckOutPanel(cmd, manager);
		
		assertNotNull(checkOutPanel.hotelManager);
		assertNotNull(checkOutPanel.command);
		assertNotNull(checkOutPanel.checkOutButton);
		assertNotNull(checkOutPanel.checkOutDateField);
		assertNotNull(checkOutPanel.occupiedRooms);
		assertNotNull(checkOutPanel.occupiedRoomTable);
		
		// Checking that the command only has the default values
		assertEquals("", checkOutPanel.command.getID());
		assertEquals("", checkOutPanel.command.getName());
		assertEquals("", checkOutPanel.command.getCompany());
		assertEquals("", checkOutPanel.command.getType());
		assertEquals(checkOutPanel.command.getCurrentDate(), checkOutPanel.command.getCheckInDate());
		assertEquals(checkOutPanel.command.getCurrentDate(), checkOutPanel.command.getCheckOutDate());
		assertFalse(checkOutPanel.command.isDataServiceRequired());
		assertEquals("00:00:00:00:00:00", checkOutPanel.command.getEthernetAddress());
		
		// Selecting a room to check out
		JTable table = checkOutPanel.occupiedRoomTable;
		assertEquals(1, table.getRowCount()); // Checking that the table contains at least the booked room
		assertEquals(-1, table.getSelectedRow()); // -1 is the default value when nothing has been selected
		assertTrue(table.getRowSelectionAllowed()); // Checking that we can select a room
		table.setRowSelectionInterval(0, 0); // Selecting the first line of the table
		assertEquals(0, table.getSelectedRow()); // Testing that the first line as been selected
		
		// Selecting a check out date
		String date = new SimpleDateFormat("dd-MM-yyyy").format(dateOut);
		checkOutPanel.checkOutDateField.setText(date);

		// Checking that the command has been updated according to the values
		assertEquals(dateIn, checkOutPanel.command.getCheckInDate());
		assertEquals("Gates", checkOutPanel.command.getName());
		assertEquals("Microsoft", checkOutPanel.command.getCompany());
		assertEquals("X12345678", checkOutPanel.command.getID());
		assertEquals("Business", checkOutPanel.command.getType());
		assertEquals("ab:12:34:56:78:90", checkOutPanel.command.getEthernetAddress());
		assertTrue(checkOutPanel.command.isDataServiceRequired());
		assertEquals(checkOutPanel.command.getCurrentDate(), checkOutPanel.command.getCheckOutDate()); // Value is never changed in code
		
		//Mock click action of checkOutPanel.checkOutButton
		checkOutPanel.checkOutButton.doClick();
		
		// Checking that the room has been released
		assertEquals(0, checkOutPanel.occupiedRoomTable.getRowCount());
		assertEquals(0, manager.listAllOccupiedRooms().size());
		assertEquals(6, manager.listAllAvailableRooms().size());
	}
	
	/**
	 * Room is not selected, so empty input
	 */
	@Test
	public void testCheckOut_empty_room(){

		// Check in with well formated data set
		Room room = manager.getRoom(0, 0); // Presidential room
		String checkin = manager.checkIn("X12345678", "Gates", "Business", "Microsoft", dateIn, true, "ab:12:34:56:78:90", room);
		assertEquals("Success", checkin);
		
		// Testing the availability of the rooms
		assertEquals("occupation failed", 1, manager.listAllOccupiedRooms().size());
		assertEquals("availability failed", 5, manager.listAllAvailableRooms().size());	
		
		//Create a CheckOutPanel object
		CheckOutCommand cmd = new CheckOutCommand(manager);
		CheckOutPanel checkOutPanel = new CheckOutPanel(cmd, manager);
		
		assertNotNull(checkOutPanel.hotelManager);
		assertNotNull(checkOutPanel.command);
		assertNotNull(checkOutPanel.checkOutButton);
		assertNotNull(checkOutPanel.checkOutDateField);
		assertNotNull(checkOutPanel.occupiedRooms);
		assertNotNull(checkOutPanel.occupiedRoomTable);
		
		// Selecting a check out date but no room
		String date = new SimpleDateFormat("dd-MM-yyyy").format(dateOut);
		checkOutPanel.checkOutDateField.setText(date);
		
		// Checking that the command only has the default values
		assertEquals("", checkOutPanel.command.getID());
		assertEquals("", checkOutPanel.command.getName());
		assertEquals("", checkOutPanel.command.getCompany());
		assertEquals("", checkOutPanel.command.getType());
		assertEquals(checkOutPanel.command.getCurrentDate(), checkOutPanel.command.getCheckInDate());
		assertEquals(checkOutPanel.command.getCurrentDate(), checkOutPanel.command.getCheckOutDate());
		assertFalse(checkOutPanel.command.isDataServiceRequired());
		assertEquals("00:00:00:00:00:00", checkOutPanel.command.getEthernetAddress());
	
		//Mock click action of checkOutPanel.checkOutButton
		checkOutPanel.checkOutButton.doClick();
		
		// Checking that no rooms have been released
		assertEquals(1, checkOutPanel.occupiedRoomTable.getRowCount());
		assertEquals(1, manager.listAllOccupiedRooms().size());
		assertEquals(5, manager.listAllAvailableRooms().size());
	}
}
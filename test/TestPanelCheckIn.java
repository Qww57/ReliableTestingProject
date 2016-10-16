import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hms.command.CheckInCommand;
import hms.gui.CheckInPanel;
import hms.main.HotelManager;
import hms.model.Room;

/**
 * Class used in order to test all the cases that could happen when performing a check in with the 
 * user interface. 
 * 
 * The tests performed are similar to the ones in {@link TestHotelManagerCheckIn}.
 * This class is a parameterized class performing three tests (one with an occupied room, one with an 
 * empty room and one with null input) on a set of 15 inputs. 
 * 
 * Two other tests have been added to check the consistency between the room information and the table 
 * displayed on the screen and to check the initialization of all components of the Panel.
 *  
 * @author Quentin
 *
 */
@RunWith(Parameterized.class)
public class TestPanelCheckIn {

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
	
	@SuppressWarnings("boxing")
	@Parameters(name = "{index}:{0},{1},{2},{3},{4},{5},{6},{7},{8}")
	public static Collection<Object[]> parameters(){
		return Arrays.asList(new Object[][]
			{
			// Well formated data sets
			{"X12345678", "Gates", "Standard", "Microsoft", dateIn, "No", "", new Integer(5), false},
			{"X12345678", "Gates", "Business", "Microsoft", dateIn, "Yes", "01:23:45:67:89:ab", new Integer(1), false}, // Presidential room
			{"X12345678", "Gates", "Business", "Microsoft", dateIn, "Yes", "01:23:45:67:89:ab", new Integer(3), false}, // Executive room
				
			// Data sets with not allowed null inputs
			{null, "Gates", "Business", "Microsoft", dateIn, "No", "", new Integer(1), true}, 
			{"X12345678", null, "Business", "Microsoft", dateIn, "No", "", new Integer(1), true},
			{"X12345678", "Gates", null, "Microsoft", dateIn, "No", "", new Integer(1), true},
			{"X12345678", "Gates", "Business", null, dateIn, "No", "",  new Integer(1), true},
			{"X12345678", "Gates", "Standard", "Microsoft", null, "No", "", new Integer(1), true},
			{"X12345678", "Gates", "Standard", "Microsoft", dateIn, "No", null, new Integer(1), true},
			
			// Data sets with not allowed empty inputs
			{"", "Gates", "Business", "Microsoft", dateIn, "No", "", 1, true},
			{"X12345678", "", "Business", "Microsoft", dateIn, "No", "", 1, true},
			{"X12345678", "Gates", "Business", "", dateIn, "No", "", 1, true},
			
			// Data set with inconsistent information
			{"X12345678", "Gates", "Standard", "Microsoft", dateIn, "Yes", "01:23:45:67:89:ab", 1, true},
			{"X12345678", "Gates", "Business", "Microsoft", dateIn, "Yes", "01:23:45:67:89:ab", 5, true}, // Standard room
						
			// Data set with invalid room type
			{"X12345678", "Gates", "Luxuous", "Microsoft", dateIn, "No", "", 1, true},
			
			// Data set with invalid ID
			{"X12345", "Gates", "Business", "Microsoft", dateIn, "No", "", 1, true},
			
			// Data set with invalid ethernetAdress
			{"X12345678", "Gates", "Business", "Microsoft", dateIn, "Yes", "XXX", 1, true},
			
			// Data set with invalid date
			{"X12345678", "Gates", "Business", "Microsoft", "INVALID DATE", "Yes", "01:23:45:67:89:ab", 1, true}
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
	public String dataServiceRequired;
	
	@Parameter(value=6)
	public String ethernetAddress;
	
	@Parameter(value=7)
	public int roomLine;
	
	@Parameter(value=8)
	public boolean expectedFailure;
	
	@Test
	public void test_with_room() {
			
		CheckInCommand cmd = new CheckInCommand(manager);
		
		//Create a CheckInPanel object
		CheckInPanel checkInPanel = new CheckInPanel(cmd, manager);
		test_initialization(checkInPanel);
		
		// Setting the input values
		checkInPanel.IDField.setText(ID);
		checkInPanel.nameField.setText(name);
		checkInPanel.companyField.setText(company);
		checkInPanel.typeField.setSelectedItem(type);
		checkInPanel.dataServiceRequiredBox.setSelectedItem(dataServiceRequired);
		checkInPanel.ethernetAddressField.setText(ethernetAddress);
		assertEquals(6, checkInPanel.availableRooms.length);
				
		//Mock click action of checkInPanel.checkInButton
		JTable table = checkInPanel.availableRoomTable;
		assertEquals(6, table.getRowCount());
		assertEquals(-1, table.getSelectedRow()); // -1 is the default value when nothing has been selected
		assertTrue(table.getRowSelectionAllowed()); // Checking that we can select a room
		try{
			table.setRowSelectionInterval(roomLine, roomLine); // Selecting the first line of the table
			assertEquals(roomLine, table.getSelectedRow()); // Testing that the first line as been selected
		} catch (Exception e){
			expectedFailure = true;
		}
		
		checkInPanel.checkInButton.doClick();
		
		if (expectedFailure == false){
			assertEquals(5, checkInPanel.availableRooms.length);
		} else {
			assertEquals(6, checkInPanel.availableRooms.length);
		}
	}
	
	@Test
	public void test_with_empty_room() {
			
		CheckInCommand cmd = new CheckInCommand(manager);
		
		//Create a CheckInPanel object
		CheckInPanel checkInPanel = new CheckInPanel(cmd, manager);
		test_initialization(checkInPanel);
		
		// Setting the input values
		checkInPanel.IDField.setText(ID);
		checkInPanel.nameField.setText(name);
		checkInPanel.companyField.setText(company);
		checkInPanel.typeField.setSelectedItem(type);
		checkInPanel.dataServiceRequiredBox.setSelectedItem(dataServiceRequired);
		checkInPanel.ethernetAddressField.setText(ethernetAddress);
		assertEquals(6, checkInPanel.availableRooms.length);
			
		checkInPanel.checkInButton.doClick();
		
		assertEquals(6, checkInPanel.availableRooms.length);	
	}
	
	@Test
	public void test_with_null_room() {
			
		CheckInCommand cmd = new CheckInCommand(manager);
		
		//Create a CheckInPanel object
		CheckInPanel checkInPanel = new CheckInPanel(cmd, manager);
		test_initialization(checkInPanel);
		
		JTable table = checkInPanel.availableRoomTable;
		try{
			table.setRowSelectionInterval(roomLine, roomLine); // Selecting the first line of the table
			assertEquals(roomLine, table.getSelectedRow()); // Testing that the first line as been selected
		} catch (Exception e){
			expectedFailure = true;
		}
		
		// Testing branches in getValueAt
		checkInPanel.availableRooms[roomLine] = null;
		assertNull(table.getValueAt(roomLine, 0));
		try{
			assertNull(table.getValueAt(roomLine, 5));
			fail("Should have raised an IndexOutOfBoundsException");
		}catch(IndexOutOfBoundsException e){
			// Expected behavior
		}
		
		// Testing line 183 TODO FAIL 
		
		// Setting the input values
		checkInPanel.IDField.setText(ID);
		checkInPanel.nameField.setText(name);
		checkInPanel.companyField.setText(company);
		checkInPanel.typeField.setSelectedItem(type);
		checkInPanel.dataServiceRequiredBox.setSelectedItem(dataServiceRequired);
		checkInPanel.ethernetAddressField.setText(ethernetAddress);
		assertEquals(6, checkInPanel.availableRooms.length);
		
		cmd.selectedRoom = cmd.hotelManager.getRoom(0, 0);
		table.setRowSelectionInterval(roomLine, roomLine); // Selecting the first line of the table
		assertEquals(roomLine, table.getSelectedRow()); // Testing that the first line as been selected
		
		checkInPanel.checkInButton.doClick();
	}
	
	/**
	 * Checking that the information in the available room table is consistent with
	 * the information displayed in the available rooms panel.
	 */
	@Test
	public void test_getCommande(){
		CheckInCommand cmd = new CheckInCommand(manager);
		
		//Create a CheckInPanel object
		CheckInPanel checkInPanel = new CheckInPanel(cmd, manager);
		test_initialization(checkInPanel);
		
		JTable table = checkInPanel.availableRoomTable;
		for (int i = 0; i < checkInPanel.availableRooms.length; i++){
			int tableLine = 1;
			table.setRowSelectionInterval(tableLine, tableLine);
			assertEquals(tableLine, table.getSelectedRow()); 
			assertNotNull(checkInPanel.command.selectedRoom);
		
			Room room = checkInPanel.command.selectedRoom;
			TableModel model = checkInPanel.availableRoomTable.getModel();
			assertEquals(room.getFloorNo() + "-" + room.getRoomNo(), model.getValueAt(tableLine, 0).toString());
			assertEquals(room.getTypeString(), model.getValueAt(tableLine, 1).toString());
			assertEquals(room.getCapacity(), Integer.parseInt(model.getValueAt(tableLine, 2).toString()));
			assertEquals(room.getRate(), Double.parseDouble(model.getValueAt(tableLine, 3).toString()), 0.001);
			if (room.getType() == 1)
				assertEquals("N/A", model.getValueAt(tableLine, 4).toString());
			else
				assertEquals("Not in used", model.getValueAt(tableLine, 4).toString());
			
			// Testing the default value of the table
			assertEquals("", model.getValueAt(tableLine, 5));
		}	
	}
	
	
	private static void test_initialization(CheckInPanel checkInPanel){
		assertNotNull(checkInPanel.hotelManager);
		assertNotNull(checkInPanel.command);
		assertNotNull(checkInPanel.IDField);
		assertNotNull(checkInPanel.nameField);
		assertNotNull(checkInPanel.companyField);
		assertNotNull(checkInPanel.checkInDateField);
		assertNotNull(checkInPanel.dataServiceRequiredBox);
		assertNotNull(checkInPanel.ethernetAddressField);
		assertNotNull(checkInPanel.availableRoomTable);
		assertNotNull(checkInPanel.availableRooms);
		assertNotNull(checkInPanel.checkInButton);
	}
}	
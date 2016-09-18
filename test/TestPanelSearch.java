import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hms.command.SearchCommand;
import hms.gui.SearchPanel;
import hms.main.HotelManager;
import hms.model.Room;

@RunWith(Parameterized.class)
public class TestPanelSearch {
	
	public static HotelManager manager;
	
	public static SearchPanel searchPanel;
	
	public static Date dateIn = new GregorianCalendar(2016, Calendar.DECEMBER, 8).getTime();
	
	@SuppressWarnings("boxing")
	@Parameters(name = "{index}:{0},{1}}")
	public static Collection<Object[]> parameters(){
		return Arrays.asList(new Object[][]
			{
				// Well formated data sets
				{0, "X12345678"}, // ID
				{1, "Gates"},  // Name
				{2, "Business"}, // Type 
				{3, "Microsoft"}, // Company
			});
	}
	
	@Parameter(value=0)
	public int type;
	
	@Parameter(value=1)
	public String value;
	
	@Before
	public void setUp(){
		//Create a HotelManager object
		manager = new HotelManager();
		
		//Create a SearchPanel object
		searchPanel = new SearchPanel(new SearchCommand(manager));	
		
		// These elements should be instantiated
		assertNotNull(searchPanel.command);
		assertNotNull(searchPanel.searchButton);
		assertNotNull(searchPanel.searchField);
		assertNotNull(searchPanel.searchResults);
		assertNotNull(searchPanel.searchRoomTable);
		assertNotNull(searchPanel.typeField);
	}
	
	@AfterClass
	public static void clean(){
		manager = null;
		searchPanel = null;
		dateIn = null;
	}
	
	@Test
	public void test_SearchPanel_standard_room() {
		
		//Booking one room
		String checkin = manager.checkIn("X12345678", "Gates", "Business", "Microsoft", dateIn, false, "", manager.getRoom(2, 0));
		assertEquals("Success", checkin);
		
		//Mock click action of searchPanel.searchButton
		searchPanel.searchField.setText(value);
		searchPanel.typeField.setSelectedIndex(type);
		searchPanel.searchButton.doClick();
		
		//Checking the number of results
		assertEquals(1, searchPanel.searchResults.length);
		TableModel model = searchPanel.searchRoomTable.getModel();
		assertEquals(1, model.getRowCount());
		
		//Checking the consistency with the room
		Room room = (Room) searchPanel.searchResults[0];
		compare_information(room, model, 0);		
	}
	
	
	@Test
	public void test_SearchPanel_presidential_room_without_data() {
		
		//Booking one room
		String checkin = manager.checkIn("X12345678", "Gates", "Business", "Microsoft", dateIn, false, "", manager.getRoom(0, 0));
		assertEquals("Success", checkin);
		
		//Mock click action of searchPanel.searchButton
		searchPanel.searchField.setText(value);
		searchPanel.typeField.setSelectedIndex(type);
		searchPanel.searchButton.doClick();
		
		//Checking the number of results
		assertEquals(1, searchPanel.searchResults.length);
		TableModel model = searchPanel.searchRoomTable.getModel();
		assertEquals(1, model.getRowCount());
		
		//Checking the consistency with the room
		Room room = (Room) searchPanel.searchResults[0];
		compare_information(room, model, 0);		
	}
	
	@Test
	public void test_SearchPanel_presidential_room_with_data() {
		
		// Booking one room
		String checkin = manager.checkIn("X12345678", "Gates", "Business", "Microsoft", dateIn, true, "01:23:45:67:89:ab",  manager.getRoom(0, 0));
		assertEquals("Success", checkin);
		
		//Mock click action of searchPanel.searchButton
		searchPanel.searchField.setText(value);
		searchPanel.typeField.setSelectedIndex(type);
		searchPanel.searchButton.doClick();
	
		//Checking the number of results
		assertEquals(1, searchPanel.searchResults.length);
		TableModel model = searchPanel.searchRoomTable.getModel();
		assertEquals(1, model.getRowCount());
		
		//Checking the consistency with the room
		Room room = (Room) searchPanel.searchResults[0];
		compare_information(room, model, 0);	
	}
		
	@Test
	public void test_searchPanel_no_booked_room(){
		
		//Mock click action of searchPanel.searchButton
		searchPanel.searchField.setText(value);
		searchPanel.typeField.setSelectedIndex(type);
		searchPanel.searchButton.doClick();
		
		assertEquals(0, searchPanel.searchResults.length);
	}
	
	@Test
	public void test_searchPanel_null_room(){			
		
		// Booking one room
		String checkin = manager.checkIn("X12345678", "Gates", "Business", "Microsoft", dateIn, false, "", manager.getRoom(0, 0));
		assertEquals("Success", checkin);
		
		//Mock click action of searchPanel.searchButton
		searchPanel.searchField.setText(value);
		searchPanel.typeField.setSelectedIndex(type);
		searchPanel.searchButton.doClick();
		
		//Checking the number of results
		assertEquals(1, searchPanel.searchResults.length);
		TableModel model = searchPanel.searchRoomTable.getModel();
		assertEquals(1, model.getRowCount());
	
		// Replacing the found room by a null element
		searchPanel.searchResults[0] = null;
	
		// Should return null whatever the value requested is
		assertNull(model.getValueAt(0, 0)); // FIXME rising an error (line 147 of SearchPanel.java)
	}

	/**
	 * 
	 * Function checking the consistency between each display on the screen from the TableModel and the 
	 * room extracted from the results.
	 * 
	 * @param room - Room from the results of the search
	 * @param model - Model 
	 * @param tableLine - Line of the model to be checked
	 */
	private static void compare_information(Room room, TableModel model, int tableLine){
		assertEquals(room.getFloorNo() + "-" + room.getRoomNo(), model.getValueAt(tableLine, 0).toString());
		assertEquals(room.getTypeString(), model.getValueAt(tableLine, 1).toString());
		assertEquals(room.getCapacity(), Integer.parseInt(model.getValueAt(tableLine, 2).toString()));
		assertEquals(room.getRate(), Double.parseDouble(model.getValueAt(tableLine, 3).toString()), 0.001);
		if (room.getType() == 1) {assertEquals("N/A", model.getValueAt(tableLine, 4).toString());}
		else if (room.getOccupation().isDataServiceRequired()) {assertEquals("In used", model.getValueAt(tableLine, 4).toString());}
		else {assertEquals("Not in used", model.getValueAt(tableLine, 4).toString());}
		assertEquals(room.getOccupation().getEthernetAddress(), model.getValueAt(tableLine, 5).toString());
		assertEquals(room.getOccupation().getOccupant().getName(), model.getValueAt(tableLine, 6).toString());
		assertEquals(room.getOccupation().getOccupant().getType(), model.getValueAt(tableLine, 7).toString());
		assertEquals(room.getOccupation().getOccupant().getID(), model.getValueAt(tableLine, 8).toString());
		assertEquals(room.getOccupation().getOccupant().getCompany(), model.getValueAt(tableLine, 9).toString());
		assertEquals(new SimpleDateFormat("dd-MM-yyyy").format(room.getOccupation().getCheckInDate()), 
				     model.getValueAt(tableLine, 10).toString());
		assertEquals("", model.getValueAt(tableLine, 11).toString());	
	}

}

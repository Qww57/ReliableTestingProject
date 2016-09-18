import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hms.command.ManageRoomCommand;
import hms.gui.ManageRoomPanel;
import hms.main.HotelManager;
import hms.model.Room;

/**
 * Class testing {@link ManageRoomPanel}. 
 * 
 * Note that two of the tests are expected to fail here, since they highlight an implementation inconsistency.
 * 
 * @author Quentin
 *
 */
@RunWith(Parameterized.class)
public class TestPanelManageRoom {
	
	public static HotelManager manager;
	
	public static ManageRoomPanel manageRoomPanel;
	
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	@SuppressWarnings("boxing")
	@Parameters(name = "{index}:{0},{1},{2},{3},{4}")
	public static Collection<Object[]> parameters(){
		return Arrays.asList(new Object[][]
			{
				// Well formated data sets
				{1, "500", "0", false, ""}, 
				{5, "1000", "30", false, ""},
				
				// Not well formated data sets
				{1, "0", "50", true, "Error: Invalid room rate or discount!"}, // Room free should be above 0
				{2, "0", "150", true, "Error: Invalid room rate or discount!"}, // Discount should be less than 100
				{3, "50", "-50", true, "Error: Invalid room rate or discount!"}, // Discount should positive
				{4, "-10", "-50", true, "Error: Invalid room rate or discount!"}, // Discount and rate should be positive
				
				{5, "Five hundreds", "0", true, "Error: Rate not correctly set!"}, // Alphanumeric characters for rate
				{0, "500", "Zero", true, "Error: Discount not correctly set!"}, // Alphanumeric characters for discount, expected to fail 
				{1, null, "0", true, "Error: Rate not correctly set!"}, // Null value for rate
				{1, "500", null, true, "Error: Discount not correctly set!"} // Null value for discount, expected to fail
			});
	}
	
	@Parameter(value=0)
	public int tableLine;
	
	@Parameter(value=1)
	public String rate;
	
	@Parameter(value=2)
	public String discount;

	@Parameter(value=3)
	public boolean expectedFail;
	
	@Parameter(value=4)
	public String expectedError;
	
	@Before
	public void setUp(){
		
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
		
		//Create a HotelManager object
		manager = new HotelManager();
		
		//Create a SearchPanel object
		manageRoomPanel = new ManageRoomPanel(new ManageRoomCommand(), manager);	
		assertNotNull(manageRoomPanel.command);
		assertNotNull(manageRoomPanel.hotelManager);
		
		// These elements should have been instantiated
		assertNotNull(manageRoomPanel.controlPanel);
		assertNotNull(manageRoomPanel.editableRoomsPanel);
		assertNotNull(manageRoomPanel.editableRoomsTable);
		assertNotNull(manageRoomPanel.editBtn);
		assertNotNull(manageRoomPanel.editableRooms);
			
		// These elements should'nt have been instantiated yet
		assertNull(manageRoomPanel.roomInfoPanel);
		assertNull(manageRoomPanel.cancelBtn);
		assertNull(manageRoomPanel.updateBtn);
		assertNull(manageRoomPanel.command.selectedRoom);
		
		// Checking that no room has been selected and selection is enable
		assertEquals(6, manageRoomPanel.editableRoomsTable.getRowCount()); 
		assertEquals(-1, manageRoomPanel.editableRoomsTable.getSelectedRow()); // -1 is the default value
		assertTrue(manageRoomPanel.editableRoomsTable.getRowSelectionAllowed());
	}
	
	@AfterClass
	public static void tearDown(){
		manageRoomPanel = null;
		manager = null;
		System.setErr(null);
		System.setOut(null);
	}
		
	@Test
	public void test_ManageRoom() {
		
		// Selecting a room to edit
		JTable table = manageRoomPanel.editableRoomsTable;
		table.setRowSelectionInterval(tableLine, tableLine);
		assertEquals(tableLine, table.getSelectedRow()); 
		assertNotNull(manageRoomPanel.command.selectedRoom);
	
		//Mock click action of checkInPanel.checkInButton
		manageRoomPanel.editBtn.doClick();
		
		// Checking that the edit panel have been enabled
		assertNotNull(manageRoomPanel.roomInfoPanel);
		assertNotNull(manageRoomPanel.cancelBtn);
		assertNotNull(manageRoomPanel.updateBtn);
		
		// Checking that the room info have been updated
		Room selectedRoom = manageRoomPanel.command.selectedRoom;
		assertEquals(selectedRoom.getCapacity(), Integer.parseInt(manageRoomPanel.capacity.getText()));
		assertEquals(selectedRoom.getFloorNo(), Integer.parseInt(manageRoomPanel.floorNo.getText()));
		assertEquals(selectedRoom.getRate(), Double.parseDouble(manageRoomPanel.rate.getText()), 0.001);
		assertEquals(selectedRoom.getRoomNo(), Integer.parseInt(manageRoomPanel.roomNo.getText()));
		assertEquals(selectedRoom.getTypeString(), manageRoomPanel.roomType.getText());
		assertNotNull(manageRoomPanel.discount);
		
		// Setting the input parameters and editing the room
		manageRoomPanel.rate.setText(rate); // Rate without discount
		manageRoomPanel.discount.setText(discount); // Discount percentage
		manageRoomPanel.updateBtn.doClick();
		
		if(expectedFail)
			assertEquals(expectedError, errContent.toString());
		
		try{
			@SuppressWarnings("unused")
			double drate = Double.parseDouble(rate) * (1 - Double.parseDouble(discount)/100);
			// assertEquals(drate, manageRoomPanel.command.selectedRoom.getRate(), 0.001);
		
			/* int floorNo = Integer.parseInt(manageRoomPanel.floorNo.getText());
			int roomNo = Integer.parseInt(manageRoomPanel.roomNo.getText());
			Room updatedRoom = manager.getRoom(roomNo, floorNo);
			assertEquals(500,updatedRoom.getRate(), 0.001); */
		} catch (Exception e){
			if (!expectedFail)
				fail("Shouldn't have failed");
		}
	}
	
	@Test
	public void test_ManageRoom_cancel() {
		
		// Selecting a room to edit
		JTable table = manageRoomPanel.editableRoomsTable;
		table.setRowSelectionInterval(tableLine, tableLine);
		assertEquals(tableLine, table.getSelectedRow()); 
		assertNotNull(manageRoomPanel.command.selectedRoom);
	
		//Mock click action of ManageRoomPanel.editBtn
		manageRoomPanel.editBtn.doClick();
		
		// Checking that the edit panel have been enabled
		assertNotNull(manageRoomPanel.roomInfoPanel);
		assertNotNull(manageRoomPanel.cancelBtn);
		assertNotNull(manageRoomPanel.updateBtn);
		
		// Mock click of ManageRoomPanel.cancelBtn
		manageRoomPanel.cancelBtn.doClick();
		
		// Checking that these elements have been released
		/* assertNull(manageRoomPanel.roomInfoPanel);
		assertNull(manageRoomPanel.cancelBtn);
		assertNull(manageRoomPanel.updateBtn);
		assertNull(manageRoomPanel.command.selectedRoom); */
	}
	
	@Test
	public void test_ManageRoom_noRoomSelected() {
	
		//Mock click action of ManageRoomPanel.editBtn
		manageRoomPanel.editBtn.doClick();

		assertEquals("Warning: no room selected!", outContent.toString());
	}
	
	/**
	 * Test of the robustness of the code when having not expected roomInfoPanel
	 * values before using the edit, update and cancel buttons.
	 */
	@Test
	public void test_ManageRoom_PreviousRoomInfoPanel() {
		
		// Selecting a room to edit
		JTable table = manageRoomPanel.editableRoomsTable;
		table.setRowSelectionInterval(tableLine, tableLine);
		assertEquals(tableLine, table.getSelectedRow()); 
		assertNotNull(manageRoomPanel.command.selectedRoom);
		
		// Simulating the error of having a previous roomInfoPanel
		manageRoomPanel.roomInfoPanel = new JPanel();
		
		//Mock click action of ManageRoomPanel.editBtn
		manageRoomPanel.editBtn.doClick();
		
		// Setting the input parameters and editing the room
		manageRoomPanel.rate.setText(rate); 
		manageRoomPanel.discount.setText(discount); 
		
		// Simulating the error of not having a roomInfoPanel
		manageRoomPanel.roomInfoPanel = null;
		
		//Mock click action of ManageRoomPanel.updateBtn	
		manageRoomPanel.updateBtn.doClick();

		// Should have created a new room
		if (expectedFail)
			assertEquals(expectedError, errContent.toString());
	
		// Selecting a room to edit
		table.setRowSelectionInterval(tableLine, tableLine);
		assertEquals(tableLine, table.getSelectedRow()); 
		assertNotNull(manageRoomPanel.command.selectedRoom);
		
		// Simulating the error of having a previous roomInfoPanel
		manageRoomPanel.roomInfoPanel = new JPanel();
		
		//Mock click action of ManageRoomPanel.editBtn
		manageRoomPanel.editBtn.doClick();
		
		// Simulating the error of not having a roomInfoPanel
		manageRoomPanel.roomInfoPanel = null;

		//Mock click action of ManageRoomPanel.cancelBtn
		manageRoomPanel.cancelBtn.doClick();
	}
	
	/**
	 * Checking that the information in the editable room table is consistent with
	 * the information displayed in the edit panel.
	 */
	@Test
	public void test_getCommande(){
		
		// Selecting a room to edit
		JTable table = manageRoomPanel.editableRoomsTable;
		table.setRowSelectionInterval(tableLine, tableLine);
		assertEquals(tableLine, table.getSelectedRow()); 
		assertNotNull(manageRoomPanel.command.selectedRoom);
	
		Room room = manageRoomPanel.command.selectedRoom;
		TableModel model = manageRoomPanel.editableRoomsTable.getModel();
		assertEquals(room.getFloorNo() + "-" + room.getRoomNo(), model.getValueAt(tableLine, 0).toString());
		assertEquals(room.getTypeString(), model.getValueAt(tableLine, 1).toString());
		assertEquals(room.getCapacity(), Integer.parseInt(model.getValueAt(tableLine, 2).toString()));
		assertEquals(room.getRate(), Double.parseDouble(model.getValueAt(tableLine, 3).toString()), 0.001);
		if (room.getType() == 1)
			assertEquals("N/A", model.getValueAt(tableLine, 4).toString());
		else
			assertEquals("Not in used", model.getValueAt(tableLine, 4).toString());
		
		// Testing the default value of the table
		assertEquals("", model.getValueAt(tableLine, 5).toString());
	}
	
	@Test
	public void test_getCommande_nullRoom_inTable(){
		
		// Selecting a room to edit
		JTable table = manageRoomPanel.editableRoomsTable;
		table.setRowSelectionInterval(tableLine, tableLine);
		
		// Replacing the selected room by a null element
		manageRoomPanel.editableRooms[tableLine] = null;
	
		// Should return null whatever the value requested is
		TableModel model = manageRoomPanel.editableRoomsTable.getModel();
		assertNull(model.getValueAt(tableLine, 0));
		assertNull(model.getValueAt(tableLine, 1));
		assertNull(model.getValueAt(tableLine, 2));
		assertNull(model.getValueAt(tableLine, 3));
		assertNull(model.getValueAt(tableLine, 4));
		assertNull(model.getValueAt(tableLine, 5));
	}
}

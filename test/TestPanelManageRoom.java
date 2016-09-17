import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JTable;

import org.junit.Test;

import hms.command.ManageRoomCommand;
import hms.gui.ManageRoomPanel;
import hms.main.HotelManager;
import hms.model.Room;

public class TestPanelManageRoom {
	
	@Test
	public void test_ManageRoom() {
		//Create a HotelManager object
		HotelManager manager = new HotelManager();
		
		//Create a SearchPanel object
		ManageRoomPanel manageRoomPanel = new ManageRoomPanel(new ManageRoomCommand(), manager);	
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
		
		// Selecting a room to edit
		JTable table = manageRoomPanel.editableRoomsTable;
		
		// Checking that no room has been selected and selection is enable
		assertEquals(6, table.getRowCount()); 
		assertEquals(-1, table.getSelectedRow()); // -1 is the default value
		assertTrue(table.getRowSelectionAllowed());
		
		// Selecting a room with the second line of the table
		int linenumber = 1;
		table.setRowSelectionInterval(linenumber, linenumber);
		assertEquals(linenumber, table.getSelectedRow()); 
		assertNotNull(manageRoomPanel.command.selectedRoom);
	
		//Mock click action of checkInPanel.checkInButton
		manageRoomPanel.editBtn.doClick();
		
		// Checking that the edit panel have been enabled
		assertNotNull(manageRoomPanel.roomInfoPanel);
		assertNotNull(manageRoomPanel.cancelBtn);
		assertNotNull(manageRoomPanel.updateBtn);
		
		// Checking that the room have been updated
		Room selectedRoom = manageRoomPanel.command.selectedRoom;
		assertEquals(selectedRoom.getCapacity(), Integer.parseInt(manageRoomPanel.capacity.getText()));
		assertEquals(selectedRoom.getFloorNo(), Integer.parseInt(manageRoomPanel.floorNo.getText()));
		assertEquals(selectedRoom.getRate(), Double.parseDouble(manageRoomPanel.rate.getText()), 0.001);
		assertEquals(selectedRoom.getRoomNo(), Integer.parseInt(manageRoomPanel.roomNo.getText()));
		assertEquals(selectedRoom.getTypeString(), manageRoomPanel.roomType.getText());
		assertNotNull(manageRoomPanel.discount);
		
		// Setting the input parameters and editing the room
		manageRoomPanel.rate.setText("500"); // Rate without discount
		manageRoomPanel.discount.setText("0"); // Discount percentage
		manageRoomPanel.updateBtn.doClick();
		
		assertEquals(500, manageRoomPanel.command.selectedRoom.getRate(), 0.001);
	
		/* int floorNo = Integer.parseInt(manageRoomPanel.floorNo.getText());
		int roomNo = Integer.parseInt(manageRoomPanel.roomNo.getText());
		Room updatedRoom = manager.getRoom(roomNo, floorNo);
		assertEquals(500,updatedRoom.getRate(), 0.001); */
	}
	
	@Test
	public void test_ManageRoom_cancel() {
		//Create a HotelManager object
		HotelManager manager = new HotelManager();
		
		//Create a SearchPanel object
		ManageRoomPanel manageRoomPanel = new ManageRoomPanel(new ManageRoomCommand(), manager);	
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
		
		// Selecting a room to edit
		JTable table = manageRoomPanel.editableRoomsTable;
		
		// Checking that no room has been selected and selection is enable
		assertEquals(6, table.getRowCount()); 
		assertEquals(-1, table.getSelectedRow()); // -1 is the default value
		assertTrue(table.getRowSelectionAllowed());
		
		// Selecting a room with the second line of the table
		int linenumber = 1;
		table.setRowSelectionInterval(linenumber, linenumber);
		assertEquals(linenumber, table.getSelectedRow()); 
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
}

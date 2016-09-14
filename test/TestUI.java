import static org.junit.Assert.*;

import java.awt.event.WindowListener;

import org.junit.Test;

import hms.command.CheckInCommand;
import hms.command.CheckOutCommand;
import hms.command.ManageRoomCommand;
import hms.command.SearchCommand;
import hms.gui.UI;
import hms.main.HotelManager;

/**
 * Tests for the {@link UI} class defining the main model of the UI.
 * 
 * This class targets to test the following properties of the UI:
 *  - Initialization  of the UI and its class members
 *  - Testing that the default command is CheckIn
 *  - Changing the current command from UI 
 *  - TODO Changing the design of the UI 
 *  - TODO Closing the window
 * 
 * @author Quentin
 *
 */
public class TestUI {
	
	@Test
	public void test_UI(){
		//Create a HotelManager object
		HotelManager manager = new HotelManager();
		
		//Create a UI object
		UI ui = new UI(manager);
		
		// Testing the initialization of class members
		assertNotNull(ui.hotelManager);
		assertNotNull(ui.menuBar);
		assertNotNull(ui.toolBarPanel);
		assertNotNull(ui.checkIn);
		assertNotNull(ui.checkOut);
		assertNotNull(ui.manageRooms);
		assertNotNull(ui.search);
		assertNotNull(ui.currentCommand);
		assertNotNull(ui.lookAndFeelMenu);
		assertNotNull(ui.tableView);
		
		// Testing the default definition of the current command 
		assertEquals(CheckInCommand.class, ui.currentCommand.getClass());
		
		// Trying to change the value of the command using the check boxes of the interface
		ui.checkOut.doClick();
		assertEquals(CheckOutCommand.class, ui.currentCommand.getClass());
		ui.search.doClick();
		assertEquals(SearchCommand.class, ui.currentCommand.getClass());
		ui.manageRooms.doClick();
		assertEquals(ManageRoomCommand.class, ui.currentCommand.getClass());
		ui.checkIn.doClick();
		assertEquals(CheckInCommand.class, ui.currentCommand.getClass());
				
		// TODO Testing to change the menu color
		ui.lookAndFeelMenu.doClick();
		ui.lookAndFeelMenu.getComponentListeners();		
		// assertNotNull(ui.lookAndFeelMenu.getActionMap()); 
		// ActionListener[] actionListeners = ui.lookAndFeelMenu.getActionListeners(); 
		// ItemListener[] itemListeners = ui.lookAndFeelMenu.getItemListeners();
		
		// Closing the window
		WindowListener[] wListeners = ui.getWindowListeners();
		assertEquals("Wrong number of windows listeners", 1, wListeners.length);
		// TODO find the code to call line 69
		ui.dispose();
	}
}

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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

import hms.command.CheckInCommand;
import hms.gui.CheckInPanel;
import hms.main.HotelManager;
import hms.model.Room;

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
	
	@Parameters(name = "{index}:{0},{1},{2},{3},{4},{5},{6}")
	public static Collection<Object[]> parameters(){
		return Arrays.asList(new Object[][]
			{
			// Well formated data sets
			{"X12345678", "Gates", "Standard", "Microsoft", dateIn, false, ""},
			{"X12345678", "Gates", "Business", "Microsoft", dateIn, true, "01:23:45:67:89:ab"},
			
			// Data sets with not allowed null inputs
			{null, "Gates", "Business", "Microsoft", dateIn, false, ""}, 
			{"X12345678", null, "Business", "Microsoft", dateIn, false, ""},
			{"X12345678", "Gates", null, "Microsoft", dateIn, false, ""},
			{"X12345678", "Gates", "Business", null, dateIn, false, ""},
			{"X12345678", "Gates", "Standard", "Microsoft", null, false, ""},
			{"X12345678", "Gates", "Standard", "Microsoft", dateIn, false, null},
			
			// Data sets with not allowed empty inputs
			{"", "Gates", "Business", "Microsoft", dateIn, false, ""},
			{"X12345678", "", "Business", "Microsoft", dateIn, false, ""},
			{"X12345678", "Gates", "Business", "", dateIn, false, ""},
			
			// Data set with inconsistent information
			{"X12345678", "Gates", "Standard", "Microsoft", dateIn, true, ""},
			
			// Data set with invalid room type
			{"X12345678", "Gates", "Luxuous", "Microsoft", dateIn, false, ""},
			
			// Data set with invalid ID
			{"X12345", "Gates", "Business", "Microsoft", dateIn, false, ""},
			
			// Data set with invalid ethernetAdress
			{"X12345678", "Gates", "Business", "Microsoft", dateIn, true, "XXX"}
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
	
	/**
	 * Test of a presidential room
	 */
	@Test
	public void test() {
		
		Room room = manager.getRoom(0, 0);
		
		CheckInCommand cmd = new CheckInCommand(manager);
		
		cmd.setID(ID);
		cmd.setName(name);
		cmd.setType(type);
		cmd.setCompany(company);
		cmd.setCheckInDate(dateIn);
		cmd.setDataServiceRequired(dataServiceRequired);
		cmd.setEthernetAddress(ethernetAddress);
		cmd.setSelectedRoom(room);
		
		//Create a CheckInPanel object
		CheckInPanel checkInPanel = new CheckInPanel(cmd, manager);
		
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
		
		//Mock click action of checkInPanel.checkInButton
		checkInPanel.checkInButton.doClick();
		
		// Replacing by the default values
		if(ID == null){ID = "";}
		if(name == null){name = "";}
		if(type == null){type = "Standard";}
		
		assertEquals(name, checkInPanel.nameField.getText());
		assertEquals(ID, checkInPanel.IDField.getText());
		assertEquals(checkInPanel.typeField.getSelectedItem(), type);
	}
}	
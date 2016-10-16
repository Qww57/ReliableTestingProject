import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	
	// Tests on the hotel manager
	TestHotelManager.class,
	TestHotelManagerCheckIn.class,
	TestHotelManagerCheckOut.class,
	
	// Tests on the classes of the model package
	TestOccupant.class,
	TestOccupation.class,
	TestRoom.class,
	
	// Tests on the GUI
	TestUI.class, 
	TestPanelCheckIn.class,
	TestPanelCheckOut.class,
	TestPanelSearch.class,
	TestPanelManageRoom.class
})

public class CSIT5100_TestMain {
	//
}

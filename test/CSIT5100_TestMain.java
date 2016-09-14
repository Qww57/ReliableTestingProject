import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	// add your test classes here
	GUITest.class,
	
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
	TestPanelCheckIn.class
})

public class CSIT5100_TestMain {
	//
}

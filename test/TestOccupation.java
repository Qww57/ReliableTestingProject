import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import hms.model.Occupant;
import hms.model.Occupation;

/**
 * Class testing the behavior of the class {@link Occupation}.
 * 
 * @author Quentin
 *
 */
public class TestOccupation {

	@SuppressWarnings("boxing")
	@Test
	public void test() {
		Occupant occupant = new Occupant("X12345678", "Standard", "Gates", "Microsoft");
		
		Calendar c = GregorianCalendar.getInstance();
		c.set(2016, 11, 8); // 8th of December 2016
		Date dateIn = c.getTime();
		
		String ethernetService = "01:23:45:67:89:ab";
		boolean dataService = true;
		Occupation occupation = new Occupation(dateIn, dataService, ethernetService, occupant);
		
		assertEquals("checkInDate has failed", dateIn, occupation.getCheckInDate());
		assertEquals("getEthernetAddress has failed", ethernetService, occupation.getEthernetAddress());
		assertEquals("isDataServiceRequired has failed", dataService, occupation.isDataServiceRequired());
		assertEquals("getOccupant has failed", occupant, occupation.getOccupant());
	}
}

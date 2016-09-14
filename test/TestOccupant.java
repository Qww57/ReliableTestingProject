import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import hms.model.Occupant;

/**
 * Parameterized class testing the behavior of the class {@link Occupant} with different
 * sets of input containing particularly some null values.
 * 
 * @author Quentin
 *
 */
@RunWith(Parameterized.class)
public class TestOccupant {

	@Parameters(name = "{index}:{0},{1},{2},{3}")
	public static Collection<Object[]> parameters(){
		return Arrays.asList(new Object[][]
			{
				// well formated input
				{"X12345678", "Standard", "Gates", "Microsoft"},
				
				// input which are not correctly formatted
				{null, "Standard", "Gates", "Microsoft"},
				{"X12345678", null, "Gates", "Microsoft"},
				{"X12345678", "Standard", null, "Microsoft"},
				{"X12345678", "Standard", "Gates", null},
				{ null,  null, null, null}
			});
	}
	
	@Parameter(value=0)
	public String ID;
	
	@Parameter(value=1)
	public String type;
	
	@Parameter(value=2)
	public String name;
	
	@Parameter(value=3)
	public String company;
	
	@Test
	public void test() {
		Occupant occupant = new Occupant(ID, type, name, company);
		assertEquals("ID has failed", ID, occupant.getID());
		assertEquals("type has failed", type, occupant.getType());
		assertEquals("name has failed", name, occupant.getName());
		assertEquals("company has failed", company, occupant.getCompany());
		
		// Checking that the strings shows the important information
		if (ID != null && name != null && company != null){
			assertTrue(occupant.toString().contains(ID));
			assertTrue(occupant.toString().contains(name));
			assertTrue(occupant.toString().contains(company));
		}
		else{
			try{
				assertTrue(occupant.toString().contains(ID));
				assertTrue(occupant.toString().contains(name));
				assertTrue(occupant.toString().contains(company));
				fail("toString should have raised an exception");
			} catch(Exception e){
				// Expected behavior
			}
		}
	}
}

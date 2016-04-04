package com.saltside.common.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.saltside.common.persistence.PersistentObject;
import com.saltside.common.persistence.TestDatabaseGroup;

public class BirdTest {
	public static final String BIRD_DB_NAME = "Bird";
	public static Bird birdRead=null, birdList1=null, birdList2=null, birdList3=null;
	
	
	@BeforeClass
	public static void setup()
	{
		TestDatabaseGroup.loadDatabase(BIRD_DB_NAME);	
	}

	@AfterClass
	public static void tearDown()
	{
		TestDatabaseGroup.unloadDatabase(BIRD_DB_NAME);
	}
	
	public static Bird buildTestObject(){
		Bird b=new Bird();
		
		b.setFamily("testFamily");
		b.setName("testName");
		Set<String> continents=new HashSet<String>();
		continents.add("Africa");
		continents.add("Asia");
		b.setContinents(continents);
		b.setVisible(true);		
		return b;
	}
	
	@Test	
	public void testGetBird() throws Exception{
		birdRead=BirdTest.buildTestObject();
		//Try to save birdRead built above
		birdRead=PersistentObject.save(Bird.class, birdRead);
		
		//Try to read the saved bird above
		Bird birdrr=Bird.getBird(birdRead.getId());
		
		assertNotNull("bird not returned from read call", birdrr);		
		assertEquals("Bird id does not match", birdRead.getId(), birdrr.getId());
		assertEquals("Bird name does not match", birdRead.getName(), birdrr.getName());
		assertEquals("Bird family does not match", birdRead.getFamily(), birdrr.getFamily());
		assertEquals("Bird continents does not match", birdRead.getContinents(), birdrr.getContinents());
		assertEquals("Bird visibility does not match", birdRead.getVisible(), birdrr.getVisible());
		assertEquals("Bird create date does not match", birdRead.getAdded(), birdrr.getAdded());	
		
	}
	
	@Test	
	public void testGetAlBirds() throws Exception{

		birdList1 = BirdTest.buildTestObject();
		birdList2 = BirdTest.buildTestObject();
		birdList3 = BirdTest.buildTestObject();

		// Try to save list birds built above
		birdList1 = PersistentObject.save(Bird.class, birdList1);
		birdList2 = PersistentObject.save(Bird.class, birdList2);
		birdList3.setVisible(false);
		birdList3 = PersistentObject.save(Bird.class, birdList3);

		// Now read all visible birds
		List<Bird> birdList = Bird.getAllBirds(true, 0, 0);
		Bird bird1 = birdList.get(0);
		Bird bird2 = birdList.get(1);
		assertEquals("Wrong number of results", 2, birdList.size());
		assertNotNull("bird not returned from list all call", bird1);
		assertNotNull("bird not returned from list all call", bird2);

		assertEquals("id does not match", birdList1.getId(), bird1.getId());
		assertEquals("id does not match", birdList2.getId(), bird2.getId());

		assertEquals("name does not match", birdList1.getName(),
				bird1.getName());
		assertEquals("name does not match", birdList2.getName(),
				bird2.getName());

		assertEquals("family does not match", birdList1.getFamily(),
				bird1.getFamily());
		assertEquals("family does not match", birdList2.getFamily(),
				bird2.getFamily());

		// List visible birds
		birdList = Bird.getAllBirds(false, 0, 0);
		Bird bird3 = birdList.get(0);
		assertEquals("Wrong number of results", 1, birdList.size());
		assertNotNull("bird not returned from list all call", bird3);
		assertEquals("name does not match", birdList3.getName(),
				bird3.getName());
		assertEquals("family does not match", birdList3.getFamily(),
				bird3.getFamily());	
		
	}
	

}

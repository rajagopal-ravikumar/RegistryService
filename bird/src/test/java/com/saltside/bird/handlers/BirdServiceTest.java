package com.saltside.bird.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.saltside.common.entity.Bird;
import com.saltside.common.entity.BirdTest;
import com.saltside.common.persistence.PersistentObject;
import com.saltside.common.persistence.TestDatabaseGroup;
import com.saltside.common.rest.RestResponse;

public class BirdServiceTest {
	public static final String BIRD_DB_NAME = "Bird";
	public static Bird birdSave=null, birdRead=null, birdDelete, birdList1, birdList2, birdList3=null;
	
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
	
	@Test
	public void testBirdServiceCreate() throws Exception{
		/*
		 * Use the test object birdSave to POST the json to REST API
		 * and verify the contents from response to match the object
		 */
		birdSave=BirdTest.buildTestObject();
		BirdService bs=new BirdService();
		Response r=bs.create(birdSave);
		assertEquals("Wrong status code", r.getStatus(),201);
		RestResponse rr=(RestResponse)r.getEntity();
		Bird birdrr=(Bird)rr.getEntities().get(0);
		assertNotNull("bird not returned from save call", birdrr);
		assertNotNull(birdrr.getId());	
		assertNotNull(birdrr.getAdded());
		assertNotNull(birdrr.getContinents());
		assertEquals("continents returned from the service does not match the input json continents",
				birdSave.getContinents(), birdrr.getContinents());
		assertNotNull(birdrr.getFamily());
		assertEquals("family returned from the service does not match the input json family",
				birdSave.getFamily(), birdrr.getFamily());
		assertNotNull(birdrr.getVisible());
		assertEquals("visibility returned from the service does not match the input json visibility",
				birdSave.getVisible(), birdrr.getVisible());	
		
		
		birdRead=BirdTest.buildTestObject();
		
		/*
		 * POST the json to REST API 
		 * without the mandatory fields in it and verify the response
		 */
		
		//POST json without continents
		birdRead.setContinents(null);
		r=bs.create(birdRead);
		assertEquals("Wrong status code", 400, r.getStatus());
		rr=(RestResponse)r.getEntity();
		assertNotNull(rr.getErrors());
		assertEquals("Rest Response ERRORS field does not contain valid information", true, rr.getErrors().size()>0);
		
		
		//POST json without name		
		Set<String> continents=new HashSet<String>();
		continents.add("Africa");
		continents.add("Asia");
		birdRead.setContinents(continents);
		birdRead.setName(null);
		r=bs.create(birdRead);
		assertEquals("Wrong status code", 400, r.getStatus());
		rr=(RestResponse)r.getEntity();
		assertNotNull(rr.getErrors());
		assertEquals("Rest Response ERRORS field does not contain valid information", true, rr.getErrors().size()>0);
		
		
		//POST json without family		
		birdRead.setName("testName");
		birdRead.setFamily(null);
		r=bs.create(birdRead);
		assertEquals("Wrong status code", 400, r.getStatus());
		rr=(RestResponse)r.getEntity();
		assertNotNull(rr.getErrors());
		assertEquals("Rest Response ERRORS field does not contain valid information", true, rr.getErrors().size()>0);
		
		
		//POST json without visible parameter - It should default to false.		
		birdRead.setVisible(new Boolean(null));
		birdRead.setFamily("testFamily");
		r=bs.create(birdRead);
		assertEquals("Wrong status code", 201, r.getStatus());
		rr=(RestResponse)r.getEntity();
		birdrr=(Bird)rr.getEntities().get(0);
		assertEquals("bird visibility is wrong", false, birdrr.getVisible());
		
		
	}
	
	@Test
	public void testBirdServiceRead() throws Exception{
		
		birdRead=BirdTest.buildTestObject();
		//Try to save birdRead built above
		birdRead=PersistentObject.save(Bird.class, birdRead);
		
		
		//Now try to READ birdRead from Bird Registry API
		BirdService bs=new BirdService();
		Response resp=bs.read(birdRead.getId());		
		assertEquals("Wrong status code", 200, resp.getStatus());
		
		RestResponse rr=(RestResponse)resp.getEntity();
		Bird birdrr=(Bird)rr.getEntities().get(0);
		
		assertNotNull("bird not returned from read call", birdrr);		
		assertEquals("Bird id does not match", birdRead.getId(), birdrr.getId());
		assertEquals("Bird name does not match", birdRead.getName(), birdrr.getName());
		assertEquals("Bird family does not match", birdRead.getFamily(), birdrr.getFamily());
		assertEquals("Bird continents does not match", birdRead.getContinents(), birdrr.getContinents());
		assertEquals("Bird visibility does not match", birdRead.getVisible(), birdrr.getVisible());
		assertEquals("Bird create date does not match", birdRead.getAdded(), birdrr.getAdded());	
		
	}
	
	@Test
	public void testBirdServiceDelete() throws Exception{
		
		birdDelete=BirdTest.buildTestObject();
		
		//Try to save birdDelete built above
		birdDelete=PersistentObject.save(Bird.class, birdDelete);
		
		
		//Now try to DELETE birdDelete from Bird Registry API
		BirdService bs=new BirdService();
		Response resp=bs.delete(birdDelete.getId());
		assertEquals("Wrong status code", 200, resp.getStatus());
		Bird checkBirdDelete=PersistentObject.find(Bird.class, birdDelete.getId());
		assertNull("Bird not deleted" , checkBirdDelete);
		
	}
	
	@Test
	public void testListAllBird() throws Exception{		
		birdList1=BirdTest.buildTestObject();
		birdList2=BirdTest.buildTestObject();
		birdList3=BirdTest.buildTestObject();
		
		//Try to save list birds built above
		birdList1=PersistentObject.save(Bird.class, birdList1);
		birdList2=PersistentObject.save(Bird.class, birdList2);
		birdList3.setVisible(false);
		birdList3=PersistentObject.save(Bird.class, birdList3);
		
		//Now read all visible birds
		BirdService bs=new BirdService();
		Response resp=bs.read(0, 0, true);
		assertEquals("Wrong status code", 200, resp.getStatus());
		String response=resp.getEntity().toString();
		JSONObject job=new JSONObject(response);
		JSONArray jarray=job.getJSONArray("entities");
		String id1=jarray.getString(0);
		String id2=jarray.getString(1);
		int resultCount=Integer.valueOf(job.getString("resultCount"));
		assertEquals("Number of entities in the result is wrong",2, jarray.length());
		assertEquals("wrong result count",2,resultCount );
		assertEquals("id does not match", birdList1.getId(), id1);		
		assertEquals("id does not match", birdList2.getId(), id2);
		
		//test list invisible birds
		resp=bs.read(0, 0, false);
		assertEquals("Wrong status code", 200, resp.getStatus());
		response=resp.getEntity().toString();
		job=new JSONObject(response);
		jarray=job.getJSONArray("entities");
		String id3=jarray.getString(0);
		resultCount=Integer.valueOf(job.getString("resultCount"));
		assertEquals("Number of entities in the result is wrong",1, jarray.length());
		assertEquals("wrong result count",1,resultCount );
		assertEquals("id does not match", birdList3.getId(), id3);	
	}
	
	
}

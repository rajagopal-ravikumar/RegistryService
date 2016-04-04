package com.saltside.common.persistence;

import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.saltside.common.entity.Bird;
import com.saltside.common.entity.BirdTest;

public class PersistentObjectTest {
	public static final String BIRD_DB_NAME = "Bird";
	public static Bird birdSave=null;
	
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
	public void testToJsonString() throws Exception{
		birdSave=BirdTest.buildTestObject();
		birdSave=PersistentObject.save(Bird.class, birdSave);		
		assertNotNull("persistent object is null",birdSave);
		String json=birdSave.toJsonString();
		assertNotNull("json is null", json);
		JSONObject job=new JSONObject(json);
		String id=job.getString("id");
		String name=job.getString("name");
		String family=job.getString("family");
		assertNotNull("id is null",id);
		assertNotNull("name is null",name);
		assertNotNull("family is null",family);
		
		
	}
}

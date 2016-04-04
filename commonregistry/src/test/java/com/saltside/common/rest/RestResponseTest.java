package com.saltside.common.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.saltside.common.entity.Bird;
import com.saltside.common.entity.BirdTest;
import com.saltside.common.exception.RegistryError;
import com.saltside.common.persistence.PersistentObject;
import com.saltside.common.persistence.TestDatabaseGroup;

public class RestResponseTest {
	@Test
	public void responseEntitiesTest() throws Exception {
		RestResponse response = new RestResponse();
		List<PersistentObject> entities = response.getEntities();
		assertNotNull(entities);
		assertEquals(0, entities.size());
		Bird bird1 = BirdTest.buildTestObject();
		response.addEntity(bird1);
		assertEquals(1, response.getEntities().size());
		response = new RestResponse(bird1);
		entities = response.getEntities();
		assertEquals(1, response.getEntities().size());
		Bird bird2 = BirdTest.buildTestObject();
		List<Bird> list = new ArrayList<Bird>();
		list.add(bird1);
		list.add(bird2);
		response = new RestResponse();
		response.addAllEntities(list);
		entities = response.getEntities();
		response = new RestResponse(list);
		entities = response.getEntities();
		assertEquals(2, response.getEntities().size());
	}

	@Test
	public void responseErrorTest() throws Exception {
		RestResponse response = new RestResponse();
		List<RegistryError> errors = response.getErrors();
		assertNotNull(errors);
		assertEquals(0, errors.size());
		assertTrue(response.isSuccessful());
	}

}

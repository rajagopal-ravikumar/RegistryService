package com.saltside.common.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response;

import org.apache.log4j.Priority;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.saltside.common.entity.Bird;
import com.saltside.common.entity.BirdTest;
import com.saltside.common.exception.RegistryError;
import com.saltside.common.exception.RegistryException;
import com.saltside.common.persistence.TestDatabaseGroup;
import com.saltside.common.rest.RestResponse;

public class GenericServiceTest
{
	
	public static final String BIRD_DB_NAME = "Bird";
	
	@BeforeClass
	public static void setupClass()
	{
		TestDatabaseGroup.loadDatabase(BIRD_DB_NAME);	
	}

	@AfterClass
	public static void tearDownClass()
	{
		TestDatabaseGroup.unloadDatabase(BIRD_DB_NAME);		
	}	

	@Test
	public void handleErrorTest()
	{

		GenericService service = new GenericService()
		{
		};

		RegistryException RegistryException = new RegistryException("Error Test", null);
		RegistryException.setDescription("This Is A Test");
		RegistryException.setStatusCode(999);
		RegistryException.setLogLevel(Priority.WARN_INT);
		Response response1 = service.handleRESTError(RegistryException);
		assertNotNull(response1.getEntity());
		assertTrue(((RestResponse) response1.getEntity()).getErrors().get(0) instanceof RegistryError);
		RegistryError error = ((RestResponse) response1.getEntity()).getErrors().get(
				0);
		assertEquals(999, error.getStatus());

		Exception exception = new RuntimeException("Error Test", null);
		Response response2 = service.handleRESTError(exception);
		assertNotNull(response2.getEntity());
		assertTrue(((RestResponse) response2.getEntity()).getErrors().get(0) instanceof RegistryError);
		error = ((RestResponse) response2.getEntity()).getErrors().get(0);
		assertEquals(500, error.getStatus());

		exception = new RegistryException("Error Test", null);
		Response response3 = service.handleRESTError(exception);
		assertNotNull(response3.getEntity());
		assertTrue(((RestResponse) response3.getEntity()).getErrors().get(0) instanceof RegistryError);
		error = ((RestResponse) response3.getEntity()).getErrors().get(0);
		assertEquals("RegistryException", error.getType());

	}
	
}

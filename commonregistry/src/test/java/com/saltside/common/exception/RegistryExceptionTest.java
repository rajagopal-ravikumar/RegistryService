package com.saltside.common.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.saltside.common.exception.RegistryException.RegistryUnknownException;

public class RegistryExceptionTest
{

	@Before
	public void setup()
	{
		
	}

	@Test
	public void RegistryErrorFieldTest() throws Exception
	{
		RegistryError error = new RegistryError();
		error.setMessage("MESSAGE");
		error.setDescription("DESCRIPTION");
		error.setStatus(1234);
		error.setType("TYPE");
		error.setLogLevel(999);
		error.setException(new RegistryException("regexception", null));	
		assertEquals("DESCRIPTION", error.getDescription());
		assertEquals(1234, error.getStatus());
		assertEquals("TYPE", error.getType());
		assertEquals(999, error.getLogLevel());
		assertTrue(error.getException() instanceof RegistryException);
	}

	@Test
	public void RegistryErrorTest() throws Exception
	{
		RegistryException exception = new RegistryException("Error Test", null);
		exception.setDescription("This Is A Test");
		exception.setStatusCode(999);
		exception.setLogLevel(1234);
		RegistryError error = new RegistryError(exception);
		assertEquals(exception, error.getException());		
		assertEquals("Error Test", error.getMessage());
		assertEquals("This Is A Test", error.getDescription());
		assertEquals(999, error.getStatus());
		assertEquals(RegistryException.class.getSimpleName(), error.getType());
		assertEquals(1234, error.getLogLevel());
	}

	@Test
	public void RegistryErrorSerializationTest() throws Exception
	{
		RegistryUnknownException e = new RegistryUnknownException(new RuntimeException(
				"Testing"));
		RegistryError error = new RegistryError(e);
		StringWriter sw = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		MappingJsonFactory jsonFactory = new MappingJsonFactory();
		JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(sw);
		mapper.writeValue(jsonGenerator, error);
		sw.close();
		String jsonString = sw.getBuffer().toString();
		assertNotNull(jsonString);
		assertTrue(jsonString.contains("Testing"));
	}	
}

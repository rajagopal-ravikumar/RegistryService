package com.saltside.common.handlers;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;

public class JsonMappingExceptionMapperTest {
	@Test
	public void testToResponse() throws Exception{
		JsonMappingException ex=new JsonMappingException("test");
		JsonMappingExceptionMapper exm=new JsonMappingExceptionMapper();
		Response r=exm.toResponse(ex);
		assertEquals("wrong status code in response", 400, r.getStatus());
		
	}
}

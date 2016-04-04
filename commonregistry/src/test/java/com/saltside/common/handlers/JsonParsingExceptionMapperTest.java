package com.saltside.common.handlers;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.junit.Test;

public class JsonParsingExceptionMapperTest {
	@Test
	public void testToResponse() throws Exception{
		JsonParseException ex=new JsonParseException(null, null);
		JsonParsingExceptionMapper exm=new JsonParsingExceptionMapper();
		Response r=exm.toResponse(ex);
		assertEquals("wrong status code in response", 400, r.getStatus());
		
	}
}

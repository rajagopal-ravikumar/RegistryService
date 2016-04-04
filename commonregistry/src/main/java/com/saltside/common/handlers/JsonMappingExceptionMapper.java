package com.saltside.common.handlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.JsonMappingException;


/**
 * @author raj
 * Used to send 400 status code to client if the json sent by client
 * cannot be mapped to a resource.
 *
 */
@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
	 @Override
	    public Response toResponse(JsonMappingException exception)  {
	        return Response.status(Response.Status.BAD_REQUEST).build();
	    }
}

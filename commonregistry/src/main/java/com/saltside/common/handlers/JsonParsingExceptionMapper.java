package com.saltside.common.handlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonParseException;

/**
 * @author raj
 * Used to send 400 status code to client if the json sent by client
 * is not a valid json.
 *
 */
@Provider
public class JsonParsingExceptionMapper implements ExceptionMapper<JsonParseException> {
	 @Override
	    public Response toResponse(JsonParseException exception)  {
	        return Response.status(Response.Status.BAD_REQUEST).build();
	    }
}

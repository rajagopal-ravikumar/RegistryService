package com.saltside.common.handlers;

import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.log4j.Priority;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.saltside.common.exception.RegistryError;
import com.saltside.common.exception.RegistryException;
import com.saltside.common.exception.RegistryException.RegistryUnknownException;
import com.saltside.common.logging.LogWrapper;
import com.saltside.common.persistence.PersistentObject;
import com.saltside.common.rest.RestResponse;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;

/**
 * Abstracts many of the common functionalities available across this Registry REST
 * services e.g. error handling and recovery.
 */
public abstract class GenericService
{

	public static final LogWrapper slogger = LogWrapper.getInstance("Generic-Service", GenericService.class);
	public static final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();	
	/**
	 * Process an unexpected exception.
	 */
	public Response handleRESTError(Throwable exception)
	{
		RegistryException sfException;
		if (exception instanceof RegistryException)
		{
			sfException = (RegistryException) exception;
		}
		else
		{
			sfException = new RegistryUnknownException(exception);
		}
		return handleRESTError(sfException);
	}

	/**
	 * Process one of OUR exceptions.
	 */
	public Response handleRESTError(RegistryException exception)
	{
		return processError(new RegistryError(exception));
	}


	 public static final String generateErrorId() {
		 	return UUID.randomUUID().toString();	 	
		 
	  }
	 
	/**
	 * Format and return a JAX-RS response with the given error information.
	 */
	public Response processError(RegistryError error)
	{
	    String errorId = generateErrorId();	    
		if (Priority.ERROR_INT == error.getLogLevel())
		{
			if(error.getException() != null)
		        slogger.error("processError", error.getException(),
		                LogWrapper.pair("error_id", errorId), 
		                LogWrapper.pair("error_description", error.getDescription()), LogWrapper.pair("error_exception", error.getException()));
		    else 
		    	slogger.error("processError", LogWrapper.pair("error_description", error.getDescription()), LogWrapper.pair("error_exception", error.getException()));
		}
		else
		{
			if(error.getException() != null)
                slogger.warn("processError", error.getException(),LogWrapper.pair("error_id", errorId),
                        LogWrapper.pair("error_description", error.getDescription()));
            else 
                slogger.warn("processError", LogWrapper.pair("error_id", errorId),
                        LogWrapper.pair("error_description", error.getDescription()));
		}
		RestResponse entity = new RestResponse(error);
		entity.setErrorId(errorId);	    
	    return Response.status(error.getStatus()).entity(entity).type("application/json").build();
	    	
	}
    
}

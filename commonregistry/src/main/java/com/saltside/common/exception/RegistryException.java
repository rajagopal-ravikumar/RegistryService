package com.saltside.common.exception;


import org.apache.log4j.Priority;

import com.saltside.common.logging.LogWrapper;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * @author raj : Different type of runtime exceptions that can happen while this
 *         registry application is processing the incoming HTTP REST requests.
 *         Each exception type is specific to the nature of root cause and
 *         appropriate description and message and status code to be used in the
 *         response.
 * 
 */
@SuppressWarnings("serial")
public class RegistryException extends RuntimeException
{
	private final static LogWrapper slogger = LogWrapper.getInstance("RegistryException", RegistryException.class);
	
	protected int statusCode = 201;
	protected String description = "";
	private int logLevel = Priority.ERROR_INT;
	
	public int getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public int getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(int logLevel)
	{
		this.logLevel = logLevel;
	}


	public RegistryException()
	{
		this(null, null, 500);//default 500 error
	}

	public RegistryException(String message)
	{
		this(message, null, 500);//default 500 error
	}

	public RegistryException(Throwable cause)
	{
		this(null, cause, 500);//default 500 error
	}
	
	public RegistryException(String message, Throwable cause)
	{
		this(message, cause, 500);//default 500 error
	}

	public RegistryException(String message, Throwable cause, int statusCode)
	{
		super(message, cause);
		this.statusCode = statusCode;	
		slogger.debug("cause="+cause);
		if(cause != null) { 
		    if(cause.getMessage() != null) {
		    setDescription(cause.getMessage());
		    }else{
		        //if no error msg found, setting exception class type
		        setDescription(getMsgAsClassName(cause.getClass()));
		}
		}else{
		    setDescription(message);
	}
	}
	
	private String getMsgAsClassName(Class c){
        if(c == null){
            return null;
        }
        return c.getName().substring(c.getName().lastIndexOf(".")+1);
    }
	
	/**
     * Unknown root cause.
     */
	public static class RegistryUnknownException extends RegistryException
	{
		public RegistryUnknownException(Throwable exception)
		{
			super(exception.getMessage(), exception);
		}

		public RegistryUnknownException(String msg, Throwable exception)
		{
			super(msg, exception);
		}
	}	
	
	
	/**
	 * @author raj
	 * Exception caused due to bad data sent by the client typically resulting in 400 status code
	 * with appropriate message about the field that has the bad data.
	 *
	 */
	public static class BadDataException extends RegistryException
	{
		public BadDataException(String type, String id)
		{
			super("Bad or missing data", null, Status.BAD_REQUEST
					.getStatusCode());
			String desc = "Bad or missing data. Type =" + type + " ID =" + id;
			setDescription(desc);
			setLogLevel(Priority.ERROR_INT);
		}
		public BadDataException(String type)
        {
            super("Bad or missing data", null, Status.BAD_REQUEST
                    .getStatusCode());
            String desc = "Bad or missing data " + type;
            setDescription(desc);
            setLogLevel(Priority.ERROR_INT);
        }
	}
	/**
	 * @author raj
	 * Exception caused because the requested resource is not found in the system.
	 * Typically resulting in 404 status code with appropriate message 
	 *
	 */
	public static class ResourceNotFoundException extends RegistryException
	{
		public ResourceNotFoundException(String type, String id)
		{
			super("Resource not found.", null, Status.NOT_FOUND
					.getStatusCode());
			String desc = "Requested resource not found. Type = " + type
					+ " , ID = " + id ;
			setDescription(desc);
			setLogLevel(Priority.ERROR_INT);
		}
	}
	
	
	/**
	 * @author raj
	 * Exception caused because of a bad query or any other issue that caused
	 * mongo to get back with an error to the application
	 *
	 */
	
	public static class GeneralMongoException extends RegistryException
	{
		public GeneralMongoException(String mongoErrorMessage)
		{
			super(mongoErrorMessage, null, Status.INTERNAL_SERVER_ERROR.getStatusCode());
			setLogLevel(Priority.ERROR_INT);
		}
		
		public GeneralMongoException(String mongoErrorMessage, Throwable t)
        {
            super(mongoErrorMessage, t, Status.INTERNAL_SERVER_ERROR.getStatusCode());
            setLogLevel(Priority.ERROR_INT);
        }
		
	}

	/**
	 * @author raj
	 * Exception caused due to missing data sent by the client typically resulting in 400 status code
	 * with appropriate message about the field that is missing.
	 *
	 */
	
	public static class MissingRequiredFieldException extends RegistryException
	{
		public MissingRequiredFieldException(String field)
		{
			super("Bad data or missing required field : "
					+ field , null, Status.BAD_REQUEST
					.getStatusCode());
			String desc = "Bad data or missing required field : "
					+ field ;
			setDescription(desc);
			setLogLevel(Priority.ERROR_INT);
		}
		
		public MissingRequiredFieldException(String type, String id)
		{
			super("Bad data or missing required field = " + type + ", id= " + id
					, null, Status.BAD_REQUEST
					.getStatusCode());
			String desc = "Bad data or missing required field. Type =" + type 
			+ " ID =" + id;
			setDescription(desc);
			setLogLevel(Priority.ERROR_INT);
		}
	}
	
}

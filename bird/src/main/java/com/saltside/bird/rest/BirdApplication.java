package com.saltside.bird.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.saltside.bird.handlers.BirdService;
import com.saltside.common.handlers.AppHealthCheckService;
import com.saltside.common.handlers.JsonMappingExceptionMapper;
import com.saltside.common.handlers.JsonParsingExceptionMapper;

/**
 * JAX-RS application class that determines which Resources and Providers are
 * made available through the REST server.
 */
public class BirdApplication extends Application
{
	@Override
	public Set<Class<?>> getClasses()
	{
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(BirdService.class);
		classes.add(AppHealthCheckService.class);		
		classes.add(JsonMappingExceptionMapper.class);
		classes.add(JsonParsingExceptionMapper.class);
		return classes;
	}

	@Override
	public Set<Object> getSingletons()
	{
		Set<Object> s = new HashSet<Object>();
		s.add(new org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider());
		return s;
	}

}

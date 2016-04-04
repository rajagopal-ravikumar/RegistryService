package com.saltside.bird.handlers;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.saltside.common.entity.Bird;
import com.saltside.common.exception.RegistryException.BadDataException;
import com.saltside.common.exception.RegistryException.MissingRequiredFieldException;
import com.saltside.common.exception.RegistryException.ResourceNotFoundException;
import com.saltside.common.handlers.GenericService;
import com.saltside.common.logging.LogWrapper;
import com.saltside.common.persistence.PersistentObject;
import com.saltside.common.rest.RestResponse;

import etm.core.monitor.EtmPoint;


@Path("/bird")
public class BirdService extends GenericService
{
	public static final LogWrapper slogger = LogWrapper.getInstance("Bird-Service", BirdService.class);
	

	public BirdService()
	{
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(Bird bird)
	{
		Response resp = null;
		EtmPoint point = null;
		try
		{
			point = etmMonitor.createPoint("BirdService-PUT-create");
			slogger.debug("create_bird_post_json="+bird.toJsonString());
			if(bird.getName()==null || bird.getName().isEmpty() || bird.getName().equals(" ")){
				throw new MissingRequiredFieldException("Bird Name : "+bird.getName());
			}
			if(bird.getFamily()==null || bird.getFamily().isEmpty() || bird.getFamily().equals(" ")){
				throw new MissingRequiredFieldException("Bird Family : "+ bird.getFamily());
			}
			if(bird.getContinents()==null || bird.getContinents().isEmpty()){
				throw new MissingRequiredFieldException("Bird Continents");
			}
			bird = PersistentObject.save(Bird.class, bird);			
			slogger.debug("after_create_bird="+bird.toJsonString());
			RestResponse entity = new RestResponse(bird);
			resp = Response.status(Status.CREATED).entity(entity).type(MediaType.APPLICATION_JSON).build();
		}
		catch (Exception e)
		{
			resp = handleRESTError(e);
		}
		finally
		{
			slogger.logStats("create", point);
		}
		return resp;
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@PathParam("id")  String id)
	{
		Response resp = null;
		EtmPoint point = null;
		slogger.debug("Read Bird", LogWrapper.pair("id", id));
		try
		{
			point = etmMonitor.createPoint("BirdService-GET-Read");			
			Bird bird = Bird.getBird(id);
            if(bird==null) {        
            	throw new ResourceNotFoundException("Bird",id);
            }
			RestResponse entity = new RestResponse(bird);
			resp = Response.ok(entity).type(MediaType.APPLICATION_JSON).build();
		}
		//Id passed is not an ObjectId
		catch(IllegalArgumentException iae){
			throw new BadDataException("Bird id : "+id);
		}
		catch (Exception e)
		{
			resp = handleRESTError(e);
		}
		finally
		{
			slogger.logStats("read", point);
		}
		return resp;
	}
	
	@GET
	@Path("/birds")
	@Produces(MediaType.APPLICATION_JSON)
	public Response read(@DefaultValue("0") @QueryParam("skip") int skip,
            @DefaultValue("0") @QueryParam("limit") int limit,
            @DefaultValue("true") @QueryParam("visible") boolean visible)
	{
		Response resp = null;
		EtmPoint point = null;
		List<Bird> birdList=null;
		slogger.debug("Read all visible birds");
		try
		{
			point = etmMonitor.createPoint("BirdService-GET-visible-Birds");			
			birdList = Bird.getAllBirds(visible, skip, limit);            
			RestResponse entity = new RestResponse(birdList);
			resp = Response.ok(entity.toJsonString(true)).type(MediaType.APPLICATION_JSON).build();
		}
		catch (Exception e)
		{
			resp = handleRESTError(e);
		}
		finally
		{
			slogger.logStats("readAllVisible", point);
		}
		return resp;
	}
	
	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") String id)
	{
        Response resp = null;
		EtmPoint point = null;
		slogger.debug("Delete Bird", LogWrapper.pair("id", id));
		try
		{
			point = etmMonitor.createPoint("BirdService-delete");
			Bird bird = Bird.getBird(id);			
		    if(bird==null) {        
            	throw new ResourceNotFoundException("Bird",id);
            }
		    slogger.debug("Bird to be deleted", LogWrapper.pair("json", bird.toJsonString()));
		    bird.delete();			
			resp = Response.ok().build();
		}
		//Id passed is not an ObjectId
		catch(IllegalArgumentException iae){
			throw new BadDataException("Bird id : "+id);
		}
		catch (Exception e)
		{
			resp = handleRESTError(e);
		}
		finally
		{
			slogger.logStats("delete", point);
		}
		return resp;
	}

}
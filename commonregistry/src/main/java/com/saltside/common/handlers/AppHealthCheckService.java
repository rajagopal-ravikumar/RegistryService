package com.saltside.common.handlers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author raj 
 * HealthCheckService that can be used by monitoring utilities or
 *         load balancer to check if the app is up and running.
 */
@Path("/app")
public class AppHealthCheckService {

    @GET
    @Path("healthcheck")
    public Response ping()
    {
        return Response.ok("OK").build();
    }
}

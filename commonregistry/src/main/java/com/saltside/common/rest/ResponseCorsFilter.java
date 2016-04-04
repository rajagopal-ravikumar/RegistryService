package com.saltside.common.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;


/**
 * @author raj
 * CorsFilter needed to allow requests from *
 */
public class ResponseCorsFilter implements ContainerResponseFilter {

    public ContainerResponse filter(ContainerRequest req, ContainerResponse contResp) {
        
        ResponseBuilder resp = Response.fromResponse(contResp.getResponse());
        
        resp.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
 
        String reqHead = null;
        if (req!=null) {
            reqHead = req.getHeaderValue("Access-Control-Request-Headers");
        }        
        if(null != reqHead && !reqHead.equals("")){
            resp.header("Access-Control-Allow-Headers", reqHead);
        }
 
        contResp.setResponse(resp.build());
        return contResp;
    }


}

package com.saltside.bird.rest;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.saltside.common.rest.CommonInitialization;


/**
 * Base initialization class in Common initializes all the common
 * stuff. This is just here for custom initialization of this
 * particular service.
 *
 */
public class Initialization extends CommonInitialization implements ServletContextListener {

    public static final String SERVICE_NAME = "bird";
    
    /**
     * Called when Tomcat starts up this web service.
     */
    public void contextInitialized(ServletContextEvent contextEvent) {
        super.serviceInitialization(contextEvent,SERVICE_NAME);
    }

    /**
     * Called before Tomcat shuts down this web service.
     */
    public void contextDestroyed(ServletContextEvent contextEvent) {
       
    	super.serviceTermination(contextEvent,SERVICE_NAME);        
    }

}

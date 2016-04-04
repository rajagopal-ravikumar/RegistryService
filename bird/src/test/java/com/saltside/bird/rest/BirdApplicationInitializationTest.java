package com.saltside.bird.rest;

import static org.junit.Assert.assertEquals;

import javax.servlet.ServletContextEvent;

import org.junit.Ignore;

import com.saltside.bird.rest.Initialization;
import com.saltside.common.rest.TestServletContext;


public class BirdApplicationInitializationTest {

    @Ignore
    public void testInit() throws Exception {
        Initialization init = new Initialization();
        TestServletContext context = new TestServletContext();
        context.initParams.put("config", "testConfig.xml");
        context.initParams.put("version", "9.8.7.6");
        context.initParams.put("logConfig", "logConfig.xml");        
        ServletContextEvent event = new ServletContextEvent(context);
        init.contextInitialized(event);
        assertEquals("9.8.7.6",Initialization.getVersionString());
        init.contextDestroyed(event);
    }

}

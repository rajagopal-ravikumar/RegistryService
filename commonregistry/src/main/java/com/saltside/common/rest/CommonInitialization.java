package com.saltside.common.rest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.saltside.common.config.Config;
import com.saltside.common.logging.LogWrapper;
import com.saltside.common.persistence.DatabaseGroup;

import etm.contrib.aggregation.log.Log4jAggregator;
import etm.core.aggregation.Aggregator;
import etm.core.aggregation.BufferedTimedAggregator;
import etm.core.aggregation.RootAggregator;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.SimpleTextRenderer;
import etm.core.timer.ExecutionTimer;
import etm.core.timer.Java15NanoTimer;

/**
 * All services provide their own initialization routine. This class provides
 * initialization of common components. Each service can either call this
 * class's initialization methods, or can initialize common components on their
 * own.
 */
public abstract class CommonInitialization {

    protected static String versionString = null;
    /*
     * App name. For example : BirdService , AnimalService, etc
     */
    protected static String appName=null;

    public static String getVersionString() {
        return versionString;
    }

    public static void setVersionString(String versionString) {
        CommonInitialization.versionString = versionString;
    }
    public static void setAppName(String appName) {
        CommonInitialization.appName = appName;   }

    
    protected static Config config = null;
   
    /*
     * jetm to measure response times of apis/ time taken for a method/block of code to execute.
     */
    protected static EtmMonitor etmMonitor = null;
    protected static LogWrapper slogger = LogWrapper.getInstance("CommonInitialization", CommonInitialization.class);

    /**
     * Common service initialization. Put initialization code into separate
     * methods rather than inline. This way services can selectively override
     * things.
     */
    public void serviceInitialization(ServletContextEvent contextEvent, String serviceName) {
        ServletContext sc = contextEvent.getServletContext();
        //config file name including the path is read from web.xml
        String configFileName = sc.getInitParameter("config");
        setAppName(serviceName);
        setVersionString(sc.getInitParameter("version"));
        initializeConfiguration(serviceName,configFileName);
        
        //log file name including the path is read from web.xml
        String logConfigFileName = sc.getInitParameter("logConfig");
        initializeLogging(logConfigFileName);
        initializeDatabase();
        initializeJetmMonitor();      
    }

    /**
     * Common service termination.
     */
    public void serviceTermination(ServletContextEvent contextEvent, String serviceName) 
    {
    	slogger.log("termination called");
        terminateJetmMonitor();    
        terminateDatabase();
        terminateLogging(serviceName);       
    }
	/*
	 * Configuration initialization
	 */
    public void initializeConfiguration(String appName, String configFile) {
        config = Config.getInstance();
        config.setAppName(appName);
        config.setConfigFile(configFile);
    }

    /**
     * close db connections
     */
    public void terminateDatabase()
    {
    	DatabaseGroup.getInstance().terminateDatabase();
    }
    
   
   /*
   * Logging initialization
   */
    public void initializeLogging(String logConfigFileName) {
        String logLevel = Config.getInstance().getLogLevel();
        BasicConfigurator.configure();
        String log4jFilePath = null;
        

        try {
        	  Logger.getRootLogger().setLevel(Level.toLevel(logLevel));
        	  DOMConfigurator.configureAndWatch(logConfigFileName,120000);
        } catch (Exception e) {
            slogger.error("failed_to_load_log4j", e, LogWrapper.pair("file", log4jFilePath));
        }
       
    }

    public void terminateLogging(String serviceName) {
        slogger.log(serviceName + " Closing Logfiles.");
    }

	/*
	 * Database initialization
	*/
    public void initializeDatabase() 
    {
        Config config = Config.getInstance(); 
        slogger.debug("Initialize MongoDb DatabaseGroup.");
        DatabaseGroup.initializeDatabase(config);
    }   
	 
    /*
	 *Java Execution Timing Monitor (JETM)
	 */     
    public void initializeJetmMonitor() {
        ExecutionTimer timer = new Java15NanoTimer();
        Aggregator aggregator = new BufferedTimedAggregator(new Log4jAggregator(new RootAggregator()));
        BasicEtmConfigurator.configure(true, timer, aggregator);
        etmMonitor = EtmManager.getEtmMonitor();
        etmMonitor.start();
        aggregator.start();
    }

    public void terminateJetmMonitor() {
        if (etmMonitor != null) {
            etmMonitor.render(new SimpleTextRenderer());
            etmMonitor.stop();
        }
        etmMonitor = null;
    }

}

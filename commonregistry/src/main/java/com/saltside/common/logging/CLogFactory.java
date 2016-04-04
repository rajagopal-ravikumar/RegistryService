package com.saltside.common.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
 * @author raj
 * log4j log factory
 *
 */
public class CLogFactory implements LoggerFactory
{
    /**
     * This method is called by Logger.getLogger(name, factory).
     * 
     * @param name name of the logger to create
     * @return new Logger instance
     */
    public Logger makeNewLoggerInstance(String name) {
        return new CLogger(name);
    }

	public Logger getInstance(Class clazz) {
        return Logger.getLogger(clazz.getName(), this);
	}

}

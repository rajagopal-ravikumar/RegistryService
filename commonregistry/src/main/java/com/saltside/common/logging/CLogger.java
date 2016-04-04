package com.saltside.common.logging;

import org.apache.log4j.Logger;

/**
 * Basic version of log4j logger 
 *
 */
public class CLogger extends Logger
{
	public CLogger(String name){
		super(name);
	}

	 private static CLogFactory sFactory = new CLogFactory();
	 
	 public static Logger getLogger(String name) {
	        return Logger.getLogger(name, sFactory);
	   }
	 
	 public static Logger getLogger(Class c) {
	     return Logger.getLogger(c.getName(), sFactory);
	  }
	 
	public void info(Object msg) {
		super.info(msg);
	}
	
	public void debug(Object msg, Throwable th)
	{
		super.debug(msg);
	}
	
	public void debug(Object msg)
	{
		super.debug(msg);
	}
	
	public void warn(Object msg, Throwable th)
	{
		super.warn(msg, th);
	}
	
	public void warn(Object msg)
	{
		super.warn(msg);
	}
	
	public void error(Object msg, Throwable th)
	{
		super.error(msg, th);
	}
	
	public void error(Object msg)
	{
		super.error(msg);
	}
	
	
}


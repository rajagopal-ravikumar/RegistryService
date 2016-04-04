package com.saltside.common.logging;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;

import com.saltside.common.entity.RegistryConstants;

import etm.core.monitor.EtmPoint;


/**
 * @author raj
 * Wrapper to log in key=value pairs.
 *
 */
public class LogWrapper {

    private final Logger logger;
    private final String prefix;
    private String action = null;
    private String message = null;
    private Throwable throwable = null;
    List<Pair> pairList = new ArrayList<LogWrapper.Pair>();

    public LogWrapper(String prefix, Logger logger) {
        this.prefix = prefix;
        this.logger = logger;
    }

    /**
     * @param prefix : prefix to denote the component.
     * @param clazz  : the class called from
     */
    public static LogWrapper getInstance(String prefix, Class<?> clazz) {
        Logger logger = CLogger.getLogger(prefix);
        return new LogWrapper(prefix, logger);
    }

    public static Pair pair(Object name, Object value) {
        if (name == null) {
            name = "null";
        }
        if (value == null) {
            value = "null";
        }
        return new Pair(name.toString(), value.toString());
    }

    public String log(String action, Pair... pairs) {
        String string = list(action, pairs);
        logger.info(string);
        return string;
    }

    public void error(String action, Pair... pairs) {
        logger.error(list(action, pairs));
    }
    
    public void removeAllAppenders(){
        logger.removeAllAppenders();
    }
    
    public void addAppender(RollingFileAppender newAppender){
        logger.addAppender(newAppender);
    }
    
    public void setLevel(Level level){
        logger.setLevel(level);
    }
    
    public void error(String action, Exception e, Pair... pairs) {
    	String string = e == null ? "null" : "\"" + e.getMessage() + "\"";
    	string = list(action, pairs) + ", " + "error_details=" + string;
    	logger.error(string, e);
    }

    public void warn(String action, Pair... pairs) {
        logger.warn(list(action, pairs));
    }
    
    public void warn(String action, Exception e, Pair... pairs) {
    	String string = e == null ? "null" : "\"" + e.getMessage() + "\"";
        string = list(action, pairs) + ", " + "error_details=" + string;
        logger.warn(string, e);
    }

    public void debug(String action, Pair... pairs) {
        if (logger.isDebugEnabled()) {
            logger.debug(list(action, pairs));
        }
    }
    
    public void logStats(String logMsg, EtmPoint point) {
        if(point != null){
            point.collect();  
            log(logMsg, LogWrapper.pair(RegistryConstants.MEASURE_POINT, point.getName()), 
                         LogWrapper.pair(RegistryConstants.TRANSACTION_TIME, point.getTransactionTime()));
        }
         
    }
    
    public void info(String action, Pair... pairs) {
        if (logger.isInfoEnabled()) {
            logger.info(list(action, pairs));
        }
    }

    public boolean isDebugEnabled()
    {
    	return logger.isDebugEnabled();
    }
    
    public boolean isInfoEnabled()
    {
    	return logger.isInfoEnabled();
    }
    
    public boolean isTraceEnabled()
    {
    	return logger.isTraceEnabled();
    }
    
    public String log(String action, Exception e, Pair... pairs) {
        String string = e == null ? "null" : "\"" + e.getMessage() + "\"";
        string = list(action, pairs) + ", " + "error_details=" + string;
        logger.error(string, e);
        return string;
    }
  

    /**
     * @param message String context message
     * @return the logger for stringing operations
     */
    public LogWrapper message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Clear the context message
     * @return the logger for stringing operations
     */
    public LogWrapper clearMessage() {
        this.message = null;
        return this;
    }

    /**
     * @param action
     * @return the logger for stringing operations
     */
    public LogWrapper action(String action) {
        reset();
        this.action = action;
        return this;
    }

    /**
     * Add a name-value pair to log
     * @param name
     * @param value
     * @return the logger for stringing operations
     */
    public LogWrapper add(String name, Object value) {
        pairList.add(pair(name, value.toString()));
        return this;
    }

    /**
     * Add an exception to the log
     * @param t
     * @return the logger for stringing operations
     */
    public LogWrapper add(Throwable t) {
        this.throwable = t;
        return this;
    }

    public String log() {
        if (throwable != null) {
            add("error_details", throwable.toString());
        }
        String string = list(action, pairList);
        if (throwable != null) {
            logger.error(string);
        } else {
            logger.info(string);
        }
        reset();
        return string;        
    }

    /**
     * Same as log() but at debug level.
     */
    public String debug() {
        String string = "";
        if (throwable != null) {
            add("error_details", throwable.toString());
        }
        string = list(action, pairList);
        if (throwable != null) {
            logger.error(string);
        } else if (logger.isDebugEnabled()) {
            logger.debug(string);
        }
        reset();
        return string;
    }

    /**
     * Same as log() but for errors
     */
    public String error() {
        if (throwable != null) {
            add("error_details", throwable.toString());
        }
        String string = list(action, pairList);
        logger.error(string);
        reset();
        return string;        
    }

    private void reset() {
        message = null;
        throwable = null;
        pairList = new ArrayList<LogWrapper.Pair>();        
    }

    private String list(String action, List<Pair> pairs) {
        StringBuilder sb = new StringBuilder();
        List<Pair> list = getPreamble(action);
        if (pairs != null) {
            list.addAll(pairs);
        }
        for (Pair pair : list) {
            if ("LogWrapper_system".equalsIgnoreCase(pair.getName())) {
                sb.append(pair.toString() + ", ");
            } else {
                sb.append(print(pair));
            }
        }
        String string = sb.toString();
        if (string.endsWith(", ")) {
            string = string.substring(0, string.length() - 2);
        }
        return string;
    }

    private String list(String action, Pair[] pairs) {
        List<Pair> list = (pairs == null? Collections.<Pair>emptyList() : new ArrayList<Pair>(Arrays.asList(pairs)));
        return list(action, list);
    }


    private String print(Pair pair) {
        return pair.getName() + "=\"" + pair.getValue() + "\", ";
    }

    private List<Pair> getPreamble(String action) {
        List<Pair> pairs = new ArrayList<Pair>();
        pairs.add(LogWrapper.pair("LogWrapper_system", prefix));
        if(action != null  && !"".equals(action.trim())) {
            pairs.add(LogWrapper.pair("action", action));
        }
        if (message != null) {
            pairs.add(LogWrapper.pair("message", message));
        }
        addPairs(pairs);
        return pairs;
    }

    /**
     * Override if you want to add more specific things to the preamble pairs.
     */
    protected void addPairs(List<Pair> pairs) {
    }

    public static class Pair {

        private final String name;
        private final String value;

        public Pair(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name + "=\"" + value + "\"";
        }
    }
}
package com.saltside.common.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.saltside.common.config.Config;
import com.saltside.common.entity.Bird;
import com.saltside.common.entity.RegistryConstants;
import com.saltside.common.exception.RegistryException;
import com.saltside.common.logging.LogWrapper;
import com.saltside.common.persistence.Database;
import com.saltside.common.persistence.DatabaseGroup;
import com.saltside.common.persistence.PersistentObject;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * @author raj
 * Factory conversion methods to convert dbObjects
 *
 */
public class RegistryFactory
{
	private static final LogWrapper slogger = LogWrapper.getInstance("Registry-Factory", RegistryFactory.class);

	/**
	 * @param cls : class representing the dbobject
	 * @param dbo : dbobject
	 * @return    : Corresponding java object.
	 * @throws Exception
	 */
	public static <T extends PersistentObject> T fromDBObject(
			final Class<T> cls, DBObject dbo) throws Exception 
	{
		slogger.debug("fromDBObject");
		String colName = getCollectionName(cls);
		Database db = DatabaseGroup.getInstance().getDatabase(colName);
		T object = db.fromDBObject(cls, dbo);
		return object;	
	}

	/**
	 * @param cls    : class representing the dbobject
	 * @param lstdbo : list of dbObjects 
	 * @return       :  Corresponding list of java objects.
	 * @throws Exception
	 */
	public static <T extends PersistentObject> List<T> fromDBObject(
			final Class<T> cls, List<DBObject> lstdbo) throws Exception 
	{
		slogger.debug("fromDBObject converting list of dbObjects");
		List<T> results = new ArrayList<T>();		
		if(lstdbo == null || lstdbo.isEmpty()){
			return results;
		}		
		String colName = getCollectionName(cls);
		Database db = DatabaseGroup.getInstance().getDatabase(colName);
		for(DBObject dbo : lstdbo) { 
			T object = db.fromDBObject(cls, dbo);
			results.add(object);		
		}			
		return results;  
	}

	

	public static String getCollectionName(Class<? extends PersistentObject> cls)
	{
		return cls.getSimpleName();		
		
	}	
}

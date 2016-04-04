package com.saltside.common.persistence;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Transient;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.WriteResult;
import com.saltside.common.config.Config;
import com.saltside.common.entity.Bird;
import com.saltside.common.entity.RegistryConstants;
import com.saltside.common.exception.RegistryException.GeneralMongoException;
import com.saltside.common.factories.RegistryFactory;
import com.saltside.common.logging.LogWrapper;
import com.saltside.common.rest.RestResponse;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * Base class for all Domain Objects. Handles automatic serialization and
 * deserialization for the MongoDB persistent store and the JSON/REST web
 * service interface.
 */
@Entity("persistentObject")
@JsonSubTypes(
{ 		@JsonSubTypes.Type(value = Bird.class, name = "Bird")})	
@JsonIgnoreProperties({"@class" })
@XmlSeeAlso({ Bird.class, RestResponse.class })
public abstract class PersistentObject
{
	@Transient
	private static final LogWrapper slogger = LogWrapper.getInstance("Persistent-Object", PersistentObject.class);
	public static final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();

	/** Mongo _id */
	@Id	
	protected String id;

	/** Set to the current date when a Persistent Object is first created. */
	private String added;
	
	/* Supposed to be updated when the corresponding document in the db is updated */
	private String updateDate;
	
	/** Incrementing object version to detect collisions. should be used in updates */
	private long version;

	
	/**
	 * Status field to determine whether object is visible.
	 */
	private Boolean visible;

	
 	
	/**
	 * One default construction set he create date to "now". If this is object
	 * is being deserialized from Mongo then the persistent value will overwrite
	 * this default value.
	 */
	public PersistentObject()
	{
		Date d=new Date();		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		added=df.format(d);	
		visible = false;
	}

	/**
	 * Return the Collection corresponding to this class and the current Tenant.
	 * NEVER go directly to the Database instance for the collection. This
	 * method detects if the collection is new, and sets up indexes.
	 */
	protected DBCollection getCollection()
	{
		String colName = getCollectionName();
		Database db = DatabaseGroup.getInstance().getDatabase(colName);		
		return db.getCollection(colName);
	}	

	
	/**
	 * Return the name of the Mongo Collection for this class. The default is to
	 * use the name of the concrete class. 
	 */
	public String getCollectionName()
	{
		return getCollectionName(this.getClass());
	}

	public static String getCollectionName(Class<? extends PersistentObject> cls)
	{
		return RegistryFactory.getCollectionName(cls);
	}

	@JsonProperty("id")
	public String getId()
	{
		return this.id;
	}
	
	@JsonProperty("id")	
	public void setId(String id)
	{
		this.id = id;
	}

	public Long getVersion()
	{
		return version;
	}

	public void setVersion(Long version)
	{
		this.version = version;
	}
	

	public String getAdded()
	{
		return added;
	}

	public void setAdded(String added)
	{
		this.added = added;
	}

	public String getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(String updateDate)
	{
		this.updateDate = updateDate;
	}

	

	public Boolean getVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	
	// //////////////////////////////
	// ///////// CREATE ///////////
	// //////////////////////////////

	/**
	 * Save a persistent object to the database. The appropriate collection for
	 * the concrete instance is determined by the overloaded "getCollection()"
	 * method.
	 */
	public static <T extends PersistentObject> T save(Class<T> cls,
			PersistentObject pobj)
	{
		EtmPoint point = null;
		try
		{
			point = etmMonitor.createPoint("PersistentObject-POST-Save");
			pobj.preSaveHook();			
            Database db = DatabaseGroup.getInstance().getDatabase(pobj.getCollectionName());
			DBObject dbo = db.toDBObject(pobj);
		    pobj.getCollection().insert(dbo);
			T persistentObject = RegistryFactory.fromDBObject(cls, dbo);
			return persistentObject;
		}
		catch (GeneralMongoException gme)
		{
			slogger.error("PersistentObject.save() GeneralMongoException = "+pobj.toJsonString(), gme);
			throw gme;
		}
		catch (MongoException me)
		{
			throw new GeneralMongoException(me.getMessage(), me);
		}
		catch (Exception e)
		{
			slogger.error("PersistentObject.save() Exception = "+pobj.toJsonString(), e);
			throw new RuntimeException("Failed to create object of type "
					+ cls.getSimpleName(), e);
		}
		finally
		{
			slogger.logStats("save", point);
		}
	}
	
	
	// Subclasses can override to get called just before
	// they are inserted (created) to the db.
	protected void preSaveHook()
	{
		this.updateDate=this.added;
	}

	    
	/**
	 * Retrieve a persistent entity based on it's Mongo OID.
	 */
	public static <T extends PersistentObject> List<T> findAll(Class<T> cls, boolean visible, int skip, int limit)
	throws Exception 
	{
		EtmPoint point = null;
		try
		{
			point = etmMonitor.createPoint("PersistentObject-GET-FindAll");
			String colName = getCollectionName(cls);
			Database db = DatabaseGroup.getInstance().getDatabase(colName);
			List<T> results = new ArrayList<T>();          			    
		    DBCollection collection = db.getCollection(colName);
		    DBObject query=new BasicDBObject();
		    query.put(RegistryConstants.VISIBLE, visible);
		    results = processQuery(collection, query, cls, skip, limit);
		    return results;
			
		}
		catch (MongoException me)
		{
			throw new GeneralMongoException(me.getMessage(), me);
		}
		catch (Exception e)
		{
			slogger.error("PersistentObject.findAll()" ,e, LogWrapper.pair("visible", visible),
					LogWrapper.pair("skip", skip), LogWrapper.pair("limit", limit));
			throw e;
		}
		finally
		{
			slogger.logStats("find", point);
		}
	}	

	public static <T extends PersistentObject> T find(Class<T> cls, String id) throws Exception
	{
		EtmPoint point = null;
		try
		{
			point = etmMonitor.createPoint("PersistentObject-GET-Find");
			String colName = getCollectionName(cls);
			Database db = DatabaseGroup.getInstance().getDatabase(colName);					    
		    DBCollection collection = db.getCollection(colName);
		    DBObject query=new BasicDBObject();
		    query.put(RegistryConstants.ID, new ObjectId(id));							
			List<T> resultList= processQuery(collection, query, cls, 0, 0);
			if(resultList.size()>0){
				return resultList.get(0);
			}			
		}
		catch (MongoException me)
		{
			throw new GeneralMongoException(me.getMessage(), me);
		}
		catch (Exception e)
		{
			slogger.error("PersistentObject.find()" , e , LogWrapper.pair("id", id));
			throw e;
		}
		finally
		{
			slogger.logStats("find", point);
		}
		return null;
	}	


	protected static WriteResult remove(String id, DBCollection collection, DBObject dbObject)
	{
		slogger.debug("PersistentObject.remove()" , LogWrapper.pair("id", id));
		return collection.remove(dbObject);
	}	
	
	protected static void closeCursor(DBCursor cursor) {
	    try {
	    	if(cursor != null) {
	            cursor.close();
	        }
	    }catch(Exception exp){
	        slogger.warn("failedToCloseDBCursor", exp, LogWrapper.pair("cursor", cursor));
	    }
	}			
	
	private static <T extends PersistentObject> List<T> processQuery(
			DBCollection collection, DBObject query, Class<T> cls, int skip, int limit) throws Exception
	{
	
		slogger.debug("processQuery", LogWrapper.pair("query", query), LogWrapper.pair("collection", collection.getName()),
						LogWrapper.pair("skip", skip),LogWrapper.pair("limit", limit));
		DBCursor cursor = null;
		List<T> results = new ArrayList<T>();
		cursor = executeQuery(collection, query, skip, limit);			
		List<DBObject> dbObjList = cursor.toArray();
		closeCursor(cursor);
		results = RegistryFactory.fromDBObject(cls, dbObjList);			
        return results;
        
	}
	
	/**
	 * This method is the main execute find/query method. The concept being is that we only
	 * need one method to do the Query and return a cursor. In order to query Mongo, you
	 * must provide a DBCollection, the query.
	 * 
	 * @param collection The current DBCollection or database to query.
	 * 
	 * @param query The current Query to execute against the DBCollection. The Query contains
	 *              all the required parameters.
	 * 
	 * 
	 * @param skip Skip and Limit are how you paginate in Mongo. If they are defined, then apply
	 *             them to the query.
	 * 
	 * @param limit See Skip for a definition.
	 * 
	 * @return Returns a DBCursor containing the resultset of the applied queries.
	 * @throws Exception 
	 */
	
	private static DBCursor executeQuery(DBCollection collection, DBObject query,
			int skip, int limit) throws Exception
	{
		
		DBCursor cursor = null;
		if (skip >= 0 && limit > 0)
		{
			cursor = find(collection, query).skip(skip).limit(limit);
		}
		else
		{
			cursor = find(collection, query);
		}
		slogger.log("executeQuery",
				LogWrapper.pair("collection", collection.getName()),
				LogWrapper.pair("skip", skip), LogWrapper.pair("limit", limit),
				LogWrapper.pair("query", query));
				
		return cursor;
	}
	
	

	// //////////////////////////////
	// ///////// DELETE ///////////
	// //////////////////////////////

	/**
	 * Delete this specific object from the database.
	 */
	public void delete()
	{
		try
		{
		    DBObject query = new BasicDBObject();
            query.put(RegistryConstants.ID, new ObjectId(this.getId()));
		    remove(this.getId(), getCollection(), query);	
		}
		catch (GeneralMongoException gme)
		{
			throw gme;
		}
		catch (MongoException me)
		{
			throw new GeneralMongoException(me.getMessage(), me);
		}
		catch (Exception e)
		{
			slogger.error("Problem deleting object " + this.getId(), e, LogWrapper.pair("id", this.getId()));
		}
	}
	

	/* Convert this persistent object into a simple JSON string that can be
	 * returned as the result of an API call.
	 */
	public String toJsonString()
	{
		String json = null;	
		try
		{
			StringWriter sw = new StringWriter();
			//SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-DD");
			ObjectMapper mapper = new ObjectMapper();			
			//mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
			//mapper.setDateFormat(df);
			MappingJsonFactory jsonFactory = new MappingJsonFactory();
			JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(sw);
			mapper.writeValue(jsonGenerator, this);
			sw.close();
			json = sw.getBuffer().toString();
		}
		catch (Exception e)
		{
		    slogger.warn("failed to convertString", e);
		}
		return json;
	}
	
	public String responseJson(){	    
	    return this.toJsonString();
	}
	
	public String idJson(){	    
	    return this.getId();
	}

	@Override
	public String toString()
	{
		return "PersistentObject [_id=" + id + ", added=" + added
				+ ", updateDate=" + updateDate + ", version=" + version + "]";
	}

	protected  static DBCursor find(DBCollection collection, DBObject query) throws Exception
    {
        DBCursor cursor = null;      
        if (query==null){
        	cursor=collection.find();
        }else{
            cursor = collection.find(query);     

        }
        return cursor;
    }
}
	
	
	
	
	
	
	
	
	
	
	

	
	
	
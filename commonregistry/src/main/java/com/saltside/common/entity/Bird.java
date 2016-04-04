package com.saltside.common.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonWriteNullProperties;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import com.saltside.common.logging.LogWrapper;
import com.saltside.common.persistence.PersistentObject;

/**
 * Object type for Birds persisted in Mongo
 */
@XmlRootElement
@Entity(value="Bird", noClassnameStored=true)
@JsonWriteNullProperties(false)
public class Bird extends PersistentObject
{
	@Transient
	private final static LogWrapper slogger = LogWrapper.getInstance("Bird", Bird.class);
	private String name;
	private String family;
	private Set<String> continents=new HashSet<String>();
	
	public String getName() {
		return name;
	}
	public String getFamily() {
		return family;
	}
	public Set<String> getContinents() {
		return continents;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public void setContinents(Set<String> continents) {
		this.continents = continents;
	}
	
	/**
	 * @param id : Object id of Bird Resource
	 * @return Bird object with matching id.
	 * @throws Exception
	 */
	public static Bird getBird(String id) throws Exception  
	{
		slogger.info("Get bird", LogWrapper.pair("id", id));
		Bird bird=null;
		if (id == null)
		{
			slogger.debug("id used to retrieve bird is null");
			return null;
		}
		bird = find(Bird.class, id);
		return bird;
	} 
	
	/**
	 * @param visible : true/false
	 * @param skip    : skip value used for pagination
	 * @param limit   : limit value used for pagination
	 * @return   : List of all birds matching the given visible parameters.
	 * if skip and limit are both 0, then all matching bird documents are returned.
	 * @throws Exception
	 */
	public static List<Bird> getAllBirds(boolean visible, int skip, int limit) throws Exception  
	{
		slogger.info("Get all birds", LogWrapper.pair("visible", visible),
				LogWrapper.pair("skip", skip), LogWrapper.pair("limit", limit));
		List<Bird> birdList = new ArrayList<Bird>();
	    birdList = findAll(Bird.class, visible, skip, limit);	
		return birdList;
	} 
}


package com.saltside.common.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.saltside.common.entity.Bird;
import com.saltside.common.exception.RegistryError;
import com.saltside.common.persistence.PersistentObject;

/**
 * Encapsulates both REST data and REST errors and other pieces in the response like
 * entities (items) and resultCount.
 */
@XmlRootElement
public class RestResponse
{
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
	@XmlElement(name = "entity")
	@XmlElementWrapper(name = "entities")
	private ArrayList<PersistentObject> entities = new ArrayList<PersistentObject>();
	private String errorId = null;
	//number of entities in the response
	private Long resultCount = null;

	@XmlElement(name = "error")
	@XmlElementWrapper(name = "errors")
	private ArrayList<RegistryError> errors = new ArrayList<RegistryError>();

	public RestResponse()
	{
	}

	public RestResponse(PersistentObject entity)
	{
		if(entity != null){		
			entities.add(entity);
		}
	}

	public RestResponse(List<? extends PersistentObject> list)
	{
		entities.addAll(list);
		resultCount=Long.valueOf(entities.size());
	}

	
	public RestResponse(RegistryError error)
	{
		errors.add(error);
	}

	public void addEntity(PersistentObject entity)
	{
		entities.add(entity);
	}

	public void addAllEntities(List<? extends PersistentObject> list)
	{
		entities.addAll(list);
		resultCount=Long.valueOf(entities.size());
	}

	public ArrayList<PersistentObject> getEntities()
	{
		return entities;
	}

	public long getResultCount()
	{
		if (resultCount == null)
		{
			return entities.size();
		}
		else
		{
			return resultCount;
		}
	}

	public void setResultCount(long resultCount)
	{
		this.resultCount = resultCount;
	}

	public void addError(RegistryError error)
	{
		errors.add(error);
	}

	public ArrayList<RegistryError> getErrors()
	{
		return errors;
	}

	@XmlTransient
	public boolean isSuccessful()
	{
		return errors.isEmpty();
	}

	
	public void setErrorId(String errorId)
	{
		this.errorId=errorId;
	}
	
	public String getErrorId()
	{
		return this.errorId;
	}
	
	public String toJsonString()
	{
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"");
	    sb.append("entities");
	    sb.append("\"");        
	    sb.append(":[");
	    StringBuilder strEntites = new StringBuilder("");
	    for(PersistentObject p : entities){	    	
	        strEntites.append(p.responseJson()).append(",");
	    }
	    if(strEntites.length() > 0){
	    	sb.append(strEntites.substring(0, strEntites.length()-1));
	    }
	    sb.append("]");
	    sb.append(",");    
	    
        sb.append("\"");        
        sb.append("errorId");
        sb.append("\"");
        sb.append(":");
        sb.append("\"");    
	    if(errorId!=null){sb.append(errorId);}
	    sb.append("\"");
	    sb.append(",");
	    
        sb.append("\"");        
        sb.append("resultCount");
        sb.append("\"");
        sb.append(":");
        sb.append("\"");            
	    sb.append(resultCount == null ? 0 : resultCount);
	    sb.append("\"");
	    sb.append(",");
	    
        sb.append("\"");        
        sb.append("errors");
        sb.append("\"");
        sb.append(":[");        
        StringBuilder strErrors = new StringBuilder("");
        for(RegistryError s : errors){
            strErrors.append("\"");
            strErrors.append(s);
            strErrors.append("\"");
            strErrors.append(",");
        }
        if(strErrors.length() > 0){
            sb.append(strErrors.substring(0, strErrors.length()-1));
        }
        sb.append("]");
	    sb.append("}");	    
		return sb.toString(); 
	}
	public String toJsonString(boolean idOnly)
	{
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"");
	    sb.append("entities");
	    sb.append("\"");        
	    sb.append(":[");
	    StringBuilder strEntities = new StringBuilder("");
	    for(PersistentObject p : entities){
	    	strEntities.append("\"");
	        strEntities.append(idOnly==true? p.idJson() : p.responseJson());
	        strEntities.append("\"");
	        strEntities.append(",");
	    }
	    if(strEntities.length() > 0){
	    	sb.append(strEntities.substring(0, strEntities.length()-1));
	    }
	    sb.append("]");
	    sb.append(",");
	    
        sb.append("\"");        
        sb.append("errorId");
        sb.append("\"");
        sb.append(":");
        sb.append("\"");    
	    if(errorId!=null){sb.append(errorId);}
	    sb.append("\"");
	    sb.append(",");
	    
        sb.append("\"");        
        sb.append("resultCount");
        sb.append("\"");
        sb.append(":");
        sb.append("\"");            
	    sb.append(resultCount == null ? 0 : resultCount);
	    sb.append("\"");
	    sb.append(",");
	    
        sb.append("\"");        
        sb.append("errors");
        sb.append("\"");
        sb.append(":[");        
        StringBuilder strErrors = new StringBuilder("");
        for(RegistryError s : errors){
            strErrors.append("\"");
            strErrors.append(s);
            strErrors.append("\"");
            strErrors.append(",");
        }
        if(strErrors.length() > 0){
            sb.append(strErrors.substring(0, strErrors.length()-1));
        }
        sb.append("]");
	    sb.append("}");	    
		return sb.toString(); 
	}	
}

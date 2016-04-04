package com.saltside.common.exception;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Priority;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author raj
 * Class representing the error information of errors that may
 * occur during HTTP REST requests to this Registry API
 *
 */
@JsonIgnoreProperties({"exception","logLevel"})
public class RegistryError {

    private RegistryException exception;
    private String message = "";
    private String description = "";
    private int status = HttpStatus.SC_ACCEPTED;
    private String type = "";
    private int logLevel = Priority.ERROR_INT;
    private String errorId = null ;

    public RegistryError() {
    }

    public RegistryError(RegistryException exception) {
        this.exception = exception;    
        this.message = exception.getMessage();
        this.description = exception.getDescription();
        this.status = exception.getStatusCode();
        this.type = exception.getClass().getSimpleName();
        this.logLevel = exception.getLogLevel();
    }

    public void setErrorId(String s) {
        this.errorId = s ;
    }

    public String getErrorId(String s) {
        return this.errorId ;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlTransient 
    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @XmlTransient 
    public RegistryException getException() {
        return exception;
    }

    public void setException(RegistryException exception) {
        this.exception = exception;
    }

}

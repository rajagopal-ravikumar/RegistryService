package com.saltside.common.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.mongodb.ReadPreference;
import com.saltside.common.entity.RegistryConstants;
import com.saltside.common.logging.LogWrapper;


/**
 * @author raj . Primary Configuration. Config values are parsed from the config
 *         file to be used by the application.
 * 
 */
public class Config {

	private static Config instance = null;
	protected static LogWrapper slogger = LogWrapper.getInstance("Config", Config.class);	
	protected Document configDocument = null;
	private String appName;
	
	/**
	 * Values are parsed once and loaded into this map so as to not parse again.
	 */
	private static final Map<String, Object> configLookupMap = new HashMap<String, Object>();
	/*
	 * Singleton
	 */
	public static Config getInstance() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}	
	
	//hostname where mongodb is running
	private final static String DATABASE_HOST = "/configuration/databaseConnection/host";
	private final static String DATABASE_HOST_DEFAULT = "localhost";
	public String getDatabaseHost() {
		return getConfigParameter(DATABASE_HOST,
				DATABASE_HOST_DEFAULT);
	}
	
	//port number where mongodb is listening for incoming connections from the app/mongo shell
	private final static String DATABASE_PORT = "/configuration/databaseConnection/port";
	private final static Integer DATABASE_PORT_DEFAULT = 27107;
	public Integer getDatabasePort() {
		return getConfigParameter(DATABASE_PORT,
				DATABASE_PORT_DEFAULT);
	}
	
	//database username
	private final static String DATABASE_USERNAME = "/configuration/databaseConnection/username";
	private final static String DATABASE_USERNAME_DEFAULT = "admin";
	public String getDatabaseUser() {
		return getConfigParameter(DATABASE_USERNAME,
				DATABASE_USERNAME_DEFAULT);
	}
	
	//database password
	private final static String DATABASE_PASSWORD = "/configuration/databaseConnection/password";
	private final static String DATABASE_PASSWORD_DEFAULT = "admin";
	public String getDatabasePassword() {
		return getConfigParameter(DATABASE_PASSWORD,
				DATABASE_PASSWORD_DEFAULT);
	}
	
	//Name of the database to connect to
	private final static String DATABASE_NAME = "/configuration/databaseConnection/name";
	private final static String DATABASE_NAME_DEFAULT = "Bird";
	public String getDatabaseName() {
		return getConfigParameter(DATABASE_NAME,
				DATABASE_NAME_DEFAULT);
	}
	
	  
    public final static String READ_PREF="/configuration/mongoConnectionOptions/readPreference";
    public final static String READ_PREF_DEFAULT="PRIMARY";
    
    //Read Preference to be at mongo driver level during initialization.
    public ReadPreference getReadPreference()
    {
        ReadPreference readPreference = ReadPreference.primary();
        String rp = getConfigParameter(READ_PREF, READ_PREF_DEFAULT);    
        if (RegistryConstants.SECONDARY_READ_PREFERENCE.equalsIgnoreCase(rp))
        {
            readPreference = ReadPreference.secondary();
        }
        else if(RegistryConstants.SECONDARY_PREFERRED_READ_PREFERENCE.equalsIgnoreCase(rp)){
        	readPreference=ReadPreference.secondaryPreferred();
        }
        else if(RegistryConstants.PRIMARY_PREFERRED_READ_PREFERENCE.equalsIgnoreCase(rp)){
        	readPreference=ReadPreference.primaryPreferred();
        }
        else if(RegistryConstants.NEAREST_READ_PREFERENCE.equalsIgnoreCase(rp)){
        	readPreference=ReadPreference.nearest();
        }
        return readPreference;
    }
    
    //log level configuration.
    private final static String LOG_LEVEL = "/configuration/logLevel";
	private final static String LOG_LEVEL_DEFAULT= "Warn";
	public String getLogLevel() {
		return getConfigParameter(LOG_LEVEL,
				LOG_LEVEL_DEFAULT);
	}
	
    
    //Following are mongo connection configuration parameters used in a distributed sharded mongo setup.
    
    public final static String CONNECTIONS_PER_HOST = "/configuration/mongoConnectionOptions/connectionsPerHost";
    public final static Integer CONNECTIONS_PER_HOST_DEFAULT = 50;
    
    public Integer getConnectionsPerHost()
    {
        return getConfigParameter(CONNECTIONS_PER_HOST, CONNECTIONS_PER_HOST_DEFAULT);
    }
    
    
    public final static String SOCKET_KEEP_ALIVE = "/configuration/mongoConnectionOptions/socketKeepAlive";
    public final static Boolean SOCKET_KEEP_ALIVE_DEFAULT = true;
    
    public Boolean getSocketKeepAlive()
    {
        return getConfigParameter(SOCKET_KEEP_ALIVE, SOCKET_KEEP_ALIVE_DEFAULT);
    }
    
    public final static String MAX_WAIT_TIME = "/configuration/mongoConnectionOptions/maxWaitTime";
    public final static Integer MAX_WAIT_TIME_DEFAULT = 300000;
    
    public Integer getMaxWaitTime()
    {
        return getConfigParameter(MAX_WAIT_TIME, MAX_WAIT_TIME_DEFAULT);
    }
    
    public final static String MONGO_CONNECT_TIMEOUT = "/configuration/mongoConnectionOptions/connectTimeout";
    public final static Integer MONGO_CONNECT_TIMEOUT_DEFAULT = 300000;
    
    public Integer getMongoConnectTimeOut()
    {
        return getConfigParameter(MONGO_CONNECT_TIMEOUT, MONGO_CONNECT_TIMEOUT_DEFAULT);
    }
    
    public final static String MONGO_SOCKET_TIMEOUT = "/configuration/mongoConnectionOptions/socketTimeout";
    public final static Integer MONGODB_SOCKET_TIMEOUT_DEFAULT = 300000;
    
    public Integer getMongoSocketTimeout()
    {
        return getConfigParameter(MONGO_SOCKET_TIMEOUT, MONGODB_SOCKET_TIMEOUT_DEFAULT);
    }
    
    public final static String MAXCONNECTION_IDLE_TIME = "/configuration/mongoConnectionOptions/maxConnectionIdleTime";
    public final static Integer MAXCONNECTION_IDLE_DEFAULT = 300000;
    
    public Integer getMaxConnectionIdleTime()
    {
        return getConfigParameter(MAXCONNECTION_IDLE_TIME, MAXCONNECTION_IDLE_DEFAULT);
    }
    
    
    public final static String THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER = "/configuration/mongoConnectionOptions/threadsAllowedToBlockForConnectionMultiplier";
    public final static Integer THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER_DEFAULT = 1;
    
    public Integer getThreadsAllowedToBlockForConnectionMultiplier()
    {
        return getConfigParameter(THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER, THREADS_ALLOWED_TO_BLOCK_FOR_CONNECTION_MULTIPLIER_DEFAULT);
    }
    
	public DatabaseInfo getDatabaseGroupMap() {
		DatabaseInfo di=new DatabaseInfo();
		di.setDatabaseName(getDatabaseName());
		di.setHost(getDatabaseHost());
		di.setPassword(getDatabasePassword());
		di.setPort(getDatabasePort());
		di.setUsername(getDatabaseUser());
		return di;
	}


	/**
	 * @author raj
	 * Bean representing the database connection information
	 */
	public static class DatabaseInfo {
		String databaseName;
		String host;
		int port;
		String username;
		String password;

		public DatabaseInfo(String databaseName, String host, int port,
				String username, String password) {
			this.databaseName = databaseName;
			this.host = host;
			this.port = port;
			this.username = username;
			this.password = password;
		}
		public DatabaseInfo() {
		}

		public String getDatabaseName() {
			return databaseName;
		}

		public void setDatabaseName(String databaseName) {
			this.databaseName = databaseName;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String toString() {
			return "databaseName=" + databaseName;
		}
	}

	/**
	 * Set the XML configuration file. This call will attempt to read the config
	 * file into memory so that its contents are accessible.
	 */
	public void setConfigFile(String configFilePath) {
		slogger.log("Loading configuration file",
				LogWrapper.pair("configFilePath", configFilePath));
		InputStream fStream = null;
		try {
			fStream = new FileInputStream(configFilePath);
			setConfigDocument(fStream);
			
		} catch (FileNotFoundException e) {
			slogger.error("Configuration file could not be found", e,
					LogWrapper.pair("configFilePath", configFilePath));
		} catch (Exception e) {
			slogger.error("Failed to load configuration file", e,
					LogWrapper.pair("configFilePath", configFilePath));
		}finally{
		    if(fStream != null){
		        try {
		            fStream.close();
		        }catch(Exception e){
		        }
		    }
		}	
	}


	/**
	 * Read the specified input stream as an XML configuration document.
	 */
	public void setConfigDocument(InputStream fStream) {
		DocumentBuilderFactory configParser = DocumentBuilderFactory
				.newInstance();
		try {
			DocumentBuilder dbConfigParser = configParser.newDocumentBuilder();
			this.configDocument = dbConfigParser.parse(fStream);
			configLookupMap.clear();
		} catch (Exception pce) {
			slogger.error("Failed to parse configuration input stream as XML",
					pce, LogWrapper.pair("", ""));
		}
	}

	/**
	 * Return the specified config property as a string, and return the default
	 * if any error occurs.
	 */
	public String getConfigParameter(String path, String defaultValue) {
		try {
			String cResults = (String) configLookupMap.get(path);
			if (cResults != null) {
				return cResults;
			}
			String result = defaultValue;
			Node node = getNode(path);
			if (node != null) {
				result = node.getTextContent();
			}
			if(result != null){ 
				result = result.trim();
			}
			configLookupMap.put(path, result);
			return result;
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Return the specified config property as an boolean, and return the
	 * default if any error occurs.
	 */
	public Boolean getConfigParameter(String path, Boolean defaultValue) {
		try {
			Boolean cResults = (Boolean) configLookupMap.get(path);
			if (cResults != null) {
				return cResults;
			}
			Boolean result = defaultValue;
			Node node = getNode(path);
			if (node != null) {
				result = Boolean.valueOf(node.getTextContent());
			}
			configLookupMap.put(path, result);
			return result;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Return the specified config property as an integer, and return the
	 * default if any error occurs.
	 */
	public Integer getConfigParameter(String path, Integer defaultValue) {
		try {
			Integer cResults = (Integer) configLookupMap.get(path);
			if (cResults != null) {
				return cResults;
			}
			Integer result = defaultValue;
			Node node = getNode(path);
			if (node != null) {
				result = Integer.parseInt(node.getTextContent());
			}
			configLookupMap.put(path, result);
			return result;
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Return the specified config property as an long, and return the default
	 * if any error occurs.
	 */
	public Long getConfigParameter(String path, Long defaultValue) {
		try {
			Long cResults = (Long) configLookupMap.get(path);
			if (cResults != null) {
				return cResults;
			}
			Long result = defaultValue;
			Node node = getNode(path);
			if (node != null) {
				result = Long.parseLong(node.getTextContent());
			}
			configLookupMap.put(path, result);
			return result;
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	
	/**
	 * Return the specified node. This allows us to tell if it really exists in
	 * the DOM. If you directly ask for the value via XPath it will return "" if
	 * it doesn't exist, rather than null.
	 */
	private Node getNode(String path) {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr;
			expr = xpath.compile(path);
			return (Node) expr.evaluate(this.configDocument,
					XPathConstants.NODE);
		} catch (Exception e) {
			return null;
		}
	}

}

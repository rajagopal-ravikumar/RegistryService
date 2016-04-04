package com.saltside.common.persistence;

import java.util.Arrays;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.saltside.common.config.Config;
import com.saltside.common.entity.Bird;
import com.saltside.common.entity.RegistryConstants;
import com.saltside.common.logging.LogWrapper;

/**
 * Wrapper for a Mongo DB and a Morphia datastore. The datastore maps to a
 * particular database instance within Mongo. database must be initialized
 * before using it.
 */
public class Database {
	private static final LogWrapper slogger = LogWrapper.getInstance("Database",
			Database.class);
	protected DB db = null;
	protected static MongoClient mongoClient=null;
	protected Datastore datastore = null;
	protected Morphia morphia = null;

	public Database() {
	}

	// Authenticating only against admin DB instead of authenticating against
	// each DB
	public static void initAdmin(String tenant, String host, int port,
			String username, String password) {
		try {
			slogger.debug("Authenticating against admin db");
			char[] pw = null;
			if (password != null) {
				pw = password.toCharArray();
				if (pw.length == 0) {
					pw = null;
				}
			}

			MongoClientOptions options = MongoClientOptions.builder()
					.connectionsPerHost(Config.getInstance().getConnectionsPerHost())			
					.socketKeepAlive(Config.getInstance().getSocketKeepAlive())
					.maxWaitTime(Config.getInstance().getMaxWaitTime())
					.connectTimeout(Config.getInstance().getMongoConnectTimeOut())
					.socketTimeout(Config.getInstance().getMongoSocketTimeout())
					.maxConnectionIdleTime(Config.getInstance().getMaxConnectionIdleTime())					
					.threadsAllowedToBlockForConnectionMultiplier(Config.getInstance().getThreadsAllowedToBlockForConnectionMultiplier())
					.readPreference(Config.getInstance().getReadPreference()).build();
			ServerAddress address = new ServerAddress(host, port);						
			MongoCredential credential = MongoCredential.createMongoCRCredential(username,  RegistryConstants.ADMIN_DB, pw);
			MongoClient mongo = new MongoClient(address, Arrays.asList(credential), options);
			mongo.getDatabase(RegistryConstants.ADMIN_DB);
			Morphia morphia = new Morphia();			
		    morphia.createDatastore(mongo, RegistryConstants.ADMIN_DB);			
			mongoClient=mongo;
		} catch (Exception e) {
			slogger.error("Admin database init failed", e);
		}
	}

	public Database( String name, String host, int port,
			String username, String password) {
		init(name, username, password);
	}

	public void createMorphiaMap(Class<? extends PersistentObject> cls) {
		if (morphia != null) {
			morphia.map(cls);
		}
	}

	public DBObject toDBObject(Object obj) {		
		return morphia.toDBObject(obj);
	}

	public <T extends PersistentObject> T fromDBObject(Class<T> cls,
			DBObject dbo) {
		return morphia.fromDBObject(cls, dbo);
	}

	public DBCollection getCollection(String collectionName) {
		return db.getCollection(collectionName);
	}

	/**
	 * Establish on connection with Mongo based on the specified credentials.
	 */
	private void init(String name, String userName, String password) {
		try {
			MongoClient mongo = this.mongoClient;
			db = mongo.getDB(name);
			morphia = new Morphia();
			datastore = morphia.createDatastore(mongo, name);
			initializeEntities();		
		} catch (Exception e) {
			slogger.error("database init failed ", e);
		}
	}

	/*
	 * Load the map for bird db
	 */
	private void initializeEntities() {
		createMorphiaMap(Bird.class);
	
	}	
	
	public void shutDown() {
		mongoClient.close();
	}

}

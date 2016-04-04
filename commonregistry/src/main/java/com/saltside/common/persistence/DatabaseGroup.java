package com.saltside.common.persistence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.saltside.common.config.Config;
import com.saltside.common.logging.LogWrapper;


/**
 * @author raj
 * Singleton classes holding reference to different databases that are initialized
 * 
 */
public class DatabaseGroup {
	private static DatabaseGroup instance = null;
	private static final LogWrapper slogger = LogWrapper.getInstance("Database-Group",
			DatabaseGroup.class);
	private static Map<String, Database> databaseGroups = new HashMap<String, Database>();
	
	
	public static void initializeDatabase(Config config){
		synchronized(DatabaseGroup.class){
			if (instance == null) {
				// Create a new Instance and init the Database Group.
				instance = new DatabaseGroup();
				String username=config.getDatabaseUser();
				String password=config.getDatabasePassword();
				int port=config.getDatabasePort();
				String name=config.getDatabaseName();
				String host=config.getDatabaseHost();
				Database.initAdmin(name, host, port, username, password);
				Database database = new Database(name, host, port, username, password);
				slogger.info("Initialized db", LogWrapper.pair("name", name));
				databaseGroups.put(name, database);
			}
		}
	}
	
	public static DatabaseGroup getInstance() {
		return instance;
	}

	protected DatabaseGroup() {
	}

	public void terminateDatabase() {
		slogger.info("terminate database");
		if (databaseGroups != null && !databaseGroups.isEmpty()) {
			for (Iterator<String> itr = databaseGroups.keySet().iterator(); itr
					.hasNext();) {
				Database db = databaseGroups.get(itr.next());
				if (db != null) {
					db.shutDown();
				}
			}
			databaseGroups.clear();
		}
	}

	public Database getDatabase(String key) {
		if (databaseGroups != null && databaseGroups.containsKey(key)) {
			return databaseGroups.get(key);
		}
		return null;

	}
	
	/** Only for testing. */
	public static void setDatabaseGroup(String name, Database database) {
		instance = new DatabaseGroup();
		databaseGroups.put(name, database);
	}

}

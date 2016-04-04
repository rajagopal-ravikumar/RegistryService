package com.saltside.common.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.RuntimeConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * @author raj 
 * using flapdoodle library to create on-the-fly dbs that unit tests
 *         can use instead of mocking db layer calls.
 * 
 * 
 */
@Ignore
public class TestDatabase extends Database
{
	private static Map<String, TestDatabase> instances = new HashMap<String, TestDatabase>();

	
	private MongodExecutable mongodExe = null;
	private MongodProcess mongod = null;
	private MongoClient mongo = null;

	public static void loadDatabase(String name)
	{
		if (!instances.containsKey(name))
		{
			TestDatabase instance = new TestDatabase();
			instance.loadTestDatabase(name);
			instances.put(name, instance);
		}
	}

	public static void unloadDatabase(String name)
	{
		if (instances.containsKey(name))
		{
			instances.get(name).shutdown(name);
			instances.remove(name);
		}
	}

	protected TestDatabase()
	{
	}

	protected void loadTestDatabase(String name)
	{
		try
		{
			Logger logger = Logger.getLogger(TestDatabase.class.getName());
			logger.setLevel(Level.SEVERE);
			final MongodStarter runtime = MongodStarter
					.getInstance(RuntimeConfig.getInstance(logger));
			mongodExe = runtime.prepare(new MongodConfig(Version.V2_2_0,
					Network.getFreeServerPort(), Network.localhostIsIPv6()));
			mongod = mongodExe.start();
			mongo = new MongoClient(new ServerAddress(Network.getLocalHost(), mongod
					.getConfig().getPort()));

			db = mongo.getDB(name);
			morphia = new Morphia();
			datastore = morphia.createDatastore(mongo, name);

			datastore.ensureIndexes();
			datastore.ensureCaps();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void shutdown(String dbName)
	{
		if (mongo != null)
		{
			mongod.stop();
			mongodExe.cleanup();
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException ie)
			{
			}
		}		
	}

}

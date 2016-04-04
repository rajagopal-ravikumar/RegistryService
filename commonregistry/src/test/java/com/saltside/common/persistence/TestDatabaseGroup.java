package com.saltside.common.persistence;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;

@Ignore
/**
 * This Object is used for running all of the unit tests. It is not the unit test for DatabaseGroup.
 * 
 * @author raj
 */
public class TestDatabaseGroup extends DatabaseGroup
{
	private static Map<String, TestDatabase> instances = new HashMap<String, TestDatabase>();

	public static void loadDatabase(String name)
	{
		if (!instances.containsKey(name))
		{
			TestDatabase instance = new TestDatabase();
			instance.loadTestDatabase(name);
			instances.put(name, instance);
			DatabaseGroup.setDatabaseGroup(name, instance);
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
}

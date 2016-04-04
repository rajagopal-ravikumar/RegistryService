package com.saltside.common.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.WriteConcern;
import com.saltside.common.persistence.TestDatabaseGroup;


public class ConfigTest {
    
    @Test
    public void xpathStringTest()
    {
        String xmlString="<Configuration><config type='db'><host>localhost</host><port>27017</port></config></Configuration>";
        InputStream testStream = new ByteArrayInputStream(xmlString.getBytes());
        TestConfig config = new TestConfig();
        config.setConfigDocument(testStream);
        assertEquals("localhost",config.getConfigParameter("//Configuration/config/host", "foo"));
        assertEquals("foo",config.getConfigParameter("//Configuration/config/hostX", "foo"));
    }    
  
 
    public static class TestConfig extends Config {
        
    }

}

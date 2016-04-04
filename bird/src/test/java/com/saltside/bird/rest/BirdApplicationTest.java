package com.saltside.bird.rest;

import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.Test;

import com.saltside.bird.rest.BirdApplication;


public class BirdApplicationTest {

    @Test
    public void testBirdApplication() throws Exception {
        BirdApplication birdApp = new BirdApplication();
        Set<Class<?>> classes = birdApp.getClasses();
        assertNotNull(classes);
        Set<Object> singletons = birdApp.getSingletons();
        assertNotNull(singletons);        
    }

}

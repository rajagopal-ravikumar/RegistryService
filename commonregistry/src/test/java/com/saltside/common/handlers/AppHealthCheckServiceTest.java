package com.saltside.common.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class AppHealthCheckServiceTest {
	@Test
	public void testPing() throws Exception{
		AppHealthCheckService ac=new AppHealthCheckService();
		Response r=ac.ping();
		assertNotNull("Wrong Response",r);
		assertEquals("Ping Response String is wrong", "OK", r.getEntity());
	}

}

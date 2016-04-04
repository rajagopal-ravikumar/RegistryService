package com.saltside.common.rest;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class TestServletContext  implements ServletContext {

    public TestServletContext() {}
    
    public Map<String,Object> attributes = new HashMap<String,Object>();
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getAttributeNames() {
        return null;
    }

    public ServletContext getContext(String arg0) {
        return null;
    }

    public String getContextPath() {
        return null;
    }

    public Map<String,String> initParams = new HashMap<String,String>();
    public String getInitParameter(String key) {
        return initParams.get(key);
    }

    @SuppressWarnings("rawtypes")
    public Enumeration getInitParameterNames() {
        return null;
    }

    public int getMajorVersion() {
        return 0;
    }

    public String mimeType = "text/plain";
    public String getMimeType(String arg0) {
        return mimeType;
    }

    public int getMinorVersion() {
        return 0;
    }

    public RequestDispatcher getNamedDispatcher(String arg0) {
        return null;
    }

    public String getRealPath(String arg0) {
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String arg0) {
        return null;
    }

    @Override
    public URL getResource(String arg0) throws MalformedURLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getResourceAsStream(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Set getResourcePaths(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getServerInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Servlet getServlet(String arg0) throws ServletException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getServletContextName() {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getServletNames() {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getServlets() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void log(String arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void log(Exception arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void log(String arg0, Throwable arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeAttribute(String arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setAttribute(String arg0, Object arg1) {
        // TODO Auto-generated method stub
        
    }
    
}

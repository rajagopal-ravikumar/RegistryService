package com.saltside.common.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.saltside.common.logging.LogWrapper;

/**
 * @author raj 
 * 		   Root filter for all incoming requests. For authentication
 *         requirements, this generic filter can be extended to form a auth
 *         filter to authenticate incoming requests using oauth or any other
 *         authentication mechanism.
 * 
 */
public class GenericFilter implements Filter {
    
    protected static LogWrapper slogger = LogWrapper.getInstance("GenericFilter", GenericFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
    
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;      
        chain.doFilter(request, response); 
    }  
    
}

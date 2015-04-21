/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import com.google.gson.Gson;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

/**
 *
 * @author Jan
 */
@Path("/")
public abstract class AbstractFacade {
    @Context
    protected ServletContext ctx;
    @Context
    protected HttpServletRequest request;
    @Context
    protected HttpServletResponse response;
    
    protected String responseText;
    protected Gson gson;
    
    public AbstractFacade(){
        gson = new Gson();
    }
    
}

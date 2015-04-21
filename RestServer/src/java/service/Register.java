/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import beans.User;
import dataprovider.DataStorage;
import exceptions.UserAlreadyExistsException;
import exceptions.UsernameDoesntExistException;
import factories.UserFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 * @author Jan
 */
@Path("/register")
public class Register extends AbstractFacade{

    private UserFactory factory;
    
    public Register(){
        factory = new UserFactory(new DataStorage());
    }  
    
    @POST
    public String login(
            @FormParam("username") String username,
            @FormParam("password") String password
    ) {
        User gebruiker;
        try {
            gebruiker = factory.register(request, username, password);
            responseText = gson.toJson(gebruiker);
        } catch (UserAlreadyExistsException | UsernameDoesntExistException ex) {
            responseText = ex.getMessage();
        } 
        return responseText;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import beans.User;
import dataprovider.DataStorage;
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
@Path("/login")
public class Login extends AbstractFacade {

    private UserFactory factory;
    
    public Login(){
        factory = new UserFactory(new DataStorage());
    }

    @POST
    public String login(
            @FormParam("username") String username,
            @FormParam("password") String password
    ) {
        User user = null;
        try {
            user = factory.login(request, username, password);
            responseText = gson.toJson(user);
        } catch (UsernameDoesntExistException ex) {
            responseText = ex.getMessage();
        } 
        return responseText;
    }
}

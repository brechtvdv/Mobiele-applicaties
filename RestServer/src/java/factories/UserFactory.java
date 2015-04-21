/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package factories;

import dataprovider.DataStorage;
import beans.User;
import exceptions.UserAlreadyExistsException;
import exceptions.UsernameDoesntExistException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Jan
 */
public class UserFactory {

    private DataStorage ds;

    public UserFactory(DataStorage ds) {
        this.ds = ds;
    }

    public User login(HttpServletRequest request, String username, String password) throws UsernameDoesntExistException {
        User user = ds.login(username, password);
        request.getSession(true).setAttribute("user", user);
        return user;
    }
    
    public User register(HttpServletRequest request, String username, String password) throws UserAlreadyExistsException, UsernameDoesntExistException{
        ds.registerUser(username, password);
        User user = ds.login(username, password);
        request.getSession(true).setAttribute("user", user);
        return user;
    }
}

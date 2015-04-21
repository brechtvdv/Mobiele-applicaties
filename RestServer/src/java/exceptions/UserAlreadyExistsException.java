/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author Jan
 */
public class UserAlreadyExistsException extends Exception{

    public UserAlreadyExistsException() {
        super("Username already exists in database");
    }
    
}

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
public class UsernameDoesntExistException extends Exception{
    
    public UsernameDoesntExistException(){
        super("Username does not exist in database");
    }
    
}

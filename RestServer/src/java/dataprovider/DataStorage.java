package dataprovider;

import beans.User;
import exceptions.UserAlreadyExistsException;
import exceptions.UsernameDoesntExistException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jan
 */
public class DataStorage {

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        ConstantProvider provider = ConstantProvider.getInstance();
        String connectionstring = ConstantProvider.getInstance().getProperty("connection_string");
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(
                ConstantProvider.getInstance().getProperty("connection_string"),
                ConstantProvider.getInstance().getProperty("db_username"),
                ConstantProvider.getInstance().getProperty("db_password"));
    }

    public User login(String username, String password) throws UsernameDoesntExistException {
        User user = null;
        try {
            Connection connection = getConnection();
            try {
                user = getUser(connection, username);
                if ( user == null || !user.getPassword().equals(password) ){
                    throw new UsernameDoesntExistException();
                }
                user.setPassword("-");
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public void registerUser(String username, String password) throws UserAlreadyExistsException {
        try {
            Connection connection = getConnection();
            try {
                User user = getUser(connection, username);
                if (user != null) {
                    // Als de user al bestaat kan je hem niet registreren:
                    throw new UserAlreadyExistsException();
                } else {
                    // Registreer de user in databank:
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            ConstantProvider.getInstance().getProperty("addUser"));
                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);
                    try {
                        preparedStatement.executeUpdate();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        preparedStatement.close();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private User fetchUserBean(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        return user;
    }

    private User getUser(Connection connection, String username) throws SQLException {
        User user = null;
        PreparedStatement preparedStatement = connection.prepareStatement(
                ConstantProvider.getInstance().getProperty("getUserByUsername"));
        preparedStatement.setString(1, username);
        try {
            ResultSet rs = preparedStatement.executeQuery(); rs.next();
            user = fetchUserBean(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            preparedStatement.close();
        }
        return user;
    }
}

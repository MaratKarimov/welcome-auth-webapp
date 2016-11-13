package ru.maratk.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maratk.exceptions.UserAlreadyExistException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * DAO for auth users
 */
public class AuthUserDao {

    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserDao.class);

    // JNDI address of datasource
    private static final String DATASOURCE_JNDI_NAME = "jdbc/welcome_authusers";

    // insert user SQL query
    private static final String INSERT_USER_QUERY = "INSERT INTO users (user_name, user_pass) VALUES (?, ?);";

    // users table primary key name
    private static final String USERS_TABLE_PRIMARY_KEY_NAME = "PK_USERS";

    // add user
    public void addUser(final String username, final String password) throws NamingException, UserAlreadyExistException, SQLException {
        DataSource dataSource = getDataSource();
        try (Connection con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(INSERT_USER_QUERY)){
            ps.setString(1, username);
            ps.setString(2, password);
            ps.execute();
        } catch (SQLException e){
            LOGGER.error("Error on insert new user!", e);
            // if we find duplicate primary key on insert
            if(e instanceof SQLIntegrityConstraintViolationException){
                SQLIntegrityConstraintViolationException e1 = (SQLIntegrityConstraintViolationException) e;
                if(e.getMessage().indexOf(USERS_TABLE_PRIMARY_KEY_NAME) != -1)
                    throw new UserAlreadyExistException();
            } else{
                throw e;
            }
        }
    }

    // get datasource by JNDI address
    private DataSource getDataSource() throws NamingException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:comp/env");
        return (DataSource) envContext.lookup(DATASOURCE_JNDI_NAME);
    }
}
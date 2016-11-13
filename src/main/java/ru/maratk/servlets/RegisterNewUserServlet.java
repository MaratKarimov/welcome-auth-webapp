package ru.maratk.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maratk.exceptions.UserAlreadyExistException;
import ru.maratk.dao.AuthUserDao;
import ru.maratk.enums.SignupMessages;
import ru.maratk.tools.SignupFormChecker;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet for sign-up
 */
public class RegisterNewUserServlet extends HttpServlet {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterNewUserServlet.class);

    private final static String USERNAME_PARAMETER_NAME = "username";
    private final static String PASSWORD_PARAMETER_NAME = "password";
    private final static String RETRY_PASSWORD_PARAMETER_NAME = "retryPassword";

    private final static String SIGN_UP_PAGE_ADDRESS = "/sign-up";
    private final static String WELCOME_PAGE_ADDRESS = "/welcome";
    private final static String ERROR_PAGE_ADDRESS = "/error";

    // instance of form checker
    private final static SignupFormChecker signupFormChecker = new SignupFormChecker();

    // instance of DAO for auth users
    private final static AuthUserDao authUserDao = new AuthUserDao();

    // POST request
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        // get username from form amd trim it
        String username = req.getParameter(USERNAME_PARAMETER_NAME).trim();
        // validate username
        SignupMessages usernameValidationResult = signupFormChecker.checkUsername(username);
        // if username validation failed
        if(!usernameValidationResult.equals(SignupMessages.USER_NAME_IS_OK)){
            fail(req, resp, usernameValidationResult);
            return;
        }
        // get password from form
        String password = req.getParameter(PASSWORD_PARAMETER_NAME);
        // get password from form
        String retryPassword = req.getParameter(RETRY_PASSWORD_PARAMETER_NAME);
        // validate password
        SignupMessages passwordValidationResult = signupFormChecker.checkPassword(password, retryPassword);
        // if password validation failed
        if(!passwordValidationResult.equals(SignupMessages.PASSWORD_IS_OK)){
            fail(req, resp, passwordValidationResult);
            return;
        }
        // save user to database
        try {
            authUserDao.addUser(username, password);
        }
        // if we can't find JNDI resource
        catch (NamingException e) {
            LOGGER.error("Error on creation of user!", e);
            fail(req, resp);
            return;
        }
        // if user already exist
        catch (UserAlreadyExistException e) {
            LOGGER.error("User already exist!");
            fail(req, resp, SignupMessages.USER_ALREADY_EXISTS);
            return;
        }
        catch(SQLException e){
            LOGGER.error("Error on creation of user!", e);
            fail(req, resp);
            return;
        }
        // if all tests passed and successfully saved user to database
        try {
            req.login(username, password);
            success(req, resp);
            return;
        } catch (ServletException e) {
            LOGGER.error("Error on login user!", e);
            fail(req, resp);
            return;
        }
    }

    // redirect for success
    private void success(HttpServletRequest req, HttpServletResponse resp){
        // request dispatcher instance
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(WELCOME_PAGE_ADDRESS);
        // forward
        try {
            dispatcher.forward(req, resp);
        } catch (ServletException e) {
            LOGGER.error("Error on redirect to welcome page", e);
            fail(req, resp);
            return;
        } catch (IOException e) {
            LOGGER.error("Error on redirect to welcome page", e);
            fail(req, resp);
            return;
        }
    }

    // redirect for fail
    private void fail(HttpServletRequest req, HttpServletResponse resp, SignupMessages signupMessage){
        // request dispatcher instance
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(SIGN_UP_PAGE_ADDRESS);
        // set message
        req.setAttribute("message", signupMessage);
        // forward
        try {
            dispatcher.forward(req, resp);
        } catch (ServletException e) {
            LOGGER.error("Error on fail redirect to sign-up page", e);
            fail(req, resp);
            return;
        } catch (IOException e) {
            LOGGER.error("Error on fail redirect to sign-up page", e);
            fail(req, resp);
            return;
        }
    }

    // redirect for fail
    private void fail(HttpServletRequest req, HttpServletResponse resp) {
        // request dispatcher instance
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(ERROR_PAGE_ADDRESS);
        try {
            dispatcher.forward(req, resp);
        } catch (IOException e) {
            LOGGER.error("Error on fail redirect to sign-up page", e);
        } catch (ServletException e) {
            LOGGER.error("Error on fail redirect to sign-up page", e);
        }
    }
}
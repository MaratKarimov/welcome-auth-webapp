package ru.maratk.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maratk.enums.SigninMessages;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for error on login
 */
public class LoginErrorServlet extends HttpServlet {

    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginErrorServlet.class);
    // page to redirect
    private static final String nextPage = "/sign-in";
    // page for unexpected error
    private final static String ERROR_PAGE_ADDRESS = "/error";

    // POST request
    public void doPost(HttpServletRequest req, HttpServletResponse resp){
        try {
            // logout
            req.logout();
            // request dispatcher instance
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextPage);
            // set message
            req.setAttribute("message", SigninMessages.WRONG_CREDENTIALS);
            // forward
            dispatcher.forward(req, resp);
        } catch (ServletException e) {
            LOGGER.error("On logout: ", e);
            fail(req, resp);
            return;
        } catch (IOException e) {
            LOGGER.error("On logout: ", e);
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
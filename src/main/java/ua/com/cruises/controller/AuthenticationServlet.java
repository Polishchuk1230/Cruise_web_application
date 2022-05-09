package ua.com.cruises.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.User;
import ua.com.cruises.repository.UserDao;

import java.io.IOException;
import java.util.*;

@WebServlet(AppPath.PATH_TO_LOGIN)
public class AuthenticationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String login = req.getParameter("login");
        String pass = DigestUtils.md2Hex(req.getParameter("password"));

        Optional<User> optionalUser = UserDao.getInstance().find(login);
        if (optionalUser.isPresent() && optionalUser.get().getPassword().equals(pass)) {
            User user = optionalUser.get();
            user.setPassword(null);
            req.getSession().setAttribute("user", user);
            System.out.println("Successfully authenticated as " + login + ". [redirect to index page]");
        }

        resp.sendRedirect(req.getContextPath());
    }
}

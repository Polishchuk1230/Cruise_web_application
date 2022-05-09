package ua.com.cruises.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Role;
import ua.com.cruises.model.User;
import ua.com.cruises.repository.UserDao;
import ua.com.cruises.service.InputDataValidation;

import java.io.IOException;

@WebServlet(AppPath.PATH_TO_REGISTRATION)
public class RegistrationServlet extends HttpServlet {
    private static Logger logger = LogManager.getLogger(RegistrationServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getParameter("error"));
        req.getRequestDispatcher("jsp/registration-page.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathSuffix = AppPath.PATH_TO_INDEX_PAGE;

        //-----------------------------------------------------------------------------------------------data validation
        if (!InputDataValidation.validatePassword(req.getParameter("password"))) {
            pathSuffix = AppPath.PATH_TO_REGISTRATION + "?error=err3";
        } else if (!InputDataValidation.validateLogin(req.getParameter("username"))) {
            pathSuffix = AppPath.PATH_TO_REGISTRATION + "?error=err2";
        } else if (!InputDataValidation.validatePhoneNumber(req.getParameter("phone-number"))) {
            pathSuffix = AppPath.PATH_TO_REGISTRATION + "?error=err4";
        } else {
            User newUser =
                    new User(0,
                            req.getParameter("username"),
                            DigestUtils.md2Hex(req.getParameter("password")),
                            req.getParameter("phone-number"));

            //insert into DataBase
            boolean result = UserDao.getInstance().insert(newUser);
            UserDao.getInstance().addRoles(newUser, Role.USER);

            if (result) {
                pathSuffix += "?success=success1";
                logger.info(String.format("New User successfully added [id%d]%s", newUser.getId(), newUser.getUsername()));
            }

            newUser.setPassword(null);
            req.getSession().setAttribute("user", newUser);
        }

        resp.sendRedirect(req.getContextPath() + pathSuffix);
    }
}

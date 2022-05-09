package ua.com.cruises.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.com.cruises.controller.filters.CheckRolesActualityFilter;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Role;
import ua.com.cruises.model.User;
import ua.com.cruises.repository.OrderDao;
import ua.com.cruises.repository.UserDao;

import java.io.IOException;
import java.util.Set;

@WebServlet(AppPath.PATH_TO_USER_PROFILE)
public class UserProfileServlet extends HttpServlet {
    Logger logger = LogManager.getLogger(UserProfileServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user;
        if (req.getParameter("user") != null && UserDao.getInstance().isUsernameExists(req.getParameter("user"))) {
            user = UserDao.getInstance().find(req.getParameter("user")).get();
            req.setAttribute("profileOwner", user);
        } else {
            user = (User) req.getSession().getAttribute("user");
        }

        if (user != null && user.getId() != 0) {
            req.setAttribute("images", UserDao.getInstance().findUserImagesByObj(user));
            req.setAttribute("orders", OrderDao.getInstance().findByUser(user));
        }

        req.getRequestDispatcher("jsp/user-profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("user");
        User user;
        String pathSuffix;

        if (username != null
                && (user = UserDao.getInstance().find(username).orElse(null)) != null)
        {
            if (user.getRoles().contains(Role.ADMIN)) {
                UserDao.getInstance().removeRoles(user, Role.ADMIN);
                logger.warn(String.format("Role Admin was removed for the user [id%d]%s", user.getId(), user.getUsername()));
            } else {
                UserDao.getInstance().addRoles(user, Role.ADMIN);
                logger.warn(String.format("Role Admin was added for the user [id%d]%s", user.getId(), user.getUsername()));
            }
            //Here we put a current user's id into an application-level collection so that the user's data (roles) would
            //be updated during the user's next request
            ((Set<Integer>) req.getServletContext().getAttribute(CheckRolesActualityFilter.UPDATE_ROLES_FOR_USERS)).add(user.getId());
            pathSuffix = "?user=" + user.getUsername();
        } else {
            pathSuffix = "?error=err8";
        }

        resp.sendRedirect(req.getContextPath() + AppPath.PATH_TO_USER_PROFILE + pathSuffix);
    }
}

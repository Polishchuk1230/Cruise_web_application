package ua.com.cruises.controller.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.com.cruises.model.User;
import ua.com.cruises.repository.UserDao;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/*
* This filter is designed to update Users' Roles dynamically.
* If a current User's id is contained in special application-level collection, data of that User's account in session
* will be updated from DataBase.
* */
@WebFilter("/*")
public class CheckRolesActualityFilter implements Filter {
    public static final String UPDATE_ROLES_FOR_USERS = "updateRolesForUsers";
    private static Logger logger = LogManager.getLogger(CheckRolesActualityFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        ServletContext context = filterConfig.getServletContext();

        if (context.getAttribute(UPDATE_ROLES_FOR_USERS) == null) {
            context.setAttribute(UPDATE_ROLES_FOR_USERS, new HashSet<Integer>());
        }
        logger.info("CheckRolesActualityFilter was initialized. The list of Users whose Roles must be updated was created in ServletContext");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Set<Integer> usersIdList = (Set<Integer>) request.getServletContext().getAttribute(UPDATE_ROLES_FOR_USERS);
        User user = (User) ((HttpServletRequest) request).getSession().getAttribute("user");

        if (user != null && usersIdList.contains(user.getId())) {
            //remove a current user's id from the application-level list
            usersIdList.remove(user.getId());

            //update information in session
            user = UserDao.getInstance().find(user.getId()).get();
            user.setPassword(null);
            ((HttpServletRequest) request).getSession().setAttribute("user", user);

            logger.info(String.format("Session data of [id%d]%s was updated.", user.getId(), user.getUsername()));
        }

        chain.doFilter(request, response);
    }
}

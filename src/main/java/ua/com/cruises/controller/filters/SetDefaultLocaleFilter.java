package ua.com.cruises.controller.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static ua.com.cruises.controller.filters.SecurityFilter.checkPathToChangeLocale;
import static ua.com.cruises.controller.filters.SecurityFilter.checkPathToLogin;
import static ua.com.cruises.controller.uris.AppPath.PATH_TO_CHANGE_LOCALE;

import java.io.IOException;

/*
* This filter sets default locale of this application
* */
@WebFilter("/*")
public class SetDefaultLocaleFilter implements Filter {
    public static final String DEFAULT_LOCALE = "UA";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;

        if (req.getSession().getAttribute("currentLocale") == null
                && !checkPathToChangeLocale.or(checkPathToLogin).test(req))
        {
            ((HttpServletResponse)servletResponse).sendRedirect(
                    req.getContextPath() + PATH_TO_CHANGE_LOCALE + "?locale=" + DEFAULT_LOCALE);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }
}

package ua.com.cruises.controller.filters;

import static ua.com.cruises.controller.uris.AppPath.*;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.com.cruises.model.Role;
import ua.com.cruises.model.User;

import java.io.IOException;
import java.util.function.Predicate;

@WebFilter("/*")
public class SecurityFilter implements Filter {
    private static Logger logger = LogManager.getLogger(SecurityFilter.class);

    private static boolean uriMatches(String regexDest, HttpServletRequest request) {
        return request.getRequestURI().matches(request.getContextPath() + regexDest);
    }

    private static boolean uriEquals(String destination, HttpServletRequest request) {
        return request.getRequestURI().equalsIgnoreCase(request.getContextPath() + destination);
    }

    //----------------------------------------------------------------------------------------------rules for filtration
    //rules for checking authority
    static Predicate<HttpServletRequest> checkUserIsAuthenticated = req -> req.getSession().getAttribute("user") != null;
    static Predicate<HttpServletRequest> checkUserHasRoleUser = req ->
            ((User) req.getSession().getAttribute("user")).getRoles().contains(Role.USER);
    static Predicate<HttpServletRequest> checkUserHasRoleAdmin = req ->
            ((User) req.getSession().getAttribute("user")).getRoles().contains(Role.ADMIN);

    //rules for checking destinations
    static Predicate<HttpServletRequest> checkPathToIndexPage = req ->           uriEquals("/", req);
    static Predicate<HttpServletRequest> checkPathToRegistration = req ->        uriEquals(PATH_TO_REGISTRATION, req);
    static Predicate<HttpServletRequest> checkPathToLogin = req ->               uriEquals(PATH_TO_LOGIN, req);
    static Predicate<HttpServletRequest> checkPathToLogout = req ->              uriEquals(PATH_TO_LOGOUT, req);
    static Predicate<HttpServletRequest> checkPathToCatalogue = req ->           uriEquals(PATH_TO_CATALOGUE, req);
    static Predicate<HttpServletRequest> checkPathToChangeLocale = req ->        uriEquals(PATH_TO_CHANGE_LOCALE, req);
    static Predicate<HttpServletRequest> checkPathToOrderPage = req ->           uriEquals(PATH_TO_ORDER_PAGE, req);
    static Predicate<HttpServletRequest> checkPathToUserUploadURI = req ->       uriEquals(PATH_TO_USER_PHOTO_UPLOAD_PAGE, req);
    static Predicate<HttpServletRequest> checkPathToCrewAdminPage = req ->       uriEquals(PATH_TO_CREW_ADMINISTRATION_PAGE, req);
    static Predicate<HttpServletRequest> checkPathToCadreAdminPage = req ->      uriEquals(PATH_TO_CADRE_ADMINISTRATION_PAGE, req);
    static Predicate<HttpServletRequest> checkPathToUserProfileForAdmin = req -> uriEquals(PATH_TO_USER_PROFILE, req);
    static Predicate<HttpServletRequest> checkPathToUserProfile = req ->         uriEquals(PATH_TO_USER_PROFILE, req) && req.getParameter("user") == null;

    static Predicate<HttpServletRequest> checkPathToCruiseAdminPage = req -> uriMatches(PATH_TO_CRUISE_ADMINISTRATION_PAGE + "(|/id[0-9]+)$", req);
    static Predicate<HttpServletRequest> checkPathToBoatAdminPage = req ->   uriMatches(PATH_TO_BOAT_ADMINISTRATION_PAGE + "(|/id[0-9]+)$", req);
    static Predicate<HttpServletRequest> checkPathToPortAdminPage = req ->   uriMatches(PATH_TO_PORT_ADMINISTRATION_PAGE + "(|/id[0-9]+)$", req);
    static Predicate<HttpServletRequest> checkPathToOrderAdminPage = req ->  uriMatches(PATH_TO_ORDER_ADMINISTRATION_PAGE + "(|/id[0-9]+)$", req);

    //rules for getting access to files
    static Predicate<HttpServletRequest> checkSVGFiles = req -> req.getRequestURI().matches(".*[^/][.][sS][vV][gG]$");
    static Predicate<HttpServletRequest> checkPNGFilesForAdmins = req -> req.getRequestURI().matches(".*[^/][.][pP][nN][gG]$");
    static Predicate<HttpServletRequest> checkPNGFilesForUsers = req -> {
        if (req.getSession().getAttribute("user") == null) return false;
        String username = ((User) req.getSession().getAttribute("user")).getUsername();
        return req.getRequestURI().matches(req.getContextPath() + "/static/images/" + username + "/.*[.][pP][nN][gG]$");
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //-----------------------------------------------------------------------------------------------Scopes of rules
        //Scope of rules for not authenticated users
        Predicate<HttpServletRequest> beforeAuthenticationChecksScope =
                checkUserIsAuthenticated.negate()
                        .and(checkPathToLogin
                                .or(checkPathToRegistration));

        //Scope of rules by users' Roles
        Predicate<HttpServletRequest> afterAuthenticationChecksScope =
                checkUserIsAuthenticated
                        .and(checkPathToLogout
                                .or(checkUserHasRoleUser
                                        .and(checkPathToOrderPage
                                                .or(checkPathToUserProfile)
                                                .or(checkPathToUserUploadURI)
                                                .or(checkPNGFilesForUsers)))
                                .or(checkUserHasRoleAdmin
                                        .and(checkPathToCruiseAdminPage
                                                .or(checkPathToBoatAdminPage)
                                                .or(checkPathToCrewAdminPage)
                                                .or(checkPathToCadreAdminPage)
                                                .or(checkPathToPortAdminPage)
                                                .or(checkPathToOrderAdminPage)
                                                .or(checkPNGFilesForAdmins)
                                                .or(checkPathToUserProfileForAdmin))));

        //Scope of always available resources
        Predicate<HttpServletRequest> regardlessOfAuthenticationScope =
                checkPathToIndexPage
                        .or(checkPathToCatalogue)
                        .or(checkPathToChangeLocale)
                        .or(checkSVGFiles);

        //Scope of checks if a destination exists
        //(now used for choosing between 404 and 403 error code)
        Predicate<HttpServletRequest> pathExistingChecksScope =
                checkPathToIndexPage
                        .or(checkPathToRegistration)
                        .or(checkPathToLogin)
                        .or(checkPathToLogout)
                        .or(checkPathToCatalogue)
                        .or(checkPathToChangeLocale)
                        .or(checkPathToOrderPage)
                        .or(checkPathToUserProfile)
                        .or(checkPathToUserProfileForAdmin)
                        .or(checkPathToUserUploadURI)
                        .or(checkPathToCrewAdminPage)
                        .or(checkPathToCadreAdminPage)
                        .or(checkPathToCruiseAdminPage)
                        .or(checkPathToBoatAdminPage)
                        .or(checkPathToPortAdminPage)
                        .or(checkPathToOrderAdminPage);

        //----------------------------------------------------------------------------------------------!!!FILTRATION!!!
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (beforeAuthenticationChecksScope
                .or(afterAuthenticationChecksScope)
                .or(regardlessOfAuthenticationScope)
                .test(req))
        {
            logger.info(String.format("Accessed to %s", req.getRequestURI()));
            chain.doFilter(request, response);
        }
        //----------------------------------------------------------------------------------------Security access denied
        else if (pathExistingChecksScope.test(req)) {
            logger.warn(String.format("(403) Redirected from %s", req.getRequestURI()));
            resp.sendError(403);
        }
        //------------------------------------------------------------------------------------------------Page not found
        else {
            logger.info(String.format("(404) Redirected from %s", req.getRequestURI()));
            resp.sendError(404);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("Security filter is initialized");
    }

    @Override
    public void destroy() {
        logger.info("Security filter is destroyed");
    }
}

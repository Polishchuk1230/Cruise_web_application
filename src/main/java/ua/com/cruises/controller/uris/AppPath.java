package ua.com.cruises.controller.uris;

/*
* Class with all uri addresses
* */
public class AppPath {
    public static final String PATH_TO_INDEX_PAGE = ""; // <-- now the same as PATH_TO_CATALOGUE
    public static final String PATH_TO_CATALOGUE = "/catalogue";

    public static final String PATH_TO_LOGIN = "/auth";
    public static final String PATH_TO_LOGOUT = "/logout";
    public static final String PATH_TO_REGISTRATION = "/registration";
    public static final String PATH_TO_CHANGE_LOCALE = "/jsp/inner/changeLocale.jsp";

    public static final String PATH_TO_USER_PROFILE = "/profile";
    public static final String PATH_TO_USER_PHOTO_UPLOAD_PAGE = "/user/upload";
    public static final String PATH_TO_BOAT_ADMINISTRATION_PAGE = "/boat-adm";
    public static final String PATH_TO_CREW_ADMINISTRATION_PAGE = "/crew-adm";
    public static final String PATH_TO_CADRE_ADMINISTRATION_PAGE = "/cadre-adm";
    public static final String PATH_TO_PORT_ADMINISTRATION_PAGE = "/port-adm";
    public static final String PATH_TO_ORDER_PAGE = "/order";
    public static final String PATH_TO_ORDER_ADMINISTRATION_PAGE = "/order-adm";
    public static final String PATH_TO_CRUISE_ADMINISTRATION_PAGE = "/cruise-adm";
//    public static final String PATH_TO_ERROR_PAGE = "/error";
//    public static final String PATH_TO_ERROR_PAGE = "/jsp/error404.jsp";

    private AppPath() {}


}

package ua.com.cruises.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SupportMethods {
    /*
     * Sets an attribute into a session if it was provided within a request
     * and returns a value of that attribute from session
     * */
    public static Object setAndReturnAttributeFromSession(String attribute, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String parameter = request.getParameter(attribute);

        //If a parameter was provided within a request, it will be written in a sessionScope as an attribute
        if (parameter != null && !parameter.isBlank() &&
                (session.getAttribute(attribute) == null ||
                        !parameter.equals(session.getAttribute(attribute).toString()))) {
            session.setAttribute(attribute, parameter);
        }
        //Else if a blank parameter was provided, it will be removed from sessionScope
        else if (parameter != null && parameter.isBlank()) {
            session.removeAttribute(attribute);
        }

        //Returns actual data from sessionScope
        return session.getAttribute(attribute);
    }
}

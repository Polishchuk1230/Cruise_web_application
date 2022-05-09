package ua.com.cruises.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Boat;
import ua.com.cruises.repository.BoatDao;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {AppPath.PATH_TO_BOAT_ADMINISTRATION_PAGE, AppPath.PATH_TO_BOAT_ADMINISTRATION_PAGE + "/*"})
public class BoatAdministrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("boat-adm#doGet");

        req.setAttribute("boats", BoatDao.getInstance().findAll());
        req.getRequestDispatcher("jsp/boat-administration-page.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("boat-adm#doPost");

        Boat boat = new Boat(0, req.getParameter("boat-name"), Integer.parseInt(req.getParameter("boat-capacity")));
        if (BoatDao.getInstance().insert(boat)) {
            System.out.println("The boat was added successfully");
        }
        resp.sendRedirect(req.getContextPath() + "/boat-adm");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Pattern pattern = Pattern.compile(".*/id([0-9]+)$");
        Matcher matcher = pattern.matcher(req.getRequestURI());

        if ( req.getMethod().equalsIgnoreCase("DELETE") && matcher.find() &&
                BoatDao.getInstance().remove(Integer.parseInt(matcher.group(1))))
        {
            System.out.println("gol ==> boat has been removed");
        }
    }
}

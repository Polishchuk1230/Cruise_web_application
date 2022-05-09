package ua.com.cruises.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Cruise;
import ua.com.cruises.model.Port;
import ua.com.cruises.repository.BoatDao;
import ua.com.cruises.repository.CruiseDao;
import ua.com.cruises.repository.PortDao;
import ua.com.cruises.service.SortingService;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@WebServlet(urlPatterns = {AppPath.PATH_TO_CRUISE_ADMINISTRATION_PAGE, AppPath.PATH_TO_CRUISE_ADMINISTRATION_PAGE + "/*"})
public class CruiseAdministrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final long OBJECTS_PER_PAGE = 5;
        List<Cruise> cruiseList = CruiseDao.getInstance().findAll();
        Stream<Cruise> cruiseStream = cruiseList.stream();

        //Sorting of cruises if required
        cruiseStream = SortingService.sortCruises(cruiseStream, req);

        //Pagination of cruises
        long pageNum = req.getParameter("page") != null && Long.parseLong(req.getParameter("page")) > 0 ?
                Long.parseLong(req.getParameter("page"))-1 : 0;
        cruiseStream = cruiseStream
                .skip(pageNum*OBJECTS_PER_PAGE)
                .limit(OBJECTS_PER_PAGE);

        req.setAttribute("lastPageNum", (int) Math.ceil((double) cruiseList.size()/OBJECTS_PER_PAGE));
        req.setAttribute("cruises", cruiseStream.toList());
        req.setAttribute("ports", PortDao.getInstance().findAll());
        req.setAttribute("boats", BoatDao.getInstance().findAll());
        req.getRequestDispatcher("jsp/cruise-administration-page.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String error = "";

        Cruise cruise = new Cruise(0,
                Date.valueOf(req.getParameter("cruise-start-date")),
                Date.valueOf(req.getParameter("cruise-end-date")),
                Integer.parseInt(req.getParameter("cruise-cost")));

        cruise.setBoat(BoatDao.getInstance().find(Integer.parseInt(req.getParameter("boat-id"))).get());

        if (req.getParameterMap().get("port-id") == null) {
            error = "?error=err8";
        } else {
            List<Port> route = new ArrayList<>();
            for (String portParam : req.getParameterMap().get("port-id")) {
                route.add(PortDao.getInstance().find(Integer.parseInt(portParam)).get());
            }
            cruise.setPorts(route);
            CruiseDao.getInstance().insert(cruise);
        }

        resp.sendRedirect(req.getContextPath() + "/cruise-adm" + error);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Pattern pattern = Pattern.compile(".*/id([0-9]+)$");
        Matcher matcher = pattern.matcher(req.getRequestURI());

        if (req.getMethod().equalsIgnoreCase("DELETE") && matcher.find() &&
            CruiseDao.getInstance().remove(Integer.parseInt(matcher.group(1))))
        {
            System.out.println("gol ==> cruise has been removed");
        }
    }
}

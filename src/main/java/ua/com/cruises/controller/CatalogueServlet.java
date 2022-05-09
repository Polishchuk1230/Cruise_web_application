package ua.com.cruises.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.com.cruises.model.Crew;
import ua.com.cruises.model.Cruise;
import ua.com.cruises.repository.CrewDao;
import ua.com.cruises.repository.CruiseDao;
import ua.com.cruises.service.CruiseFilterService;
import ua.com.cruises.service.SortingService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static ua.com.cruises.controller.uris.AppPath.*;

@WebServlet(urlPatterns = {PATH_TO_CATALOGUE, PATH_TO_INDEX_PAGE})
public class CatalogueServlet extends HttpServlet {
    final static long OBJECTS_PER_PAGE = 6;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Stream<Cruise> cruiseStream = CruiseDao.getInstance().findAll().stream();

        //Filtering if required
        cruiseStream = CruiseFilterService.filterCruises(cruiseStream, req);


        //providing boat's crew if required
        if (req.getParameter("boatId") != null) {
            int boatIdParam = Integer.parseInt(req.getParameter("boatId"));
            Crew crew = CrewDao.getInstance().findByBoatId(boatIdParam).orElse(null);
            cruiseStream = cruiseStream.peek(cruise -> {
                if (cruise.getBoat().getId() == boatIdParam)
                    cruise.getBoat().setCrew(crew);
            });
        }

        //Sorting of cruises if required
        cruiseStream = SortingService.sortCruises(cruiseStream, req);

        //Setting amount of pages in request's attribute
        List<Cruise> tempCruiseList = cruiseStream.toList();
        req.setAttribute("lastPageNum", (int) Math.ceil((double) tempCruiseList.size()/OBJECTS_PER_PAGE));
        cruiseStream = tempCruiseList.stream();
        //Pagination
        long pageNum = req.getParameter("page") != null && Long.parseLong(req.getParameter("page")) > 0 ?
                Long.parseLong(req.getParameter("page"))-1 : 0;
        cruiseStream = cruiseStream
                .skip(pageNum * OBJECTS_PER_PAGE)
                .limit(OBJECTS_PER_PAGE);

        req.setAttribute("cruises", cruiseStream.toList());
        req.getRequestDispatcher("jsp/index.jsp").forward(req, resp);
    }
}
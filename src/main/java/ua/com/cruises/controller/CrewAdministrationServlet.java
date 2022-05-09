package ua.com.cruises.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Boat;
import ua.com.cruises.model.Cadre;
import ua.com.cruises.model.Crew;
import ua.com.cruises.repository.BoatDao;
import ua.com.cruises.repository.CadreDao;
import ua.com.cruises.repository.CrewDao;

import java.io.IOException;
import java.util.List;

@WebServlet(AppPath.PATH_TO_CREW_ADMINISTRATION_PAGE)
public class CrewAdministrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
        * If boat-id parameter provided, pulled out that boat from DB, otherwise all boats will be pulled out from DB
        * and provided in boats attribute (for choosing specified one at front-side)
        * */
        if (req.getParameter("boat-id") != null) {
            Boat boat = BoatDao.getInstance().find(Integer.parseInt(req.getParameter("boat-id"))).get();
            req.setAttribute("boat", boat);
        } else {
            req.setAttribute("boats", BoatDao.getInstance().findAll());
        }

        //Providing all free Cadres as freeCadres attribute
        List<Cadre> freeCadres = CadreDao.getInstance().findAllWithCrewId(0);
        req.setAttribute("freeCadres", freeCadres);
        req.getRequestDispatcher("jsp/crew-administration-page.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //receive an array of Cadres that should be assigned with a boat's crew (can be empty)
        String[] params = req.getParameterMap().get("cadre-id");
        Cadre[] cadres = params != null ? new Cadre[params.length] : new Cadre[0];
        for (int i = 0; i < cadres.length; i++) {
            cadres[i] = CadreDao.getInstance().find(Integer.parseInt(params[i])).get();
        }

        //pull out a Boat for assigning from BD (boat-id - parameter is absolutely required for this request)
        Boat boat = BoatDao.getInstance().find(Integer.parseInt(req.getParameter("boat-id"))).get();

        //remove a boat's crew
        if (cadres.length == 0 && boat.getCrew() != null) {
            CrewDao.getInstance().remove(boat.getCrew().getId());
        }
        //recollect existing a Boat's Crew
        else if (boat.getCrew() != null) {
            CadreDao.getInstance().assignCrewId(boat.getCrew().getId(), cadres);
        }
        //create a new Crew for a Boat
        else {
            Crew newCrew = new Crew(0);
            CrewDao.getInstance().insert(newCrew);
            boat.setCrew(newCrew);
            CrewDao.getInstance().assignBoatId(boat.getId(), newCrew);
            CadreDao.getInstance().assignCrewId(newCrew.getId(), cadres);
        }

        resp.sendRedirect(req.getContextPath()+AppPath.PATH_TO_CREW_ADMINISTRATION_PAGE);
    }
}

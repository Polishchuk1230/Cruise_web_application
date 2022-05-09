package ua.com.cruises.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Cadre;
import ua.com.cruises.repository.CadreDao;
import ua.com.cruises.service.InputDataValidation;

import java.io.IOException;

@WebServlet(AppPath.PATH_TO_CADRE_ADMINISTRATION_PAGE)
public class CadreAdministrationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String error = "";

        Cadre newCadre = new Cadre(0,
                req.getParameter("name"),
                req.getParameter("surname"),
                req.getParameter("position"),
                req.getParameter("characteristic"));

        if (InputDataValidation.validateCadre(newCadre)) {
            CadreDao.getInstance().insert(newCadre);
        } else {
            error = "?error=err1";
        }

        resp.sendRedirect(req.getContextPath() + AppPath.PATH_TO_CREW_ADMINISTRATION_PAGE + error);
    }
}

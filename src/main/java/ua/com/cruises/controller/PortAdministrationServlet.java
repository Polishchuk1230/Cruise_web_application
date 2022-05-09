package ua.com.cruises.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Port;
import ua.com.cruises.repository.PortDao;

import java.io.IOException;

@WebServlet(AppPath.PATH_TO_PORT_ADMINISTRATION_PAGE)
public class PortAdministrationServlet extends HttpServlet {
    private final PortDao portDao = PortDao.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("ports", portDao.findAll());
        req.getRequestDispatcher("jsp/port-administration-page.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        switch (req.getParameter("purpose")) {
            case "add-port" -> portDao.insert(new Port(0, req.getParameter("port-country"), req.getParameter("port-city")));
            case "edit-port" -> portDao.update(new Port(Integer.parseInt(req.getParameter("port-id")), req.getParameter("port-country"), req.getParameter("port-city")));
            case "delete-port" -> portDao.remove(Integer.parseInt(req.getParameter("port-id")));
        }

        resp.sendRedirect(req.getContextPath() + "/port-adm");
    }
}

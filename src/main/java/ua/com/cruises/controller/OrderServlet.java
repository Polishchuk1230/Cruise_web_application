package ua.com.cruises.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Order;
import ua.com.cruises.model.User;
import ua.com.cruises.repository.CruiseDao;
import ua.com.cruises.repository.OrderDao;
import ua.com.cruises.service.InputDataValidation;

import java.io.IOException;

@WebServlet(AppPath.PATH_TO_ORDER_PAGE)
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Order newOrder =
                new Order(0,
                        Integer.parseInt(req.getParameter("seats")),
                        false);

        //Setting filler empty objects of User and Cruise with real identifiers
        newOrder.setUser(new User(Integer.parseInt(req.getParameter("user-id")), null, null, null));
        newOrder.setCruise(CruiseDao.getInstance().find(Integer.parseInt(req.getParameter("cruise-id"))).get());

        if (InputDataValidation.validateOrder(newOrder)) {
            //insert into DataBase
            OrderDao.getInstance().insert(newOrder);
        }

        resp.sendRedirect(req.getContextPath());
    }
}

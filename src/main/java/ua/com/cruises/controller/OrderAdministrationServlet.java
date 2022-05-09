package ua.com.cruises.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Order;
import ua.com.cruises.repository.OrderDao;

import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {AppPath.PATH_TO_ORDER_ADMINISTRATION_PAGE, AppPath.PATH_TO_ORDER_ADMINISTRATION_PAGE + "/*"})
public class OrderAdministrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Collection<Order> resultList = null;

        if (req.getParameter("getall") != null && req.getParameter("getall").equalsIgnoreCase("true")) {
            resultList = OrderDao.getInstance().findAll();
        } else {
            resultList = OrderDao.getInstance().findAllNotConfirmed();
        }

        req.setAttribute("orders", resultList);
        req.getRequestDispatcher("jsp/order-administration-page.jsp").forward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        Pattern pattern = Pattern.compile(".*/id([0-9]+)$");
        Matcher matcher = pattern.matcher(req.getRequestURI());

        if (req.getMethod().equalsIgnoreCase("PUT") && matcher.find()) {
            Order order = OrderDao.getInstance().find(Integer.parseInt(matcher.group(1))).get();
            order.setConfirmed(!order.isConfirmed()); // <-- toggle this field
            OrderDao.getInstance().setConfirmedByOrderObj(order);
        }
    }
}

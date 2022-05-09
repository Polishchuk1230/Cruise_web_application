package ua.com.cruises.service;

import static ua.com.cruises.service.SupportMethods.setAndReturnAttributeFromSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ua.com.cruises.model.Cruise;

import java.sql.Date;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CruiseFilterService {
    private CruiseFilterService() {}

    public final static String CRUISE_START_DATE_PARAM_NAME = "cruiseStartDate";
    public final static String CRUISE_END_DATE_PARAM_NAME = "cruiseEndDate";
    public final static String CRUISE_START_DURATION_DIAPASON_PARAM_NAME = "startDaysDiapason";
    public final static String CRUISE_END_DURATION_DIAPASON_PARAM_NAME = "endDaysDiapason";

    public static Stream<Cruise> filterCruises(Stream<Cruise> stream, HttpServletRequest request) {
        HttpSession session = request.getSession();

        //Filtration by default
        Predicate<Cruise> startDatePredicate = cruise -> true;
        Predicate<Cruise> endDatePredicate = cruise -> true;
        Predicate<Cruise> startDaysDiapasonPredicate = cruise -> true;
        Predicate<Cruise> endDaysDiapasonPredicate = cruise -> true;

        Object filterPoint;

        filterPoint = setAndReturnAttributeFromSession(CRUISE_START_DATE_PARAM_NAME, request);
        if (filterPoint != null) {
            Date startDateFilter = Date.valueOf(filterPoint.toString());
            startDatePredicate = cruise -> cruise.getStartDate().compareTo(startDateFilter) >= 0;
            filterPoint = null;
        }

        filterPoint = setAndReturnAttributeFromSession(CRUISE_END_DATE_PARAM_NAME, request);
        if (filterPoint != null) {
            Date endDateFilter = Date.valueOf(filterPoint.toString());
            endDatePredicate = cruise -> cruise.getEndDate().compareTo(endDateFilter) <= 0;
            filterPoint = null;
        }

        filterPoint = setAndReturnAttributeFromSession(CRUISE_START_DURATION_DIAPASON_PARAM_NAME, request);
        if (filterPoint != null) {
            int startDiapason = Integer.parseInt(filterPoint.toString());
            startDaysDiapasonPredicate = cruise -> cruise.getDuration() >= startDiapason;
            filterPoint = null;
        }

        filterPoint = setAndReturnAttributeFromSession(CRUISE_END_DURATION_DIAPASON_PARAM_NAME, request);
        if (filterPoint != null) {
            int endDiapason = Integer.parseInt(filterPoint.toString());
            endDaysDiapasonPredicate = cruise -> cruise.getDuration() <= endDiapason;
            filterPoint = null;
        }

        return stream.filter(
                startDatePredicate
                        .and(endDatePredicate)
                        .and(startDaysDiapasonPredicate)
                        .and(endDaysDiapasonPredicate));
    }
}

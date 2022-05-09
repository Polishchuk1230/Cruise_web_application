package ua.com.cruises.service;

import static ua.com.cruises.service.SupportMethods.setAndReturnAttributeFromSession;

import jakarta.servlet.http.HttpServletRequest;
import ua.com.cruises.model.Cruise;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public class SortingService {
    private SortingService() {}

    public enum SortCriteria { BY_CRUISE_COST, BY_BOOKED_SEATS, BY_CRUISE_DURATION }
    public static final String SORTING_ATTRIBUTE_NAME = "sorted";

    public static Stream<Cruise> sortCruises(Stream<Cruise> stream, HttpServletRequest request) {
        if (request == null || request.getSession() == null) {
            throw new IllegalArgumentException();
        }

        Object sortingAttr = setAndReturnAttributeFromSession(SORTING_ATTRIBUTE_NAME, request);

        if (sortingAttr == null) {
            return stream;
        }

        if (Arrays.stream(SortCriteria.values())
                .map(Enum::name)
                .anyMatch(e -> e.equals(sortingAttr.toString()))) {
            return switch (SortCriteria.valueOf(sortingAttr.toString())) {
                case BY_CRUISE_COST -> stream.sorted(Comparator.comparingInt(Cruise::getCost));
                case BY_BOOKED_SEATS -> stream.sorted(Comparator.comparingInt(Cruise::getBookedSeats));
                case BY_CRUISE_DURATION -> stream.sorted(Comparator.comparingLong(Cruise::getDuration));
            };
        }
        return stream;
    }
}

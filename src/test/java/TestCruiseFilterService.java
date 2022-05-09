import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ua.com.cruises.model.Cruise;
import ua.com.cruises.service.CruiseFilterService;
import ua.com.cruises.service.SupportMethods;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TestCruiseFilterService {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    List<Cruise> inputData;

    @BeforeEach
    private void beforeEachPreparations() {
        inputData = new ArrayList<>(
                List.of(
                        new Cruise(0, Date.valueOf("2022-05-17"), Date.valueOf("2022-05-19"), 0),
                        new Cruise(0, Date.valueOf("2022-05-17"), Date.valueOf("2022-05-22"), 0),
                        new Cruise(0, Date.valueOf("2022-05-17"), Date.valueOf("2022-06-01"), 0)));
    }

    @Test
    public void testFilterCruisesEndDurationDiapason() {
        List<Cruise> outputData;

        try (MockedStatic<SupportMethods> supportMethods = Mockito.mockStatic(SupportMethods.class)) {
            supportMethods
                    .when(() -> SupportMethods.setAndReturnAttributeFromSession(CruiseFilterService.CRUISE_END_DURATION_DIAPASON_PARAM_NAME, request))
                    .thenReturn("5"); // <-- This part should filter out every Cruise object which duration more than 5 days;

            outputData = CruiseFilterService.filterCruises(inputData.stream(), request).toList();
        }

        inputData.remove(2); // <-- Here we've removed the Cruise with duration more than 5;
        Assertions.assertEquals(inputData, outputData);
    }

    @Test
    public void testFilterCruisesStartDurationDiapason() {
        List<Cruise> outputData;

        try (MockedStatic<SupportMethods> supportMethods = Mockito.mockStatic(SupportMethods.class)) {
            supportMethods
                    .when(() -> SupportMethods.setAndReturnAttributeFromSession(CruiseFilterService.CRUISE_START_DURATION_DIAPASON_PARAM_NAME, request))
                    .thenReturn("3"); // <-- This part should filter out every Cruise object which duration less than 3 days;

            outputData = CruiseFilterService.filterCruises(inputData.stream(), request).toList();
        }

        inputData.remove(0); // <-- Here we've removed the Cruise with duration less than 3;
        Assertions.assertEquals(inputData, outputData);
    }

    @Test
    public void testFilterCruisesEndDate() {
        List<Cruise> outputData;

        try (MockedStatic<SupportMethods> supportMethods = Mockito.mockStatic(SupportMethods.class)) {
            supportMethods
                    .when(() -> SupportMethods.setAndReturnAttributeFromSession(CruiseFilterService.CRUISE_END_DATE_PARAM_NAME, request))
                    .thenReturn("2022-05-21"); // <-- This part should filter out every Cruise object which end date is after 21.05.2022;

            outputData = CruiseFilterService.filterCruises(inputData.stream(), request).toList();
        }

        inputData.remove(2);
        inputData.remove(1);
        Assertions.assertEquals(inputData, outputData);
    }

    @Test
    public void testFilterCruisesStartDate() {
        List<Cruise> outputData;

        try (MockedStatic<SupportMethods> supportMethods = Mockito.mockStatic(SupportMethods.class)) {
            supportMethods
                    .when(() -> SupportMethods.setAndReturnAttributeFromSession(CruiseFilterService.CRUISE_START_DATE_PARAM_NAME, request))
                    .thenReturn("2022-05-18"); // <-- This part should filter out every Cruise object which start date is before 18.05.2022;

            outputData = CruiseFilterService.filterCruises(inputData.stream(), request).toList();
        }

        Assertions.assertEquals(new ArrayList<>(), outputData);
    }
}

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ua.com.cruises.model.Cruise;
import ua.com.cruises.model.CruiseStatus;

import java.sql.Date;
import java.util.Random;

public class TestCruiseStatus {
    private static Cruise generateCruise(int remainingDaysFrom, int remainingDaysTo) {
        int randomDays = remainingDaysFrom + new Random().nextInt(remainingDaysTo-remainingDaysFrom);

        return new Cruise(42,
                new Date(System.currentTimeMillis() + (long) randomDays * 1000*60*60*24 + 1000*60*3),
                new Date(System.currentTimeMillis() + (long) (randomDays + 8) * 1000*60*60*24),
                10_000);
    }

    @Test
    public void testCruiseStatusRegistrationOpen() {
        Cruise testCruise = generateCruise(3, 5);
        System.out.printf("Now ================> [%s]\n", new Date(System.currentTimeMillis()));
        System.out.printf("Test cruise dates ==> [%s - %s]\n", testCruise.getStartDate(), testCruise.getEndDate());
        System.out.printf("The Cruise's status ==> [%s]\n\n", testCruise.getCruiseStatus());

        Assertions.assertEquals(CruiseStatus.REGISTRATION_IN_PROGRESS, testCruise.getCruiseStatus());
    }

    @Test
    public void testCruiseStatusRegistrationClosed() {
        Cruise testCruise = generateCruise(1, 3);
        System.out.printf("Now ================> [%s]\n", new Date(System.currentTimeMillis()));
        System.out.printf("Test cruise dates ==> [%s - %s]\n", testCruise.getStartDate(), testCruise.getEndDate());
        System.out.printf("The Cruise's status ==> [%s]\n\n", testCruise.getCruiseStatus());

        Assertions.assertEquals(CruiseStatus.REGISTRATION_CLOSED, testCruise.getCruiseStatus());
    }

    @Test
    public void testCruiseStatusInProgress() {
        Cruise testCruise = generateCruise(-8, 1);
        System.out.printf("Now ================> [%s]\n", new Date(System.currentTimeMillis()));
        System.out.printf("Test cruise dates ==> [%s - %s]\n", testCruise.getStartDate(), testCruise.getEndDate());
        System.out.printf("The Cruise's status ==> [%s]\n\n", testCruise.getCruiseStatus());

        Assertions.assertEquals(CruiseStatus.IN_PROGRESS, testCruise.getCruiseStatus());
    }

    @Test
    public void testCruiseStatusCompleted() {
        Cruise testCruise = generateCruise(-10, -8);
        System.out.printf("Now ================> [%s]\n", new Date(System.currentTimeMillis()));
        System.out.printf("Test cruise dates ==> [%s - %s]\n", testCruise.getStartDate(), testCruise.getEndDate());
        System.out.printf("The Cruise's status ==> [%s]\n\n", testCruise.getCruiseStatus());

        Assertions.assertEquals(CruiseStatus.COMPLETED, testCruise.getCruiseStatus());
    }
}

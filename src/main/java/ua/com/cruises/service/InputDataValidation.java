package ua.com.cruises.service;

import ua.com.cruises.model.Cadre;
import ua.com.cruises.model.Order;
import ua.com.cruises.repository.CruiseDao;
import ua.com.cruises.repository.UserDao;

import java.util.function.Predicate;

public class InputDataValidation {
    private static final int PASSWORD_MIN_LENGTH = 4;
    private static final int PASSWORD_MAX_LENGTH = 10;
    private static final int LOGIN_MIN_LENGTH = 1;
    private static final int LOGIN_MAX_LENGTH = 15;

    private static Predicate<String> checkPasswordLength = password ->
            password.length() >= PASSWORD_MIN_LENGTH && password.length() <= PASSWORD_MAX_LENGTH;

    private static Predicate<String> checkSymbols = string ->
            string.matches("^[a-zA-Zа-яА-ЯїЇ0-9_-]*$");

    private static Predicate<String> checkSymbolsOfRealName = string ->
            string.matches("^[a-zA-Zа-яА-ЯїЇ -]*$");

    private static Predicate<String> checkLoginDuplicate = login ->
            !UserDao.getInstance().isUsernameExists(login);

    private static Predicate<String> checkLoginLength = login ->
            login.length() >= LOGIN_MIN_LENGTH && login.length() <= LOGIN_MAX_LENGTH;

    private static Predicate<String> checkPhoneNumberFormat = phoneNumber ->
            phoneNumber.matches("^[+]?[0-9() -]{10,18}$");

    private static Predicate<Order> checkSeatsAvailability = order -> {
        if (order == null || order.getCruise() == null || order.getCruise().getBoat() == null) {
            return false;
        }
        return order.getCruise().getBoat().getCapacity() -
                CruiseDao.getInstance().getBookedSeats(order.getCruise()) -
                order.getSeats() >= 0;
    };

    private static Predicate<Order> checkSeatsMore0 = order -> {
        if (order == null) {
            return false;
        }
        return order.getSeats() >= 0;
    };

    public static boolean validatePassword(String password) {
        return checkPasswordLength
                .and(checkSymbols)
                .test(password);
    }

    public static boolean validateLogin(String login) {
        return checkLoginDuplicate
                .and(checkSymbols)
                .test(login);
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        return checkPhoneNumberFormat
                .test(phoneNumber);
    }

    public static boolean validateOrder(Order order) {
        return checkSeatsMore0
                .and(checkSeatsAvailability)
                .test(order);
    }

    //--------------------------------------------------------------------------------------------------Cadre validation
    private static Predicate<Cadre> checkAllFieldsSymbols = cadre ->
            checkSymbolsOfRealName.test(cadre.getName() + cadre.getSurname() + cadre.getPosition() + cadre.getCharacteristic());

    public static boolean validateCadre(Cadre cadre) {
        return checkAllFieldsSymbols.test(cadre);
    }

}

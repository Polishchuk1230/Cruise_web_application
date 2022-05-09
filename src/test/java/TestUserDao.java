import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ua.com.cruises.model.Role;
import ua.com.cruises.model.User;
import ua.com.cruises.repository.UserDao;
import ua.com.cruises.repository.ConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TestUserDao {
    final private static String DATABASE_URL_FOR_TESTS = "jdbc:mysql://localhost/cruisecompany?user=root&password=password";

    private static Object mockConnection(Supplier<Object> supplier) throws SQLException {
        try (MockedStatic<ConnectionPool> connectionPoolMockedStatic = Mockito.mockStatic(ConnectionPool.class);
             Connection connection = DriverManager.getConnection(DATABASE_URL_FOR_TESTS))
        {
            connectionPoolMockedStatic.when(ConnectionPool::getConnection)
                    .thenReturn(connection);

            return supplier.get();
        }
    }

    private User testUser1 = new User(0, "Taras", "tester", "+380 (77) 777-77-71");
    private User testUser2 = new User(0, "Taras2", "tester", "+380 (77) 777-77-72");

    @Test
    public void testInsertMethod() throws SQLException {

        //Checks, if in DataBase there is not a user with the provided username.
        if (!(boolean) mockConnection(() ->
                UserDao.getInstance().isUsernameExists(testUser1.getUsername()))) {

            //Check that the user was successfully added
            Assertions.assertTrue(
                    (boolean) mockConnection(() ->
                            UserDao.getInstance().insert(testUser1)));

            //Check that the user's id was updated in the result of applying UserDao#insert method
            Assertions.assertTrue(testUser1.getId() > 0);

            //Check that we can NOT to insert a User with the same id
            testUser2.setId(testUser1.getId());
            Assertions.assertFalse(
                    (boolean) mockConnection(() ->
                            UserDao.getInstance().insert(testUser2)));
            testUser2.setId(0);

            //Check that we can NOT to insert a User with the same login
            String temp = testUser2.getUsername();
            testUser2.setUsername(testUser1.getUsername());
            Assertions.assertFalse(
                    (boolean) mockConnection(() ->
                            UserDao.getInstance().insert(testUser2)));
            testUser2.setUsername(temp);

            //Remove the testUser from DB after testing
            Assertions.assertTrue(
                    (boolean) mockConnection(() ->
                            UserDao.getInstance().remove(testUser1.getId())));

//            //Check that we can NOT remove from DB a user which one was not there
            Assertions.assertFalse(
                    (boolean) mockConnection(() ->
                            UserDao.getInstance().remove(testUser2.getId())));
        }
    }

    @Test
    public void testPullingOutAggregatedUser() throws SQLException {
        mockConnection(() -> UserDao.getInstance().insert(testUser1));

        //Pull out a User without a Role
        User user = (User) mockConnection(() -> UserDao.getInstance().find(testUser1.getId()).get());
        Assertions.assertTrue(user.getRoles().isEmpty());

        //
        Assertions.assertTrue(
                (boolean) mockConnection(() ->
                        UserDao.getInstance().addRoles(testUser1, Role.USER)));
        Assertions.assertTrue(
                (boolean) mockConnection(() ->
                        UserDao.getInstance().addRoles(testUser1, Role.USER, Role.ADMIN)));

        //Function compares two lists ignoring order of inner items
        BiFunction<List<Role>, List<Role>, Boolean> compareListsOfRoles = (roles, roles2) ->
            roles.size() == roles2.size() && roles.containsAll(roles2) && roles2.containsAll(roles);

        user = (User) mockConnection(() -> UserDao.getInstance().find(testUser1.getId()).get());
        Assertions.assertTrue(
                compareListsOfRoles.apply(
                        new ArrayList<Role>(List.of(Role.USER, Role.ADMIN)),
                        user.getRoles()));

        //---
        Assertions.assertTrue(
                (boolean) mockConnection(() ->
                        UserDao.getInstance().removeRoles(testUser1, Role.ADMIN)));
        Assertions.assertFalse(
                (boolean) mockConnection(() ->
                        UserDao.getInstance().removeRoles(testUser1, Role.USER, Role.ADMIN)));

        user = (User) mockConnection(() -> UserDao.getInstance().find(testUser1.getId()).get());
        Assertions.assertFalse(
                compareListsOfRoles.apply(
                        new ArrayList<Role>(List.of(Role.USER, Role.ADMIN)),
                        user.getRoles()));

        Assertions.assertTrue(
                compareListsOfRoles.apply(
                        new ArrayList<Role>(List.of(Role.USER)),
                        user.getRoles()));

        //Remove the testUser from DB after testing
        mockConnection(() ->
                UserDao.getInstance().remove(testUser1.getId()));
    }

}

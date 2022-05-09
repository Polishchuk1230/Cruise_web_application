package ua.com.cruises.repository;

import static ua.com.cruises.repository.DaoMethods.*;

import ua.com.cruises.model.Order;
import ua.com.cruises.model.Role;
import ua.com.cruises.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDao implements Dao<User> {
    private static UserDao instance;

    private UserDao() {}

    public static UserDao getInstance() {
        if (instance == null)
            instance = new UserDao();
        return instance;
    }

    /*
     * Method of mapping SQL row to Java Object
     * */
    //package-protected
    static User mapUser (ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("users.id"),
                resultSet.getString("users.username"),
                resultSet.getString("users.password"),
                resultSet.getString("users.phone_number"));
    }

    static Optional<Object> commonMassSelectMethod(String query, Statement statement) throws SQLException {
        List<User> resultList = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            User temp = mapUser(resultSet);
            while (true) {

                //If the variable temp contains a user with another, maps another user
                if (temp.getId() != resultSet.getInt("users.id")) {
                    resultList.add(temp);
                    temp = mapUser(resultSet);
                }

                //If role exists, adds this role to a user's list (if not, a user will stay without a role)
                if (resultSet.getString("users_roles.role") != null) {
                    temp.getRoles()
                            .add(
                                    Role.valueOf(
                                            resultSet.getString("users_roles.role")));
                }

                //If there isn't the next iteration, breaks the loop
                if (!resultSet.next()) {
                    resultList.add(temp);
                    break;
                }
            }
        }

        return Optional.of(resultList);
    }

    /*
     * Reducing boilerplate code. Used in next methods of current class:
     * */
    private Optional<User> findUserReducingBoilerplateCodeMethod(String query) {
        FunctionThrowsSQLExc<Statement, Optional<Object>> findBy = statement ->
                commonMassSelectMethod(query, statement);

        return provideStatementForFunction(findBy).map(list -> {
            List<User> temp = (List<User>) list;
            return !temp.isEmpty() ? temp.get(0) : null;
        });
    }

    @Override
    public Optional<User> find(int id) {
        final String QUERY = """
                                SELECT * FROM users
                                LEFT JOIN users_roles ON users.id = users_roles.user_id
                                WHERE users.id = """ + id;

        return findUserReducingBoilerplateCodeMethod(QUERY);
    }

    public Optional<User> find(String login) {
        final String QUERY = String.format("""
                SELECT * FROM users
                LEFT JOIN users_roles ON users.id = users_roles.user_id
                WHERE users.username = '%s'""", login);

        return findUserReducingBoilerplateCodeMethod(QUERY);
    }

    public Optional<User> findByOrderId(Order order) {
        final String QUERY = """
                SELECT * FROM users
                LEFT JOIN users_roles ON users.id = users_roles.user_id
                JOIN orders ON users.id = orders.user_id
                WHERE orders.id = """ + order.getId();

        return findUserReducingBoilerplateCodeMethod(QUERY);
    }

    @Override
    public List<User> findAll() {
        final String QUERY = "SELECT * FROM users LEFT JOIN users_roles ON users.id = users_roles.user_id";

        FunctionThrowsSQLExc<Statement, Optional<Object>> findAll = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (List<User>) provideStatementForFunction(findAll).orElse(new ArrayList<>());
    }

    /*
     * Writes provided in arguments Roles for a particular User into DataBase.
     * (Works NOT like a transaction)
     * */
    public boolean addRoles(User user, Role... roles) {
        final String QUERY_PATTERN = "INSERT IGNORE INTO users_roles (user_id, role) VALUES (%d, '%s')";

        FunctionThrowsSQLExc<Statement, Optional<Object>> addRoles = statement -> {
            for (Role role : roles) {
                String query = String.format(QUERY_PATTERN, user.getId(), role);
                int affectedRowsCount = statement.executeUpdate(query);
                if (affectedRowsCount == 1) {
                    user.getRoles().add(role);
                }
            }

            return Optional.of(true);
        };

        return (boolean) provideStatementForFunction(addRoles).orElse(false);
    }

    /*
    * Removes provided in arguments Roles for a particular User in DataBase.
    * If one or more provided roles were not contained earlier, nothing will be changed.
    * (Works like a transaction)
    * */
    public boolean removeRoles(User user, Role... roles) {
        final String QUERY_PATTERN = "DELETE FROM users_roles WHERE user_id = %d AND role = '%s'";

        FunctionThrowsSQLExc<Statement, Optional<Object>> removeRoles = statement -> {
            statement.getConnection().setAutoCommit(false); // <-- Start transaction
            for (Role role : roles) {
                String query = String.format(QUERY_PATTERN, user.getId(), role);
                int affectedRowsCount = statement.executeUpdate(query);
                if (affectedRowsCount != 1) {
                    statement.getConnection().rollback(); // <-- Rollback transaction
                    return Optional.of(false);
                }
            }

            statement.getConnection().commit(); // <-- Commit transaction
            return Optional.of(true);
        };

        return (boolean) provideStatementForFunction(removeRoles).orElse(false);
    }

    @Override
    public boolean insert(User user) {
        final String QUERY = String.format("INSERT INTO users (id, username, password, phone_number) VALUES (%d, '%s', '%s', '%s')",
                user.getId(), user.getUsername(), user.getPassword(), user.getPhoneNumber());

        FunctionThrowsSQLExc<Statement, Optional<Object>> insert = statement ->
                commonInsertNewRow(user, QUERY, statement);

        return (boolean) provideStatementForFunction(insert).orElse(false);
    }

    @Override
    public boolean update(User user) {
        final String QUERY = String.format("UPDATE users SET username = '%s', password = '%s', phone_number = '%s' WHERE id = %d",
                user.getUsername(), user.getPassword(), user.getPhoneNumber(), user.getId());

        FunctionThrowsSQLExc<Statement, Optional<Object>> update = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(update).orElse(false);
    }

    @Override
    public boolean remove(int id) {
        final String QUERY = "DELETE FROM users WHERE id = " + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> remove = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(remove).orElse(false);
    }

    /*
    * Method checks if a User with provided username exists in DataBase
    * */
    public boolean isUsernameExists(String username) {
        final String QUERY = String.format("SELECT * FROM users where username = '%s'", username);

        FunctionThrowsSQLExc<Statement, Optional<Object>> isUsernameExists = statement -> {
            boolean result = statement.executeQuery(QUERY).next();
            return Optional.of(result);
        };

        return (boolean) provideStatementForFunction(isUsernameExists).orElse(false);
    }

    /*
    * Method returns a List of image URIs assigned to a User from DataBase
    * */
    public List<String> findUserImagesByObj(User user) {
        final String QUERY = "SELECT uri FROM user_images where user_id = " + user.getId();

        FunctionThrowsSQLExc<Statement, Optional<Object>> findUserImagesByObj = statement -> {
            List<String> resultList = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery(QUERY);
            while (resultSet.next()) {
                resultList.add(
                        resultSet.getString("uri"));
            }

            return Optional.of(resultList);
        };

        return (List<String>) provideStatementForFunction(findUserImagesByObj).orElse(new ArrayList<>());
    }

    public boolean insertUserImagesById(int userId, List<String> fileNames) {
        final String QUERY = "INSERT INTO user_images (user_id, uri) VALUES " +
                fileNames.stream()
                        .map(str -> String.format("(%d, '%s')", userId, str))
                        .collect(Collectors.joining(", "));

        FunctionThrowsSQLExc<Statement, Optional<Object>> insertUserImagesById = statement -> {
            int affectedRowsCount = statement.executeUpdate(QUERY);
            if (affectedRowsCount == fileNames.size()) {
                return Optional.of(true);
            }
            return Optional.of(false);
        };

        return (boolean) provideStatementForFunction(insertUserImagesById).orElse(false);
    }
}

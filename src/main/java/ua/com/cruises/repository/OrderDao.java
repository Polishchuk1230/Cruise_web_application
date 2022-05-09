package ua.com.cruises.repository;

import static ua.com.cruises.repository.DaoMethods.*;

import ua.com.cruises.model.Order;
import ua.com.cruises.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class OrderDao implements Dao<Order> {
    private static OrderDao instance;

    private OrderDao() {}

    public static OrderDao getInstance() {
        if (instance == null)
            instance = new OrderDao();
        return instance;
    }

    /*
     * Method of mapping SQL row to Java Object
     * */
    //package-protected
    static Order mapOrder (ResultSet resultSet) throws SQLException {
        Order order = new Order(
                resultSet.getInt("orders.id"),
                resultSet.getInt("orders.seats"),
                resultSet.getBoolean("orders.confirmed"));

        //Order's BookTime field is set separately of its constructor because that data is created on DB's side.
        order.setBookTime(resultSet.getTimestamp("book_time"));

        return order;
    }

    private static Optional<Object> commonMassSelectMethod(String query, Statement statement) throws SQLException {
        List<Order> resultList = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            Order temp = (Order) findInList(resultSet.getInt("orders.id"), resultList);

            if (temp == null) {
                temp = mapOrder(resultSet); // <-- map Order
                temp.setUser(
                        UserDao.mapUser(resultSet)); // <-- map User (one mapping per each mapping of Order)
                temp.setCruise(
                        CruiseDao.mapCruise(resultSet)); // <-- map Cruise (one mapping per each mapping of Order)
                temp.getCruise()
                        .setBoat(
                                BoatDao.mapBoat(resultSet)); // <-- map Boat (one mapping per each mapping of Cruise)
                resultList.add(temp);
            }

            temp.getCruise().getPorts()
                    .add(
                            PortDao.mapPort(resultSet)); // <-- map Port (one mapping per iteration)
        }

        return Optional.of(resultList);
    }

    public boolean setConfirmedByOrderObj(Order order) {
        final String QUERY = String.format("UPDATE orders SET confirmed = %b WHERE id = %d",
                order.isConfirmed(), order.getId());

        FunctionThrowsSQLExc<Statement, Optional<Object>> setConfirmedByOrderObj = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(setConfirmedByOrderObj).orElse(false);
    }

    @Override
    public boolean insert(Order order) {
        if (order == null || order.getUser() == null || order.getCruise() == null) {
            return false;
        }

        final String QUERY = String.format("INSERT INTO orders (user_id, cruise_id, seats) VALUES (%d, %d, %d)",
                order.getUser().getId(), order.getCruise().getId(), order.getSeats());

        FunctionThrowsSQLExc<Statement, Optional<Object>> insert = statement ->
                commonInsertNewRow(order, QUERY, statement);

        return (boolean) provideStatementForFunction(insert).orElse(false);
    }

    @Override
    public Optional<Order> find(int id) {
        final String QUERY = String.format("""
                SELECT * FROM ports
                JOIN cruises_ports ON ports.id = cruises_ports.port_id
                JOIN cruises ON cruises_ports.cruise_id = cruises.id
                JOIN boats ON cruises.boat_id = boats.id
                JOIN orders ON cruises.id = orders.cruise_id
                JOIN users ON orders.user_id = users.id
                WHERE orders.id = %d
                ORDER BY orders.id DESC, cruises.id, cruises_ports.seq_number
                             """, id);

        FunctionThrowsSQLExc<Statement, Optional<Object>> find = statement ->
                commonMassSelectMethod(QUERY, statement);

        return provideStatementForFunction(find).map(list -> {
            List<Order> temp = (List<Order>) list;
            return !temp.isEmpty() ? temp.get(0) : null;
        });
    }

    public List<Order> findByUser(User user) {
        final String QUERY = String.format("""
                SELECT * FROM ports
                JOIN cruises_ports ON ports.id = cruises_ports.port_id
                JOIN cruises ON cruises_ports.cruise_id = cruises.id
                JOIN boats ON cruises.boat_id = boats.id
                JOIN orders ON cruises.id = orders.cruise_id
                JOIN users ON orders.user_id = users.id
                WHERE users.id = %d
                ORDER BY orders.id DESC, cruises.id, cruises_ports.seq_number
                             """, user.getId());

        FunctionThrowsSQLExc<Statement, Optional<Object>> find = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (List<Order>) provideStatementForFunction(find).orElse(new ArrayList<Order>());
    }

    @Override
    public Collection<Order> findAll() {
        final String QUERY = """
                SELECT * FROM ports
                JOIN cruises_ports ON ports.id = cruises_ports.port_id
                JOIN cruises ON cruises_ports.cruise_id = cruises.id
                JOIN boats ON cruises.boat_id = boats.id
                JOIN orders ON cruises.id = orders.cruise_id
                JOIN users ON orders.user_id = users.id
                ORDER BY orders.id DESC, cruises.id, cruises_ports.seq_number
                             """;

        DaoMethods.FunctionThrowsSQLExc<Statement, Optional<Object>> findAll = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (List<Order>) provideStatementForFunction(findAll).orElse(new ArrayList<>());
    }

    public Collection<Order> findAllNotConfirmed() {
        final String QUERY = """
                SELECT * FROM ports
                JOIN cruises_ports ON ports.id = cruises_ports.port_id
                JOIN cruises ON cruises_ports.cruise_id = cruises.id
                JOIN boats ON cruises.boat_id = boats.id
                JOIN orders ON cruises.id = orders.cruise_id
                JOIN users ON orders.user_id = users.id
                WHERE orders.confirmed = FALSE
                ORDER BY orders.id DESC, cruises.id, cruises_ports.seq_number
                             """;

        DaoMethods.FunctionThrowsSQLExc<Statement, Optional<Object>> findAll = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (List<Order>) provideStatementForFunction(findAll).orElse(new ArrayList<>());
    }

    @Override
    public boolean update(Order obj) {
        return false;
    }

    @Override
    public boolean remove(int id) {
        final String QUERY = "DELETE FROM orders WHERE id = " + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> remove = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(remove).orElse(false);
    }

}

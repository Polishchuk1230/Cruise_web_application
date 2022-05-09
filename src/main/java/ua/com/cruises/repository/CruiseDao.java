package ua.com.cruises.repository;

import static ua.com.cruises.repository.DaoMethods.*;

import ua.com.cruises.model.*;

import java.sql.*;
import java.util.*;

public class CruiseDao implements Dao<Cruise> {
    private static CruiseDao instance;

    private CruiseDao() {}

    public static CruiseDao getInstance() {
        if (instance == null)
            instance = new CruiseDao();
        return instance;
    }

    /*
     * Method of mapping SQL row to Java Object
     * */
    //package-protected
    static Cruise mapCruise (ResultSet resultSet) throws SQLException {
        return new Cruise(
                resultSet.getInt("cruises.id"),
                resultSet.getDate("cruises.start_date"),
                resultSet.getDate("cruises.end_date"),
                resultSet.getInt("cruises.cost"));
    }

    private static Optional<Object> commonMassSelectMethod(String query, Statement statement) throws SQLException {
        List<Cruise> resultList = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            Cruise temp = (Cruise) findInList(resultSet.getInt("cruises.id"), resultList);

            if (temp == null) {
                temp = mapCruise(resultSet); // <-- map Cruise
                temp.setBoat(
                        BoatDao.mapBoat(resultSet)); // <-- map Boat (one mapping per each mapping of Cruise)
                resultList.add(temp);
            }

            temp.getPorts()
                    .add(
                            PortDao.mapPort(resultSet)); // <-- map Port (one mapping per iteration)
        }

        return Optional.of(resultList);
    }

    /*
    * Returns amount of booked seats of a provided Cruise object
    * */
    public int getBookedSeats(Cruise cruise) {
        final String QUERY = "SELECT sum(seats) AS seats_sum FROM orders WHERE cruise_id = " + cruise.getId();

        FunctionThrowsSQLExc<Statement, Optional<Object>> getBookedSeats = statement -> {
            ResultSet resultSet = statement.executeQuery(QUERY);
            if (resultSet.next()) {
                return Optional.of(
                        resultSet.getInt("seats_sum"));
            }
            return Optional.empty();
        };

        return (int) provideStatementForFunction(getBookedSeats).orElse(-1);
    }

    /*
    * Assigns aggregated List of Ports with a Cruise in DataBase
    * Method designed to be used by another method - insert
    * */
    private static boolean insertRoute(Cruise cruise, Statement statement) throws SQLException {
        final String QUERY_PATTERN = "INSERT INTO cruises_ports (cruise_id, port_id, seq_number) VALUES (%d, %d, %d)";

        int counter = 1;
        for (Port port : cruise.getPorts()) {
            int affectedRowsCount =
                    statement.executeUpdate(
                            String.format(QUERY_PATTERN, cruise.getId(), port.getId(), counter++));
            if (affectedRowsCount != 1) {
                return false;
            }
        }
        return true;
    }

    /*
    * This method inserts into DataBase a new Cruise object AND assigns aggregated Boat object and List of Ports to
    * this Cruise object.
    * */
    @Override
    public boolean insert(Cruise cruise) {
        if (cruise == null || cruise.getBoat() == null || cruise.getPorts().isEmpty()) {
            return false;
        }

        final String QUERY = String.format("INSERT INTO cruises (id, boat_id, start_date, end_date, cost) VALUES (%d, %d, '%s', '%s', %d)",
                cruise.getId(), cruise.getBoat().getId(), cruise.getStartDate(), cruise.getEndDate(), cruise.getCost());

        FunctionThrowsSQLExc<Statement, Optional<Object>> insert = statement -> {
            statement.getConnection().setAutoCommit(false); // <-- Start transaction

            boolean resultOfInsertingCruise =
                    //insert a Cruise object to DB
                    (boolean) commonInsertNewRow(cruise, QUERY, statement).orElse(false);

            //assigning List<Port> with the Cruise in DB
            boolean resultOfAssigningPorts = insertRoute(cruise, statement);

            if (!resultOfInsertingCruise || !resultOfAssigningPorts) {
                statement.getConnection().rollback(); // <-- Rollback transaction
                return Optional.of(false);
            }

            statement.getConnection().commit(); // <-- Commit transaction
            return Optional.of(true);
        };

        return (boolean) provideStatementForFunction(insert).orElse(false);
    }

    @Override
    public Optional<Cruise> find(int id) {
        final String QUERY = String.format("""
                SELECT * FROM ports
                JOIN cruises_ports ON ports.id = cruises_ports.port_id
                JOIN cruises ON cruises_ports.cruise_id = cruises.id
                JOIN boats ON cruises.boat_id = boats.id
                WHERE cruises.id = %d
                ORDER BY cruises.id DESC, cruises_ports.seq_number
                            """, id);

        FunctionThrowsSQLExc<Statement, Optional<Object>> find = statement ->
                commonMassSelectMethod(QUERY, statement);

        return provideStatementForFunction(find).map(list -> {
            List<Cruise> temp = (List<Cruise>) list;
            return !temp.isEmpty() ? temp.get(0) : null;
        });
    }

    public Optional<Cruise> findByOrderId(Order order) {
        final String QUERY = String.format("""
                SELECT * FROM ports
                JOIN cruises_ports ON ports.id = cruises_ports.port_id
                JOIN cruises ON cruises_ports.cruise_id = cruises.id
                JOIN boats ON cruises.boat_id = boats.id
                JOIN orders ON cruises.id = orders.cruise_id
                WHERE orders.id = %d
                ORDER BY cruises.id DESC, cruises_ports.seq_number
                                            """, order.getId());

        FunctionThrowsSQLExc<Statement, Optional<Object>> findByOrderId = statement ->
                commonMassSelectMethod(QUERY, statement);

        return provideStatementForFunction(findByOrderId).map(list -> {
            List<Cruise> temp = (List<Cruise>) list;
            return !temp.isEmpty() ? temp.get(0) : null;
        });
    }

    @Override
    public List<Cruise> findAll() {
        final String QUERY = """
                SELECT * FROM ports
                JOIN cruises_ports ON ports.id = cruises_ports.port_id
                JOIN cruises ON cruises_ports.cruise_id = cruises.id
                JOIN boats ON cruises.boat_id = boats.id
                ORDER BY cruises.id DESC, cruises_ports.seq_number
                                """;

        FunctionThrowsSQLExc<Statement, Optional<Object>> findAll = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (List<Cruise>) provideStatementForFunction(findAll).orElse(new ArrayList<>());
    }

    @Override
    public boolean update(Cruise obj) {
        return false;
    }

    @Override
    public boolean remove(int id) {
        final String QUERY = "DELETE FROM cruises WHERE id = " + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> remove = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(remove).orElse(false);
    }

}

package ua.com.cruises.repository;

import static ua.com.cruises.repository.DaoMethods.*;

import ua.com.cruises.model.*;

import java.sql.*;
import java.util.*;

public class PortDao implements Dao<Port> {
    private static PortDao instance;

    private PortDao() {}

    public static PortDao getInstance() {
        if (instance == null)
            instance = new PortDao();
        return instance;
    }

    /*
    * Method of mapping SQL row to Java Object
    * */
    //package-protected
    static Port mapPort (ResultSet resultSet) throws SQLException {
        return new Port(
                resultSet.getInt("ports.id"),
                resultSet.getString("ports.country"),
                resultSet.getString("ports.city"));
    }

    static Optional<Object> commonMassSelectMethod(String query, Statement statement) throws SQLException {
        List<Port> resultList = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            resultList.add(
                    mapPort(resultSet));
        }

        return Optional.of(resultList);
    }

    @Override
    public boolean insert(Port port) {
        final String sqlPattern = "INSERT INTO ports (id, country, city) VALUES (?, ?, ?)";

        FunctionThrowsSQLExc<PreparedStatement, Optional<Object>> insert = prepStatement -> {
            prepStatement.setInt(1, port.getId());
            prepStatement.setString(2, port.getCountry());
            prepStatement.setString(3, port.getCity());
            int count = prepStatement.executeUpdate();

            if (count == 1) {
                ResultSet resultSet = prepStatement.getGeneratedKeys();
                if (port.getId() == 0 && resultSet.next()) {
                    port.setId(resultSet.getInt(1));
                }
                return Optional.of(true);
            }
            return Optional.of(false);
        };

        return (boolean) providePrepStatementForFunction(insert, sqlPattern).orElse(false);
    }

    @Override
    public boolean update(Port port) {
        final String QUERY = String.format("UPDATE ports SET country = '%s', city = '%s' WHERE id = %d",
                port.getCountry(), port.getCity(), port.getId());

        FunctionThrowsSQLExc<Statement, Optional<Object>> update = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(update).orElse(false);
    }

    @Override
    public Optional<Port> find(int id) {
        final String QUERY = "SELECT * FROM ports WHERE id = " + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> find = statement ->
                commonMassSelectMethod(QUERY, statement);

        return provideStatementForFunction(find).map(list -> {
            List<Port> temp = (List<Port>) list;
            return !temp.isEmpty() ? temp.get(0) : null;
        });
    }

    @Override
    public List<Port> findAll() {
        final String QUERY = "SELECT * FROM ports";

        FunctionThrowsSQLExc<Statement, Optional<Object>> findAll = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (List<Port>) provideStatementForFunction(findAll).orElse(new ArrayList<>());
    }

    @Override
    public boolean remove(int id) {
        final String QUERY = "DELETE FROM ports WHERE id = " + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> remove = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(remove).orElse(false);
    }

    /*
    * Method returns List of ports of a particular Cruise like a route.
    * */
    public ArrayList<Port> findByCruiseId(int cruiseId) {
        final String QUERY = String.format("""
                                    SELECT * FROM ports
                                    JOIN cruises_ports ON ports.id = cruises_ports.port_id
                                    JOIN cruises ON cruises_ports.cruise_id = cruises.id
                                    WHERE cruises.id = %d
                                    ORDER BY cruises_ports.seq_number
                                    """, cruiseId);

        FunctionThrowsSQLExc<Statement, Optional<Object>> findByCruiseId = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (ArrayList<Port>) provideStatementForFunction(findByCruiseId).orElse(new ArrayList<>());
    }
}

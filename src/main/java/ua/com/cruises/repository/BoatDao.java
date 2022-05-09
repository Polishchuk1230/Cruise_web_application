package ua.com.cruises.repository;

import ua.com.cruises.model.Boat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ua.com.cruises.repository.DaoMethods.*;

public class BoatDao implements Dao<Boat> {
    public static BoatDao instance;

    private BoatDao() {}

    public static BoatDao getInstance() {
        if (instance == null) {
            instance = new BoatDao();
        }
        return instance;
    }

    /*
     * Method of mapping SQL row to Java Object
     * */
    //package-protected
    static Boat mapBoat (ResultSet resultSet) throws SQLException {
        return new Boat(
                resultSet.getInt("boats.id"),
                resultSet.getString("boats.name"),
                resultSet.getInt("boats.capacity")
        );
    }

    private static Optional<Object> commonMassSelectMethod(String query, Statement statement) throws SQLException {
        List<Boat> resultList = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            Boat temp = (Boat) findInList(resultSet.getInt("boats.id"), resultList);

            if (temp == null) {
                temp = mapBoat(resultSet); // <-- map Boat
                temp.setCrew(
                        CrewDao.mapCrew(resultSet)); // <-- map Crew (one mapping per each mapping of Boat)
                resultList.add(temp);
            }

            if (temp.getCrew() != null) {
                temp.getCrew().getCadres()
                        .add(
                                CadreDao.mapCadre(resultSet)); // <-- map Cadre (one mapping per iteration)
            }
        }

        return Optional.of(resultList);
    }

    @Override
    public boolean insert(Boat boat) {
        final String QUERY = String.format("INSERT INTO boats (id, name, capacity) VALUES (%d, '%s', %d)",
                boat.getId(), boat.getName(), boat.getCapacity());

        FunctionThrowsSQLExc<Statement, Optional<Object>> insertOneRow = statement ->
                commonInsertNewRow(boat, QUERY, statement);

        return (boolean) provideStatementForFunction(insertOneRow).orElse(false);
    }

    /*
    * Reducing boilerplate code. Used in next methods of current class: find, findByCruiseId,
    * findByOrderId.
    * */
    private static Optional<Boat> findBoatReducingBoilerplateCodeMethod(String query) {
        FunctionThrowsSQLExc<Statement, Optional<Object>> findByBoatId = statement ->
                commonMassSelectMethod(query, statement);

        return provideStatementForFunction(findByBoatId).map(list -> {
            List<Boat> temp = (List<Boat>) list;
            return !temp.isEmpty() ? temp.get(0) : null;
        });
    }

    @Override
    public Optional<Boat> find(int id) {
        final String QUERY = """
                SELECT * FROM boats
                LEFT JOIN crews ON boats.id = crews.boat_id
                LEFT JOIN cadres ON crews.id = cadres.crew_id
                WHERE boats.id = """ + id;

        return findBoatReducingBoilerplateCodeMethod(QUERY);
    }

    public Optional<Boat> findByCruiseId(int cruiseId) {
        final String QUERY = """
                SELECT * FROM boats
                LEFT JOIN crews ON boats.id = crews.boat_id
                LEFT JOIN cadres ON crews.id = cadres.crew_id
                JOIN cruises ON boats.id = cruises.boat_id
                WHERE cruises.id = """ + cruiseId;

        return findBoatReducingBoilerplateCodeMethod(QUERY);
    }

    public Optional<Boat> findByOrderId(int orderId) {
        final String QUERY = """
                SELECT * FROM boats
                LEFT JOIN crews ON boats.id = crews.boat_id
                LEFT JOIN cadres ON crews.id = cadres.crew_id
                JOIN cruises ON cruises.boat_id = boats.id
                JOIN orders ON orders.cruise_id = cruises.id
                WHERE orders.id = """ + orderId;

        return findBoatReducingBoilerplateCodeMethod(QUERY);
    }

    @Override
    public Collection<Boat> findAll() {
        final String QUERY = """
                SELECT * FROM boats
                LEFT JOIN crews ON boats.id = crews.boat_id
                LEFT JOIN cadres ON crews.id = cadres.crew_id
                            """;

        FunctionThrowsSQLExc<Statement, Optional<Object>> findAll = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (List<Boat>) provideStatementForFunction(findAll).orElse(new ArrayList<>());
    }

    @Override
    public boolean update(Boat boat) {
        final String QUERY = String.format("UPDATE boats SET name = '%s', capacity = %d WHERE id = %d",
                boat.getName(), boat.getCapacity(), boat.getId());

        FunctionThrowsSQLExc<Statement, Optional<Object>> update = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(update).orElse(false);
    }

    @Override
    public boolean remove(int id) {
        final String QUERY = "DELETE FROM boats WHERE id = " + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> remove = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(remove).orElse(false);
    }
}

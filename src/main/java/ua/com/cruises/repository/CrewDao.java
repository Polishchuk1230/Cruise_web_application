package ua.com.cruises.repository;

import ua.com.cruises.model.Crew;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ua.com.cruises.repository.DaoMethods.*;

public class CrewDao implements Dao<Crew> {
    public static CrewDao instance;

    private CrewDao() {}
    public static CrewDao getInstance() {
        if (instance == null) {
            instance = new CrewDao();
        }
        return instance;
    }

    /*
     * Method of mapping SQL row to Java Object
     * */
    /*package-protected*/
    static Crew mapCrew (ResultSet resultSet) throws SQLException {
        if (resultSet.getInt("crews.id") == 0) {
            return null;
        }

        return new Crew(
                resultSet.getInt("crews.id"));
    }

    private static Optional<Object> commonMassSelectMethod(String query, Statement statement) throws SQLException {
        List<Crew> resultList = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            Crew temp = mapCrew(resultSet);
            while (true) {

                if (temp.getId() != resultSet.getInt("crews.id")) {
                    resultList.add(temp);
                    temp = mapCrew(resultSet);
                }

                temp.getCadres()
                        .add(
                                CadreDao.mapCadre(resultSet));

                if (!resultSet.next()) {
                    resultList.add(temp);
                    break;
                }
            }
        }

        return Optional.of(resultList);
    }

    /*
    * Writes into DataBase new row that represents new Crew object.
    * (This method does NOT assign all Cadres which may be contained inside it, for that purpose use
    * CadreDao.assignCrewId())
    * */
    @Override
    public boolean insert(Crew crew) {
        final String QUERY = String.format("INSERT INTO crews (id) VALUES (%d)", crew.getId());

        FunctionThrowsSQLExc<Statement, Optional<Object>> insertNewRow = statement ->
            commonInsertNewRow(crew, QUERY, statement);

        return (boolean) provideStatementForFunction(insertNewRow).orElse(false);
    }

    @Override
    public Optional<Crew> find(int id) {
        final String QUERY = """
                SELECT * FROM crews
                JOIN cadres ON crews.id = cadres.crew_id
                WHERE crews.id = """ + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> selectCrewById = statement ->
                commonMassSelectMethod(QUERY, statement);

        return provideStatementForFunction(selectCrewById).map(list -> {
            List<Crew> temp = (List<Crew>) list;
            return !temp.isEmpty() ? temp.get(0) : null;
        });
    }

    public Optional<Crew> findByBoatId(int boatId) {
        final String QUERY = """
                SELECT * FROM crews
                JOIN cadres ON crews.id = cadres.crew_id
                WHERE crews.boat_id = """ + boatId;

        FunctionThrowsSQLExc<Statement, Optional<Object>> selectCrewByBoatId = statement ->
                commonMassSelectMethod(QUERY, statement);

        return provideStatementForFunction(selectCrewByBoatId).map(list -> {
            List<Crew> temp = (List<Crew>) list;
            return !temp.isEmpty() ? temp.get(0) : null;
        });
    }

    @Override
    public Collection<Crew> findAll() {
        final String QUERY = "SELECT * FROM crews JOIN cadres ON crews.id = cadres.crew_id";

        FunctionThrowsSQLExc<Statement, Optional<Object>> selectAllCrews = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (ArrayList<Crew>) provideStatementForFunction(selectAllCrews).orElse(new ArrayList<>());
    }

    @Override
    public boolean update(Crew crew) {
        return false;
    }

    @Override
    public boolean remove(int id) {
        final String QUERY = "DELETE FROM crews WHERE id = " + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> deleteARow = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(deleteARow).orElse(false);
    }

    /*
    * Assigns provided boat_id with a crew in DataBase. (One to One relationship)
    * If boatId == 0, in DataBase, particular Crew will be assigned with no boat (NULL)
    * */
    public boolean assignBoatId(int boatId, Crew crew) {
        final String QUERY = String.format("UPDATE crews SET boat_id = %s WHERE id = %d", boatId != 0 ? boatId : "null", crew.getId());

        FunctionThrowsSQLExc<Statement, Optional<Object>> setBoatIdForCrew = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(setBoatIdForCrew).orElse(false);
    }
}

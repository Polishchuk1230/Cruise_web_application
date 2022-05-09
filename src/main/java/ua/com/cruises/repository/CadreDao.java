package ua.com.cruises.repository;

import ua.com.cruises.model.Cadre;

import java.sql.*;
import java.util.*;

import static ua.com.cruises.repository.DaoMethods.*;

public class CadreDao implements Dao<Cadre> {
    public static CadreDao instance;

    private CadreDao() {}
    public static CadreDao getInstance() {
        if (instance == null) {
            instance = new CadreDao();
        }
        return instance;
    }

    /*
     * Method of mapping SQL row to Java Object
     * */
    /*package-protected*/
    static Cadre mapCadre (ResultSet resultSet) throws SQLException {
        if (resultSet.getInt("cadres.id") == 0) {
            return null;
        }

        return new Cadre(
                resultSet.getInt("cadres.id"),
                resultSet.getString("cadres.name"),
                resultSet.getString("cadres.surname"),
                resultSet.getString("cadres.position"),
                resultSet.getString("cadres.characteristic"));
    }

    static Optional<Object> commonMassSelectMethod(String query, Statement statement) throws SQLException {
        List<Cadre> resultList = new ArrayList<>();

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            resultList.add(
                    mapCadre(resultSet));
        }

        return Optional.of(resultList);
    }

    @Override
    public boolean insert(Cadre cadre) {
        final String QUERY = String.format("INSERT INTO cadres (id, name, surname, position, characteristic) VALUES (%d, '%s', '%s', '%s', '%s')",
                cadre.getId(), cadre.getName(), cadre.getSurname(), cadre.getPosition(), cadre.getCharacteristic());

        FunctionThrowsSQLExc<Statement, Optional<Object>> insertNewRow = statement ->
            commonInsertNewRow(cadre, QUERY, statement);

        return (boolean) provideStatementForFunction(insertNewRow).orElse(false);
    }

    @Override
    public Optional<Cadre> find(int id) {
        final String QUERY = "SELECT * FROM cadres WHERE id = " + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> selectARow = statement ->
                commonMassSelectMethod(QUERY, statement);

        return provideStatementForFunction(selectARow).map(list -> {
            List<Cadre> temp = (List<Cadre>) list;
            return !temp.isEmpty() ? temp.get(0) : null;
        });
    }

    @Override
    public Collection<Cadre> findAll() {
        final String QUERY = "SELECT * FROM cadres";

        FunctionThrowsSQLExc<Statement, Optional<Object>> findAll = statement ->
                commonMassSelectMethod(QUERY, statement);

        //if there was an issue during fetching from DB, this method returns empty collection
        return (ArrayList<Cadre>) provideStatementForFunction(findAll).orElse(new ArrayList<Cadre>());
    }

    @Override
    public boolean update(Cadre newCadre) {
        final String QUERY = String.format("UPDATE cadres SET name = '%s', surname = '%s', position = '%s', characteristic = '%s' WHERE id = %d",
                newCadre.getName(), newCadre.getSurname(), newCadre.getPosition(), newCadre.getCharacteristic(), newCadre.getId());

        FunctionThrowsSQLExc<Statement, Optional<Object>> updateARow = statement ->
                commonUpdateRowMethod(QUERY, statement);

        return (boolean) provideStatementForFunction(updateARow).orElse(false);
    }

    @Override
    public boolean remove(int id) {
        final String QUERY = "DELETE FROM cadres WHERE id = " + id;

        FunctionThrowsSQLExc<Statement, Optional<Object>> deleteARow = statement ->
                commonUpdateRowMethod(QUERY, statement);
        
        return (boolean) provideStatementForFunction(deleteARow).orElse(false);
    }

    /*
    * Writes provided crewId for every single Cadre in arguments into DataBase.
    *
    * (Works like a transaction)
    * */
    public boolean assignCrewId(int crewId, Cadre... cadres) {
        final String PREPARE_QUERY = "UPDATE cadres SET crew_id = null WHERE crew_id = " + crewId;
        final String QUERY_PATTERN = "UPDATE cadres SET crew_id = %s WHERE id = %d";

        FunctionThrowsSQLExc<Statement, Optional<Object>> setCrewIdForCadres = statement -> {
            statement.getConnection().setAutoCommit(false); // <-- start transaction

            if (crewId != 0) {
                statement.executeUpdate(PREPARE_QUERY);
            }

            for (Cadre cadre : cadres) {
                String query = String.format(QUERY_PATTERN, crewId != 0 ? crewId : "null", cadre.getId());
                int affectedRowsCount = statement.executeUpdate(query);
                if (affectedRowsCount != 1) {
                    statement.getConnection().rollback(); // <-- rollback transaction
                    return Optional.of(false);
                }
            }

            statement.getConnection().commit(); // <-- commit transaction
            return Optional.of(true);
        };

        return (boolean) provideStatementForFunction(setCrewIdForCadres).orElse(false);
    }

    /*
    * Returns list of all Cadres with provided crewId.
    * If provided 0, returns all Cadres with NULL in cadre_id column.
    * */
    public List<Cadre> findAllWithCrewId(int crewId) {
        final String QUERY = "SELECT * FROM cadres WHERE crew_id " + (crewId != 0 ? "= "+crewId : "is null");

        FunctionThrowsSQLExc<Statement, Optional<Object>> selectAllCadresWithCrewId = statement ->
                commonMassSelectMethod(QUERY, statement);

        return (List<Cadre>) provideStatementForFunction(selectAllCadresWithCrewId).orElse(new ArrayList<Cadre>());
    }
}

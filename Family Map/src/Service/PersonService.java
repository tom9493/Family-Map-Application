package Service;

import DAO.AuthTokenDao;
import DAO.DataAccessException;
import DAO.Database;
import DAO.PersonDao;
import RequestResult.PersonResult;
import java.sql.Connection;

import static Server.Server.Logger;

public class PersonService {

    /**
     * Returns ALL family members of the current user. The current user is determined from the provided auth token.
     * @return PersonResult object
     */
    public PersonResult person(String authToken) throws DataAccessException {
        Logger.entering("PersonService", "person");

        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            PersonDao dao = new PersonDao(conn);
            AuthTokenDao Adao = new AuthTokenDao(conn);
            String associatedUsername = Adao.find(authToken);

            if (associatedUsername != null) {
                PersonResult personResult = new PersonResult(dao.getAllPeople(associatedUsername), true);
                if (personResult.getPeople().size() == 0) { throw new DataAccessException("Error: no people"); }
                db.closeConnection(!RegisterService.isTest);
                return personResult;
            }
            else { throw new DataAccessException("Error: Bad authToken"); }
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            throw e;
        }
    }
}

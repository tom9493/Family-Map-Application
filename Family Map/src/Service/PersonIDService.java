package Service;

import DAO.AuthTokenDao;
import DAO.DataAccessException;
import DAO.Database;
import DAO.PersonDao;
import RequestResult.PersonIDRequest;
import RequestResult.PersonIDResult;
import model.Person;
import java.sql.Connection;

import static Server.Server.Logger;

public class PersonIDService {

    /**
     * Returns the single Person object with the specified ID
     */
    public PersonIDResult personID(PersonIDRequest r, String authToken) throws DataAccessException {
        Logger.entering("PersonIDService", "personID");

        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            PersonDao dao = new PersonDao(conn);
            AuthTokenDao Adao = new AuthTokenDao(conn);
            String associatedUsername = Adao.find(authToken);

            if (associatedUsername != null) {
                Person p = dao.find(r.getPersonID(), associatedUsername);
                if (p == null) { throw new DataAccessException("Error: Bad personID or not associated with username"); }

                PersonIDResult result = new PersonIDResult(p.getPersonID(), p.getUsername(), p.getFirstName(),
                        p.getLastName(), p.getGender(), p.getFatherID(), p.getMotherID(), p.getSpouseID(), true);

                db.closeConnection(!RegisterService.isTest);
                return result;
            }
            else { throw new DataAccessException("Error: Bad authToken"); }
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            throw e;
        }
    }
}

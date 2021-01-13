package Service;

import DAO.AuthTokenDao;
import DAO.DataAccessException;
import DAO.Database;
import DAO.EventDao;
import RequestResult.EventResult;
import java.sql.Connection;

import static Server.Server.Logger;

public class EventService {

    /**
     * Returns ALL events for ALL family members of the current user. The current user is determined
     * from the provided auth token.
     *
     * @return
     */
    public EventResult event(String authToken) throws DataAccessException {
        Logger.entering("EventService", "event");

        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            EventDao eDao = new EventDao(conn);
            AuthTokenDao Adao = new AuthTokenDao(conn);
            String associatedUsername = Adao.find(authToken);

            if (associatedUsername != null) {
                EventResult result = new EventResult(eDao.getAllEvents(associatedUsername), true);
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


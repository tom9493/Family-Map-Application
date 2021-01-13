package Service;

import DAO.AuthTokenDao;
import DAO.DataAccessException;
import DAO.Database;
import DAO.EventDao;
import RequestResult.EventIDRequest;
import RequestResult.EventIDResult;
import model.Event;
import java.sql.Connection;

import static Server.Server.Logger;

public class EventIDService {

    /**
     * Returns the single Event object with the specified ID
     * @param r
     * @return
     */
    public EventIDResult eventID(EventIDRequest r, String authTokenString) throws DataAccessException {
        Logger.entering("EventIDService", "eventID");

        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            EventDao eDao = new EventDao(conn);
            AuthTokenDao Adao = new AuthTokenDao(conn);
            String associatedUsername = Adao.find(authTokenString);

            if (associatedUsername != null) {
                Event event = eDao.find(r.getEventID(), associatedUsername);

                if (event == null) { throw new DataAccessException("Error: Bad eventID or not associated with user"); }
                EventIDResult result = new EventIDResult(event.getEventID(), event.getAssociatedUsername(), event.getPersonID(),
                        event.getLatitude(), event.getLongitude(), event.getCountry(), event.getCity(),
                        event.getEventType(), event.getYear(), true);

                db.closeConnection(!RegisterService.isTest);
                return result;
            }
            else { throw new DataAccessException("Error: Bad authToken"); }
        } catch(DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            throw e;
        }
    }
}

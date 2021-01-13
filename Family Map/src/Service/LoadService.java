package Service;

import DAO.DataAccessException;
import DAO.Database;
import RequestResult.LoadRequest;
import RequestResult.LoadResult;

import static Server.Server.Logger;

public class LoadService {

    /**
     * Clears all data from the database (just like the /clear API), and then loads the posted user, person,
     * and event data into the database.
     * @param r
     * @return
     */
    public LoadResult load(LoadRequest r) throws DataAccessException {
        Logger.entering("LoadService", "load");

        Database db = new Database();
        try {
            db.openConnection();
            String resMessage = "Successfully added " + r.getUsers().size() + " users, " + r.getPersons().size() +
                    " persons, and " + r.getEvents().size() + " events to the database.";

            LoadResult result = new LoadResult(db.load(r.getUsers(), r.getPersons(), r.getEvents()));
            result.setMessage(resMessage);
            db.closeConnection(!RegisterService.isTest);
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            throw e;
        }
    }
}

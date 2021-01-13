package Service;

import DAO.DataAccessException;
import DAO.Database;
import RequestResult.ClearResult;

import static Server.Server.Logger;

public class ClearService {

    /**
     * CLears ALL data from database
     * @return
     */
    public ClearResult clear() throws DataAccessException {
        Logger.entering("ClearService", "clear");

        Database db = new Database();
        try {
            db.openConnection();
            db.clearTables();

            ClearResult result = new ClearResult(true);
            result.setMessage("Clear succeeded");
            db.closeConnection(!RegisterService.isTest);
            return result;
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            throw e;
        }
    }

}

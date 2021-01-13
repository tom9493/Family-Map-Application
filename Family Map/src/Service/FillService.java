package Service;

import DAO.*;
import RequestResult.FillRequest;
import RequestResult.FillResult;
import model.Person;
import model.User;
import java.io.FileNotFoundException;
import java.sql.Connection;

import static Server.Server.Logger;

public class FillService {

    /**
     * Deletes prexisting data associated with user and repopulates generations and events
     * Generation default is 4, but users can insert their own amount of generations
     * @param r
     * @return
     */
    public FillResult fill(FillRequest r) throws DataAccessException, FileNotFoundException {
        Logger.entering("FillService", "fill");

        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            PersonDao pDao = new PersonDao(conn);
            UserDao uDao = new UserDao(conn);
            EventDao eDao = new EventDao(conn);

            // Get person object associated with the user indicated
            User user = uDao.findUser(r.getUserName());
            if (user == null) { throw new DataAccessException("Error: User not found"); }
            String personID = pDao.getPersonID(user.getUsername(), user.getFirstName(), user.getLastName(), user.getGender());
            Person person = pDao.find(personID, r.getUserName());

            // Clear all events and people currently linked to this user
            eDao.removeEvents(r.getUserName());
            pDao.removePeople(r.getUserName());

            // Fill database with new family tree
            String resMessage = "Successfully added " + numPeopleAdded(r.getGenerations()) +
                    " persons and " + numEventsAdded(r.getGenerations()) + " events to the database.";
            FillResult result = new FillResult(db.fill(person, r.getGenerations()));
            result.setMessage(resMessage);

            // If this is a test, don't commit. else commit
            db.closeConnection(!RegisterService.isTest);
            return result;
        } catch (DataAccessException | FileNotFoundException e) {
            e.printStackTrace();
            db.closeConnection(false);
            throw e;
        }
    }

    /**
     * Gets the number of people added based on generations provided
     * @param generations
     * @return
     */
    public int numPeopleAdded(int generations) {
        int num = 2;
        for (int i = 0; i < generations; ++i) { num = num * 2; }
        return num - 1;
    }

    /**
     * Gets the number of events added based on generations provided
     * @param generations
     * @return
     */
    public int numEventsAdded(int generations) { return (numPeopleAdded(generations) * 3) - 2; }
}

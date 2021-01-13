package Service;

import DAO.*;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import model.AuthToken;
import model.Person;
import model.User;
import java.io.FileNotFoundException;
import java.sql.Connection;

import static Server.Server.Logger;

public class RegisterService {
    static boolean isTest = false;

    /**
     * Registers a new User, creating account, making generations, logging user in, and return authToken + personID
     * @param r RegisterRequest object
     * @return RegisterResult object
     */
    public RegisterResult register(RegisterRequest r) throws DataAccessException, FileNotFoundException {
        Logger.entering("RegisterService", "register");

        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            UserDao uDao = new UserDao(conn);
            PersonDao pDao = new PersonDao(conn);
            EventDao eDao = new EventDao(conn);
            AuthTokenDao aDao = new AuthTokenDao(conn);
            Generator generator = new Generator();

            // First see if this username is already taken
            if (uDao.findUser(r.getUserName()) != null) { throw new DataAccessException("Error: User already registered"); }

            // Generate necessary information for Register function
            String authTokenString = generator.randStringGenerator(12);
            String personID = generator.randStringGenerator(10);

            // Create user and person objects out of information provided. Insert them into database
            User user = new User(r.getUserName(), r.getPassword(), r.getEmail(), r.getFirstName(), r.getLastName(),
                    r.getGender());
            Person person = new Person(personID, r.getUserName(), r.getFirstName(), r.getLastName(), r.getGender());
            AuthToken authToken = new AuthToken(r.getUserName(), authTokenString);

            uDao.insert(user);
            pDao.insert(person);
            aDao.insert(authToken);
            eDao.removeEvents(r.getUserName());
            pDao.removePeople(r.getUserName());

            // After User and their person object are added, fill the table with generated events and people
            db.fill(person, 4);

            // If this is a test, dont commit. else commit
            if (r.getUserName().equals("tomTest") || r.getUserName().equals("saraTest")) {
                db.closeConnection(false);
            } else { db.closeConnection(!isTest); }

            return new RegisterResult(authTokenString, r.getUserName(), personID, true);

        } catch (DataAccessException | FileNotFoundException e) {
            e.printStackTrace();
            db.closeConnection(false);
            throw e;
        }
    }
}

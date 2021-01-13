package Service;

import DAO.*;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import model.AuthToken;
import model.User;
import java.sql.Connection;

import static Server.Server.Logger;

public class LoginService {

    /**
     * Logs in User with the given username and password
     * @param r
     * @return
     */
    public LoginResult login(LoginRequest r) throws DataAccessException {
        Logger.entering("LoginService", "login");

        Database db = new Database();
        try {
            Connection conn = db.getConnection();
            UserDao uDao = new UserDao(conn);
            Generator generator = new Generator();
            User user = uDao.find(r.getUserName(), r.getPassword()); // Find user linked with Username and password

            if (user != null) {                               // If one is found, construct authToken and login result
                AuthTokenDao aDao = new AuthTokenDao(conn);
                String token = generator.randStringGenerator(12);
                AuthToken authToken = new AuthToken(r.getUserName(), token);
                aDao.insert(authToken);

                PersonDao pDao = new PersonDao(conn);
                String ID = pDao.getPersonID(r.getUserName(), user.getFirstName(), user.getLastName(), user.getGender());
                db.closeConnection(!RegisterService.isTest);
                return new LoginResult(token, r.getUserName(), ID, true);
            }
            else { throw new DataAccessException("Error: Login attempt failed"); }
        } catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);  // Must close connection before exception goes further
            throw e;
        }
    }
}

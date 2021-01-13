package Handle;

import java.io.*;
import java.net.HttpURLConnection;
import DAO.DataAccessException;
import RequestResult.*;
import Service.PersonIDService;
import Service.PersonService;
import com.sun.net.httpserver.*;

import static Server.Server.Logger;

public class PersonHandler implements HttpHandler {

    private String personID;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Logger.entering("PersonHandler", "handle");

        PersonResult personResult;
        PersonIDResult personIDResult;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                String authToken;

                if (reqHeaders.containsKey("Authorization")) { authToken = reqHeaders.getFirst("Authorization"); }
                else { throw new DataAccessException("No authToken Header"); }
                if (authToken == null) { throw new DataAccessException("No authToken"); }

                String URI = exchange.getRequestURI().toString();
                parsePerson(URI);

                if (personID.length() != 0) {
                    PersonIDService ps = new PersonIDService();
                    PersonIDRequest personIDRequest = new PersonIDRequest(personID);
                    personIDResult = ps.personID(personIDRequest, authToken);
                    String respData = EncoderDecoder.decode(personIDResult);

                    if (!personIDResult.isSuccess()) { ReadWrite.handleFailure(exchange); }
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    ReadWrite.writeString(respData, exchange.getResponseBody());
                }
                else {
                    PersonService ps = new PersonService();
                    personResult = ps.person(authToken);
                    String respData = EncoderDecoder.decode(personResult);

                    if (!personResult.isSuccess()) { ReadWrite.handleFailure(exchange); }
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    ReadWrite.writeString(respData, exchange.getResponseBody());
                }
                exchange.getResponseBody().close();
            }
        } catch (IOException | DataAccessException e) {
            personIDResult = new PersonIDResult();
            personIDResult.setMessage(e.getMessage());
            ReadWrite.handleCatch(exchange, personIDResult);
        }
    }

    /**
     * Parses the URI for person
     * @param URI
     */
    public void parsePerson(String URI) {
        int append = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < URI.length(); ++i) {
            if (URI.charAt(i) == '/') { append = 1; ++i; }
            if (append != 0) { sb.append(URI.charAt(i)); }
        }
        personID = sb.toString();
    }
}

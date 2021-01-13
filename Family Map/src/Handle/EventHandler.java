package Handle;

import java.io.*;
import java.net.*;
import DAO.DataAccessException;
import RequestResult.EventIDRequest;
import RequestResult.EventIDResult;
import RequestResult.EventResult;
import Service.EventIDService;
import Service.EventService;
import com.sun.net.httpserver.*;
import static Server.Server.Logger;

public class EventHandler implements HttpHandler {

    private String eventID;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Logger.entering("EventHandler", "handle");
        EventResult eventResult;
        EventIDResult eventIDResult;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                String authToken;

                if (reqHeaders.containsKey("Authorization")) { authToken = reqHeaders.getFirst("Authorization"); }
                else { throw new DataAccessException("Error: No authToken Header"); }
                if (authToken == null) { throw new DataAccessException("Error: No authToken"); }

                String URI = exchange.getRequestURI().toString();
                parseEvent(URI);

                if (eventID.length() != 0) {
                    EventIDService es = new EventIDService();
                    EventIDRequest eventIDRequest = new EventIDRequest(eventID);
                    eventIDResult = es.eventID(eventIDRequest, authToken);
                    String respData = EncoderDecoder.decode(eventIDResult);

                    if (!eventIDResult.isSuccess()) { ReadWrite.handleFailure(exchange); }
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    ReadWrite.writeString(respData, exchange.getResponseBody());
                }

                else {
                    EventService es = new EventService();
                    eventResult = es.event(authToken);
                    String respData = EncoderDecoder.decode(eventResult);

                    if (!eventResult.isSuccess()) { ReadWrite.handleFailure(exchange); }
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    ReadWrite.writeString(respData, exchange.getResponseBody());
                }

                exchange.getResponseBody().close();
            }
        } catch (IOException | DataAccessException e) {
            eventResult = new EventResult();
            eventResult.setMessage(e.getMessage());
            ReadWrite.handleCatch(exchange, eventResult);
        }
    }

    /**
     * Parses the URI for event
     * @param URI
     */
    public void parseEvent(String URI) {
        int append = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < URI.length(); ++i) {
            if (URI.charAt(i) == '/') { append = 1; ++i; }
            if (append != 0) { sb.append(URI.charAt(i)); }
        }
        eventID = sb.toString();
    }
}

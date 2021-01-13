package Handle;

import java.io.*;
import java.net.*;
import DAO.DataAccessException;
import RequestResult.FillRequest;
import RequestResult.FillResult;
import Service.FillService;
import com.sun.net.httpserver.*;

import static Server.Server.Logger;

public class FillHandler implements HttpHandler {
    private String username;
    private int generations = 4;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Logger.entering("FillHandler", "handle");
        FillResult fillResult;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                FillService fs = new FillService();                                     // fillService object
                FillRequest fillRequest = new FillRequest();                           // fillRequest object

                String URI = exchange.getRequestURI().toString();                      // parse URI for username and
                parseFill(URI);                                                       // possible generation change

                fillRequest.setUserName(username);                                      // Set username and generations
                fillRequest.setGenerations(generations);                               // for request object
                fillResult = fs.fill(fillRequest);                                    // request -> service -> result
                String respData = EncoderDecoder.decode(fillResult);                 // result -> json String

                if (!fillResult.isSuccess()) { ReadWrite.handleFailure(exchange); }        // Send headers (Fail)
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);  // Send headers (OK)
                ReadWrite.writeString(respData, exchange.getResponseBody());            // json String -> response body
                exchange.getResponseBody().close();                                    // Send body
            }
        } catch (IOException | DataAccessException e) {
            fillResult = new FillResult();
            fillResult.setMessage(e.getMessage());
            ReadWrite.handleCatch(exchange, fillResult);
        }
    }

    /**
     * Parses the URI for fill. Gets the username we need to fill and number of generations (default 4)
     * @param URI
     */
    public void parseFill(String URI) {
        int append = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < URI.length(); ++i) {
            if (URI.charAt(i) == '/') {
                if (append == 0) { append = 1; ++i; }
                else if (append == 1) { append = 2; generations = Integer.parseInt(String.valueOf(URI.charAt(i + 1))); }
            }
            if (append == 1) { sb.append(URI.charAt(i)); }
        }
        username = sb.toString();
    }

}

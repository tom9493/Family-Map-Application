package Handle;

import java.io.*;
import java.net.*;
import DAO.DataAccessException;
import RequestResult.ClearResult;
import Service.ClearService;
import com.sun.net.httpserver.*;

import static Server.Server.Logger;


public class ClearHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Logger.entering("ClearHandler", "handle");
        ClearResult clearResult;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                ClearService cs = new ClearService();                                           // ClearService object
                clearResult = cs.clear();                                                      // service -> result
                String respData = EncoderDecoder.decode(clearResult);                         // result -> json String

                if (!clearResult.isSuccess()) { ReadWrite.handleFailure(exchange); }        // Send headers (Fail)
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);   // Send headers (OK)

                ReadWrite.writeString(respData, exchange.getResponseBody());            // json String -> response body
                exchange.getResponseBody().close();                                    // Send body
            }
        } catch (IOException | DataAccessException e) {
            clearResult = new ClearResult();
            clearResult.setMessage(e.getMessage());
            ReadWrite.handleCatch(exchange, clearResult);
        }
    }
}

package Handle;

import java.io.*;
import java.net.HttpURLConnection;
import DAO.DataAccessException;
import RequestResult.LoadRequest;
import RequestResult.LoadResult;
import Service.LoadService;
import com.sun.net.httpserver.*;

import static Server.Server.Logger;

public class LoadHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Logger.entering("LoadHandler", "handle");
        LoadResult loadResult;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                LoadService ls = new LoadService();                                       // loadService object
                LoadRequest loadRequest = new LoadRequest();                             // loadRequest object
                String reqData = ReadWrite.readString(exchange.getRequestBody());

                loadRequest = (LoadRequest) EncoderDecoder.encode(reqData, loadRequest);  // json string -> request
                loadResult = ls.load(loadRequest);                                       // request -> service -> result
                String respData = EncoderDecoder.decode(loadResult);                    // result -> json String

                if (!loadResult.isSuccess()) { ReadWrite.handleFailure(exchange); }        // Send headers (Fail)
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);  // Send headers (OK)
                ReadWrite.writeString(respData, exchange.getResponseBody());            // json String -> response body
                exchange.getResponseBody().close();                                    // Send body
            }
        } catch (IOException | DataAccessException e) {
            loadResult = new LoadResult();
            loadResult.setMessage(e.getMessage());
            ReadWrite.handleCatch(exchange, loadResult);
        }
    }
}


package Handle;

import DAO.DataAccessException;
import RequestResult.RegisterRequest;
import RequestResult.RegisterResult;
import Service.RegisterService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.HttpURLConnection;

import static Server.Server.Logger;

public class RegisterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Logger.entering("RegisterHandler", "handle");

        RegisterResult registerResult;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                RegisterRequest registerRequest = new RegisterRequest();
                RegisterService rs = new RegisterService();
                String reqData = ReadWrite.readString(exchange.getRequestBody());

                registerRequest = (RegisterRequest) EncoderDecoder.encode(reqData, registerRequest);
                registerResult = rs.register(registerRequest);
                String respData = EncoderDecoder.decode(registerResult);

                if (!registerResult.isSuccess()) { ReadWrite.handleFailure(exchange); }
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                ReadWrite.writeString(respData, exchange.getResponseBody());
                exchange.getResponseBody().close();
            }
        } catch (IOException | DataAccessException e) {
            registerResult = new RegisterResult();
            registerResult.setMessage(e.getMessage());
            ReadWrite.handleCatch(exchange, registerResult);
        }
    }
}

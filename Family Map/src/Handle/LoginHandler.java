package Handle;

import java.io.*;
import java.net.*;
import DAO.DataAccessException;
import RequestResult.LoginRequest;
import RequestResult.LoginResult;
import Service.LoginService;
import com.sun.net.httpserver.*;

import static Server.Server.Logger;

public class LoginHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Logger.entering("LoginHandler", "handle");

        LoginResult loginResult;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                LoginRequest loginRequest = new LoginRequest();
                LoginService ls = new LoginService();

                String reqData = ReadWrite.readString(exchange.getRequestBody());
                loginRequest = (LoginRequest) EncoderDecoder.encode(reqData, loginRequest);
                loginResult = ls.login(loginRequest);
                String respData = EncoderDecoder.decode(loginResult);

                if (!loginResult.isSuccess()) { ReadWrite.handleFailure(exchange); }
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                ReadWrite.writeString(respData, exchange.getResponseBody());
                exchange.getResponseBody().close();
            }
        } catch (IOException | DataAccessException e) {
            loginResult = new LoginResult();
            loginResult.setMessage(e.getMessage());
            ReadWrite.handleCatch(exchange, loginResult);
        }
    }
}

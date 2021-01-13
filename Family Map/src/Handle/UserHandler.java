package Handle;

import java.io.*;
import com.sun.net.httpserver.*;

import static Server.Server.Logger;

public class UserHandler implements HttpHandler {

    private final LoginHandler loginHandler = new LoginHandler();
    private final RegisterHandler registerHandler = new RegisterHandler();

   @Override
    public void handle(HttpExchange exchange) throws IOException {

        Logger.entering("UserHandler", "handle");

        if (exchange.getRequestURI().toString().contains("login")) { loginHandler.handle(exchange); }
        if (exchange.getRequestURI().toString().contains("register")) { registerHandler.handle(exchange); }
    }
}



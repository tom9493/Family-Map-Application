package Handle;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import com.sun.net.httpserver.*;

import static Server.Server.Logger;

public class MyFileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Logger.entering("MyFIleHandler", "handle");

        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                String urlPath = exchange.getRequestURI().toString();
                if (urlPath == null || urlPath.equals("/")) { urlPath = "/index.html"; }

                String filePath = "web" + urlPath;
                File file = new File(filePath);

                if (!file.exists()) { throw new IOException("Error: File does not exist"); }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream respBody = exchange.getResponseBody();
                    Files.copy(file.toPath(), respBody);
                    success = true;
                    respBody.close();
                }
            }

            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
                exchange.getResponseBody().close();
            }
        } catch (IOException e) {
            String filePath = "web/HTML/404.html";
            File file = new File(filePath);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            OutputStream respBody = exchange.getResponseBody();
            Files.copy(file.toPath(), respBody);
            respBody.close();
            e.printStackTrace();
        }
    }
}

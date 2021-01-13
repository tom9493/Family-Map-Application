package Handle;

import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.HttpURLConnection;

public class ReadWrite {

    /**
     * Writes a String to an OutputStream
     * @param str
     * @param os
     * @throws IOException
     */
    public static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    /**
     * Reads a String from an InputStream.
     * @param is
     * @return
     * @throws IOException
     */
     public static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /**
     * Sends 500 code header
     * @param exchange
     * @throws IOException
     */
    static void handleFailure(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0); // Send headers (500)
    }

    /**
     * Sends 400 code header
     * @param exchange
     * @param o
     * @throws IOException
     */
    static void handleCatch(HttpExchange exchange, Object o) throws IOException {
        String respData = EncoderDecoder.decode(o);
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); // Send headers (400)
        ReadWrite.writeString(respData, exchange.getResponseBody());
        exchange.getResponseBody().close();
    }
}

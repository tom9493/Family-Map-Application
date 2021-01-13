package Server;

import DAO.DataAccessException;
import Handle.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import com.sun.net.httpserver.*;

public class Server {
    private static final int MAX_WAITING_CONNECTIONS = 12;
    public static Logger Logger;

    static {
        try { initLog(); }
        catch (IOException e) {
            Logger.log(Level.SEVERE, e.getMessage(), e);
            System.out.println("Could not initialize log: " + e.getMessage());
        }
    }

    private static void initLog() throws IOException {
        Level logLevel = Level.FINEST;

        Logger = java.util.logging.Logger.getLogger("FamilyMap");
        Logger.setLevel(logLevel);
        Logger.setUseParentHandlers(false);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);
        consoleHandler.setFormatter(new SimpleFormatter());
        Logger.addHandler(consoleHandler);

        FileHandler fileHandler = new FileHandler("log.txt", false);
        fileHandler.setLevel(logLevel);
        fileHandler.setFormatter(new SimpleFormatter());
        Logger.addHandler(fileHandler);
    }

    private HttpServer server;

    private void run(String portNumber) {
        Logger.info("Initializing HTTP Server");

        try {
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch (IOException e) {
            Logger.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        server.setExecutor(null);
        Logger.info("Creating contexts");

        server.createContext("/person", new PersonHandler());
        server.createContext("/event", new EventHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/user", new UserHandler());
        server.createContext("/", new MyFileHandler());

        Logger.info("Starting server");
        server.start();
        Logger.info("Server started");
    }

    public static void main(String[] args) throws FileNotFoundException, DataAccessException {
        String portNumber = "8080";
        new Server().run(portNumber);
    }
}



package manager.server;

import com.sun.net.httpserver.HttpServer;
import task.*;
import manager.*;
import manager.server.handlers.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080),0);
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/subtasks", new TasksHandler(manager));
        httpServer.createContext("/epics", new TasksHandler(manager));
        httpServer.createContext("/history", new TasksHandler(manager));
        httpServer.createContext("/prioritized", new TasksHandler(manager));
        System.out.println("Сервер создан на порту " + PORT);
    }

    public static void main(String[] args) throws IOException {
        final HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
        httpTaskServer.start();
    }

    public void start() {
        httpServer.start();
        System.out.println("Server start");
    }
    public void stop() {
        httpServer.stop(0);
        System.out.println("Server stop");
    }

}

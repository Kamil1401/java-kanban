package tracker.http.httpserver;

import com.sun.net.httpserver.HttpServer;
import tracker.http.handlers.*;
import tracker.managers.Managers;
import tracker.managers.taskmanager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager taskManager) {
        this.manager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.startServer();
        taskServer.stopServer();
    }


    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/subtasks", new SubtaskHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(manager));
        server.start();
        System.out.println("HTTP-сервер запущен на порту 8080");
    }

    public void stopServer() {
        server.stop(1);
        System.out.println("Сервер остановлен.");
    }
}

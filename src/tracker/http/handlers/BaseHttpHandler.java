package tracker.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tracker.http.handlers.adapters.DurationAdapter;
import tracker.http.handlers.adapters.LocalDateTimeAdapter;
import tracker.managers.taskmanager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;
    protected static Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handleRequest(exchange);
    }

    abstract void handleRequest(HttpExchange exchange) throws IOException;

    public static Gson getGson() {
        return gson;
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "Задача не найдена", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "Задача пересекается с уже существующей", 406);
    }

    protected Optional<Integer> getTaskIdFromPath(HttpExchange exchange) {
        String[] pathSegments = exchange.getRequestURI().getPath().split("/");
        if (pathSegments.length < 3) {
            return Optional.empty();
        }
        try {
            int id = Integer.parseInt(pathSegments[2]);
            return Optional.of(id);
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат id");
            return Optional.empty();
        }
    }
}
package tracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import tracker.managers.taskmanager.TaskManager;
import tracker.model.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> handleTheGetRequest(exchange);
            case "POST" -> handleThePostRequest(exchange);
            case "DELETE" -> handleTheDeleteRequest(exchange);
            default -> sendText(exchange, "Метод не поддерживается", 405);
        }
    }

    private void handleTheGetRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalId = getTaskIdFromPath(exchange);
        String path = exchange.getRequestURI().getPath();
        String[] pathSegments = path.split("/");

        if (pathSegments.length == 4 && optionalId.isPresent()) {
            int id = optionalId.get();
            Epic epic = taskManager.getEpic(id);
            if (epic == null) {
                sendNotFound(exchange);
                return;
            }
            sendText(exchange, gson.toJson(taskManager.getSubtasksForEpic(epic)), 200);
            return;
        }
        if (pathSegments.length == 3 && optionalId.isPresent()) {
            int id = optionalId.get();
            Epic epic = taskManager.getEpic(id);
            if (epic == null) {
                sendNotFound(exchange);
                return;
            }
            sendText(exchange, gson.toJson(epic), 200);
            return;
        }
        sendText(exchange, gson.toJson(taskManager.getEpics()), 200);
    }

    private void handleThePostRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        Epic epic = gson.fromJson(body, Epic.class);

        taskManager.addEpic(epic);
        sendText(exchange, "Эпик успешно создан", 201);
    }

    private void handleTheDeleteRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalId = getTaskIdFromPath(exchange);
        if (optionalId.isPresent()) {
            int id = optionalId.get();
            taskManager.removeEpic(id);
            sendText(exchange, "Эпик успешно удален", 200);
        }
    }
}
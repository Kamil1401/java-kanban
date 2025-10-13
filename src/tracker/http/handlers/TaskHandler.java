package tracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import tracker.managers.taskmanager.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
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
        if (optionalId.isPresent()) {
            int id = optionalId.get();
            Task task = taskManager.getTask(id);
            if (task == null) {
                sendNotFound(exchange);
                return;
            }
            sendText(exchange, gson.toJson(task), 200);
            return;
        }
        sendText(exchange, gson.toJson(taskManager.getTasks()), 200);
    }

    private void handleThePostRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalId = getTaskIdFromPath(exchange);
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        Task task = gson.fromJson(body, Task.class);

        try {
            if (optionalId.isPresent()) {
                task.setId(optionalId.get());
                taskManager.updateTask(task);
                sendText(exchange, "Задача успешно обновлена", 201);
            } else {
                taskManager.addTask(task);
                sendText(exchange, "Задача успешно создана", 201);
            }
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleTheDeleteRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> optionalId = getTaskIdFromPath(exchange);
        if (optionalId.isPresent()) {
            int id = optionalId.get();
            Task task = taskManager.getTask(id);
            if (task == null) {
                sendNotFound(exchange);
                return;
            }
            taskManager.removeTask(id);
            sendText(exchange, "Задача успешно удалена", 200);
        }
    }
}
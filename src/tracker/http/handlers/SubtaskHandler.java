package tracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import tracker.managers.taskmanager.TaskManager;
import tracker.model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Optional<Integer> optionalId = getTaskIdFromPath(exchange);

        switch (method) {
            case "GET" -> {
                if (optionalId.isPresent()) {
                    int id = optionalId.get();
                    Subtask subtask = taskManager.getSubtask(id);
                    if (subtask == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(subtask), 200);
                } else {
                    sendText(exchange, gson.toJson(taskManager.getSubtasks()), 200);
                }
            }

            case "POST" -> {
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);

                try {
                    if (optionalId.isPresent()) {
                        subtask.setId(optionalId.get());
                        taskManager.updateSubtask(subtask);
                        sendText(exchange, "Подзадача успешно обновлена", 201);
                    } else {
                        taskManager.addSubtask(subtask);
                        sendText(exchange, "Подзадача успешно создана", 201);
                    }
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                }
            }

            case "DELETE" -> {
                if (optionalId.isPresent()) {
                    int id = optionalId.get();
                    taskManager.removeSubtask(id);
                    sendText(exchange, "Подзадача успешно удалена", 200);
                }
            }
        }
    }
}
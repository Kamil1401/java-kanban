package tracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import tracker.managers.taskmanager.TaskManager;

import java.io.IOException;

public class PrioritizedTasksHandler extends BaseHttpHandler {

    public PrioritizedTasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
    }
}
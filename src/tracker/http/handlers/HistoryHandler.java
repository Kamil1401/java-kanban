package tracker.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import tracker.managers.taskmanager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
    }
}
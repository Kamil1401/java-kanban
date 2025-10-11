package tracker.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.http.handlers.SubtaskHandler;
import tracker.http.httpserver.HttpTaskServer;
import tracker.managers.taskmanager.InMemoryTaskManager;
import tracker.managers.taskmanager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskManagerPrioritizedTasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    public HttpTaskManagerPrioritizedTasksTest() {
    }


    @BeforeEach
    public void beforeEach() throws IOException {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.startServer();
    }

    @AfterEach
    public void afterEach() {
        taskServer.stopServer();
    }

    @Test
    public void getPrioritizedTasks_returnPrioritizedTasksList_alongThePathPrioritized() throws
            IOException, InterruptedException {
        Task task1 = new Task("Task1", "Desc1",
                LocalDateTime.of(2025, 10, 10, 8, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Task2", "Desc2",
                LocalDateTime.of(2025, 10, 10, 8, 35), Duration.ofMinutes(30));
        manager.addTask(task1);
        manager.addTask(task2);

        Epic epic = new Epic("Epic1", "Desc1");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask1", "Desc1", epic.getId(),
                LocalDateTime.of(2025, 10, 10, 9, 10), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Subtask2", "Desc2", epic.getId(),
                LocalDateTime.of(2025, 10, 10, 10, 0), Duration.ofMinutes(30));
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        URI url = URI.create("http://localhost:8080/prioritized");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
    }
}

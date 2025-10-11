package tracker.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.http.handlers.TaskHandler;
import tracker.http.httpserver.HttpTaskServer;
import tracker.managers.taskmanager.InMemoryTaskManager;
import tracker.managers.taskmanager.TaskManager;
import tracker.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerTasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = TaskHandler.getGson();

    public HttpTaskManagerTasksTest() {
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
    public void addTask_taskShouldBeAddedToTheMap_pathAdditionTasks() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Desc1",
                LocalDateTime.of(2025, 10, 10, 8, 0), Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = manager.getTasks();

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals("Task1", tasks.getFirst().getName());
    }

    @Test
    public void getTask_returnTask_ifIdIsPresent() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Desc1",
                LocalDateTime.of(2025, 10, 10, 8, 0), Duration.ofMinutes(30));
        manager.addTask(task);
        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void removeTask_theTaskShouldBeDeleted_ifIdIsPresent() throws IOException, InterruptedException {
        Task task = new Task("Task1", "Desc1",
                LocalDateTime.of(2025, 10, 10, 8, 0), Duration.ofMinutes(30));
        manager.addTask(task);
        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(manager.getTasks().isEmpty());
    }
}
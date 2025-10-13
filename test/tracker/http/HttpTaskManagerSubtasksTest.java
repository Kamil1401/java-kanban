package tracker.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.http.handlers.SubtaskHandler;
import tracker.http.handlers.adapters.DurationAdapter;
import tracker.http.handlers.adapters.LocalDateTimeAdapter;
import tracker.http.httpserver.HttpTaskServer;
import tracker.managers.taskmanager.InMemoryTaskManager;
import tracker.managers.taskmanager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerSubtasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskManagerSubtasksTest() {
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
    public void addSubtask_subtaskShouldBeAddedToTheMap_pathAdditionSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Desc1");
        manager.addEpic(epic);
        Subtask task = new Subtask("Subtask1", "Desc1", epic.getId(),
                LocalDateTime.of(2025, 10, 10, 8, 0), Duration.ofMinutes(30));

        String subtaskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = manager.getSubtasks();
        System.out.println(response.body());

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals(manager.getSubtasksForEpic(epic).getFirst().getId(), subtasks.getFirst().getId());
        Assertions.assertEquals("Subtask1", subtasks.getFirst().getName());
    }

    @Test
    public void getSubtask_returnSubtask_ifIdIsPresent() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Desc1");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Desc1", epic.getId(),
                LocalDateTime.of(2025, 10, 10, 8, 0), Duration.ofMinutes(30));
        manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void removeSubtask_theSubtaskShouldBeDeleted_ifIdIsPresent() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Desc1");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Desc1", epic.getId(),
                LocalDateTime.of(2025, 10, 10, 8, 0), Duration.ofMinutes(30));
        manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(manager.getSubtasks().isEmpty());
        Assertions.assertTrue(epic.getSubtaskIds().isEmpty());
    }
}

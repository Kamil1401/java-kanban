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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskManagerEpicsTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = SubtaskHandler.getGson();

    public HttpTaskManagerEpicsTest() {
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
    public void addEpic_epicShouldBeAddedToTheMap_pathAdditionEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Desc1");

        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        List<Epic> epics = manager.getEpics();

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertNotNull(epics);
        Assertions.assertEquals("Epic1", epics.getFirst().getName());
    }

    @Test
    public void getEpic_returnEpic_ifIdIsPresent() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Desc1");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Desc1", epic.getId(),
                LocalDateTime.of(2025, 10, 10, 8, 0), Duration.ofMinutes(30));
        manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/epics" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void getSubtasksForEpic_returnSubtasksOfEpic_ifIdIsPresent() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Desc1");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Desc1", epic.getId(),
                LocalDateTime.of(2025, 10, 10, 8, 0), Duration.ofMinutes(30));
        manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void removeEpic_theEpicShouldBeDeleted_ifIdIsPresent() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Desc1");
        manager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertTrue(manager.getEpics().isEmpty());
    }
}


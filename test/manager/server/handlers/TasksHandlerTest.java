package manager.server.handlers;

import com.google.gson.*;
import exceptions.TimeIntersectionException;

import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import manager.server.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TasksHandlerTest {
    protected Subtask createSubtask(int epicId) {
        return new Subtask("name1", "description1",epicId,"2024-01-01 18:13","20");
    }
    protected Epic createEpic() {
        return new Epic("name1", "description1");
    }
    protected Task createTask() {
        return new Task("name1", "description1","2024-01-01 16:13","20");
    }

    TaskManager manager;
    HttpClient client;
    HttpTaskServer taskServer;
    HttpRequest request;
    HttpResponse response;
    Gson gson;
    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        client = HttpClient.newHttpClient();
        taskServer = new HttpTaskServer(manager);
        gson = Managers.getGson();
        taskServer.start();
    }
    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void getTasks() throws IOException, TimeIntersectionException, InterruptedException {
        Task task = createTask();
        manager.addTask(task);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(),200);
    }
    @Test
    void getTasksById() throws IOException, TimeIntersectionException, InterruptedException {
        Task task = createTask();
        int id = manager.addTask(task);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/"+id))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"не получили задачу");
        HttpRequest request1 = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/tasks/" + (++id)))
                .build();
        HttpResponse response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,response1.statusCode(), "получили не существующую задачу");
    }
    @Test
    void postCreateTask() throws IOException, TimeIntersectionException, InterruptedException {
        Task task = createTask();
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode(),"не создалась таска");
        Task task1 = createTask();
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        HttpResponse.BodyHandler<String> handler1 = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler1);
        assertEquals(406, response.statusCode(), "создали таску с пересечением по времени");
    }
    @Test
    void postUpdateTask() throws IOException, TimeIntersectionException, InterruptedException {
        Task task = createTask();
        task.setStatusTask(TaskStatus.IN_PROGRESS);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode(),"не создалась таска");
        Task task1 = createTask();
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        HttpResponse.BodyHandler<String> handler1 = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler1);
        assertEquals(406, response.statusCode(), "создали таску с пересечением по времени");

    }
    @Test
    void delTask() throws IOException, TimeIntersectionException, InterruptedException {
        Task task = createTask();
        int id = manager.addTask(task);
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/tasks/"+id))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());
        HttpRequest request1 = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/tasks/" + (++id)))
                .build();
        HttpResponse response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,response1.statusCode());
    }
    @Test
    void getSubtasks() throws IOException, TimeIntersectionException, InterruptedException {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic.getId());
        manager.addTask(subtask);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(),200);
    }
    @Test
    void getSubtaskById() throws IOException, TimeIntersectionException, InterruptedException {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic.getId());
        int id = manager.addSubtask(subtask);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/"+id))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"не получили задачу");
        HttpRequest request1 = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/subtasks/" + (++id)))
                .build();
        HttpResponse response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,response1.statusCode(), "получили не существующую задачу");
    }
    @Test
    void postCreateSubtask() throws IOException, TimeIntersectionException, InterruptedException {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic.getId());
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode(),"не создалась таска");
        Epic epic1 = createEpic();
        manager.addEpic(epic1);
        Subtask subtask1 = createSubtask(epic1.getId());
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        HttpResponse.BodyHandler<String> handler1 = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler1);
        assertEquals(406, response.statusCode(), "создали таску с пересечением по времени");
    }
    @Test
    void postUpdateSubtask() throws IOException, TimeIntersectionException, InterruptedException {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic.getId());
        subtask.setStatusTask(TaskStatus.IN_PROGRESS);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertEquals(201, response.statusCode(),"не создалась таска");
        Epic epic1 = createEpic();
        manager.addEpic(epic1);
        Subtask subtask1 = createSubtask(epic1.getId());
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        HttpResponse.BodyHandler<String> handler1 = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler1);
        assertEquals(406, response.statusCode(), "создали таску с пересечением по времени");
    }@Test
    void delSubtask() throws IOException, TimeIntersectionException, InterruptedException {
        Epic epic = createEpic();
        manager.addEpic(epic);
        Subtask subtask = createSubtask(epic.getId());
        int id = manager.addSubtask(subtask);
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/subtasks/"+id))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());
        HttpRequest request1 = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/subtasks/" + (++id)))
                .build();
        HttpResponse response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,response1.statusCode());
    }@Test
    void getEpics() throws IOException, TimeIntersectionException, InterruptedException {
        Epic epic = createEpic();
        manager.addEpic(epic);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(response.statusCode(),200);
    }@Test
    void getEpicById() throws IOException, TimeIntersectionException, InterruptedException {
        Epic epic = createEpic();
        int id = manager.addEpic(epic);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/"+id))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"не получили задачу");
        HttpRequest request1 = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/" + (++id)))
                .build();
        HttpResponse response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,response1.statusCode(), "получили не существующую задачу");
    }@Test
    void getEpicSubtaskById() throws IOException, TimeIntersectionException, InterruptedException {
        Epic epic = createEpic();
        int id = manager.addEpic(epic);
        Subtask subtask = createSubtask(epic.getId());
        manager.addSubtask(subtask);
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/"+id+"/subtask"))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"не получили задачу");
        HttpRequest request1 = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/epics/" + (++id)+"/subtask"))
                .build();
        HttpResponse response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,response1.statusCode(), "получили не существующую задачу");
    }
    @Test
    void delEpic() throws IOException, TimeIntersectionException, InterruptedException {
        Epic epic = createEpic();
        int id = manager.addEpic(epic);
        Subtask subtask = createSubtask(epic.getId());
        manager.addSubtask(subtask);
        request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/epics/"+id))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());
        HttpRequest request1 = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/epics/" + (++id)))
                .build();
        HttpResponse response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(404,response1.statusCode());
    }
    @Test
    void getHistory() throws IOException, TimeIntersectionException, InterruptedException {
        Task task1 = createTask();
        Epic epic1 = createEpic();
        manager.getTaskById(manager.addTask(task1));
        manager.getEpicById(manager.addEpic(epic1));
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/history"))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());
    }
    @Test
    void getPrioritizedTasks() throws IOException, TimeIntersectionException, InterruptedException {
        Task task1 = createTask();
        Epic epic1 = createEpic();
        manager.getTaskById(manager.addTask(task1));
        manager.getEpicById(manager.addEpic(epic1));
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode());
    }
}
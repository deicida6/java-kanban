package manager.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.Managers;
import manager.TaskManager;
import exceptions.*;
import task.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TasksHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = Managers.getGson();
    }
    SendResponse sendResponse = new SendResponse();
    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case ("GET"):
                switch (pathParts[1]) {
                    case ("tasks"):
                        if (pathParts.length == 3) {
                            return Endpoint.GET_TASK_BY_ID;
                        } else {
                            return Endpoint.GET_TASKS;
                        }
                    case ("subtasks"):
                        if (pathParts.length == 3) {
                            return Endpoint.GET_SUBTASK_BY_ID;
                        } else {
                            return Endpoint.GET_SUBTASKS;
                        }
                    case ("epics"):
                        if (pathParts.length == 4) {
                            return Endpoint.GET_EPIC_SUBTASKS_BY_ID;
                        } else if (pathParts.length == 3){
                            return Endpoint.GET_EPIC_BY_ID;
                        } else {
                            return Endpoint.GET_EPICS;
                        }
                    case ("history"):
                        if (pathParts.length == 2) {
                            return Endpoint.GET_HISTORY;
                        } else {
                            return Endpoint.NOT_FOUND;
                        }
                    case ("prioritized"):
                        if (pathParts.length == 2) {
                            return Endpoint.GET_PRIORITIZED_TASKS;
                        } else {
                            return Endpoint.NOT_FOUND;
                        }
                    default:
                        return Endpoint.NOT_FOUND;
                }
            case ("POST"):
                switch (pathParts[1]) {
                    case ("tasks"):
                        if (pathParts.length == 3) {
                            return Endpoint.POST_TASK_UPDATE_BY_ID;
                        } else {
                            return Endpoint.POST_TASK_CREATE;
                        }
                    case ("subtasks"):
                        if (pathParts.length == 3) {
                            return Endpoint.POST_SUBTASK_UPDATE_BY_ID;
                        } else {
                            return Endpoint.POST_SUBTASK_CREATE;
                        }
                    case ("epics"):
                        return Endpoint.POST_EPIC_CREATE;
                    default:
                        return Endpoint.NOT_FOUND;
                }
            case ("DELETE"):
                switch (pathParts[1]) {
                    case ("tasks"):
                        return Endpoint.DELETE_TASK_BY_ID;
                    case ("subtasks"):
                        return Endpoint.DELETE_SUBTASK_BY_ID;
                    case ("epics"):
                        return Endpoint.DELETE_EPIC_BY_ID;
                    default:
                        return Endpoint.NOT_FOUND;
                }
            default:
                return Endpoint.NOT_FOUND;
        }
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException, NumberFormatException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes());
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        switch (endpoint) {
            case GET_TASKS:
                sendResponse.send(exchange, 200, (gson.toJson(manager.getAllTasks())));
                break;
            case GET_TASK_BY_ID:
                try{
                    Task task = manager.getTaskById(Integer.parseInt(pathParts[2]));
                    sendResponse.send(exchange, 200,gson.toJson(task));
                    break;
                } catch (NullPointerException e) {
                    sendResponse.send(exchange, 404, "Такой задачи нет");
                    break;
                }
            case POST_TASK_CREATE:
                try {
                    int id = manager.addTask(gson.fromJson(body, Task.class));
                    sendResponse.send(exchange, 201, gson.toJson("создана Таска, ее id: " + id));
                } catch (TimeIntersectionException | NullPointerException e) {
                    sendResponse.send(exchange, 406, e.getMessage());
                }
                break;
            case POST_TASK_UPDATE_BY_ID:
                try {
                    manager.updateTask(gson.fromJson(body, Task.class));
                    sendResponse.send(exchange, 201, "Таска c id: " + Integer.parseInt(pathParts[2]) + " обновлена");
                } catch (TimeIntersectionException e) {
                    sendResponse.send(exchange, 406, e.getMessage());
                }
                break;
            case DELETE_TASK_BY_ID:
                try {
                    manager.getTaskById(Integer.parseInt(pathParts[2]));
                    manager.removeTaskById(Integer.parseInt(pathParts[2]));
                    sendResponse.send(exchange, 200, "Задача с id: " + Integer.parseInt(pathParts[2]) + " удалена");
                } catch (RuntimeException e) {
                    sendResponse.send(exchange, 404, "Задача не найдена");
                }
                break;
            case GET_SUBTASKS:
                sendResponse.send(exchange, 200, gson.toJson(manager.getAllSubtasks()));
                break;
            case GET_SUBTASK_BY_ID:
                try{
                    Subtask subtask = manager.getSubtaskById(Integer.parseInt(pathParts[2]));
                    sendResponse.send(exchange, 200,gson.toJson(subtask));
                    break;
                } catch (NullPointerException e) {
                    sendResponse.send(exchange, 404, "Такой задачи нет");
                    break;
                }
            case POST_SUBTASK_CREATE:
                try {
                    int id = manager.addSubtask(gson.fromJson(body, Subtask.class));
                    sendResponse.send(exchange, 201, gson.toJson("создана Сабтаска, ее id: " + id));
                } catch (TimeIntersectionException | NullPointerException e) {
                    sendResponse.send(exchange, 406, e.getMessage());
                }
                break;
            case POST_SUBTASK_UPDATE_BY_ID:
                try {
                    manager.updateSubtask(gson.fromJson(body, Subtask.class));
                    sendResponse.send(exchange, 201, "Сабтаска c id: " + Integer.parseInt(pathParts[2]) + " обновлена");
                } catch (TimeIntersectionException e) {
                    sendResponse.send(exchange, 406, e.getMessage());
                }
                break;
            case DELETE_SUBTASK_BY_ID:
                try {
                    manager.getSubtaskById(Integer.parseInt(pathParts[2]));
                    manager.removeSubtaskById(Integer.parseInt(pathParts[2]));
                    sendResponse.send(exchange, 200, "Задача с id: " + Integer.parseInt(pathParts[2]) + " удалена");
                } catch (RuntimeException e) {
                    sendResponse.send(exchange, 404, "Задача не найдена");
                }
                break;
            case GET_EPICS:
                sendResponse.send(exchange, 200, gson.toJson(manager.getAllEpic()));
                break;
            case GET_EPIC_BY_ID:
                try{
                    Epic epic = manager.getEpicById(Integer.parseInt(pathParts[2]));
                    sendResponse.send(exchange, 200,gson.toJson(epic));
                    break;
                } catch (NullPointerException e) {
                    sendResponse.send(exchange, 404, "Такой задачи нет");
                    break;
                }
            case GET_EPIC_SUBTASKS_BY_ID:
                try{
                    Epic epic = manager.getEpicById(Integer.parseInt(pathParts[2]));
                    List<Subtask> listSubtasks = manager.getAllSubtasksOfEpic(epic);
                    sendResponse.send(exchange, 200,gson.toJson(listSubtasks));
                    break;
                } catch (NullPointerException e) {
                    sendResponse.send(exchange, 404, "Такой задачи нет, либо у задачи нет подзадач");
                    break;
                }
            case POST_EPIC_CREATE:
                try {
                    int id = manager.addEpic(gson.fromJson(body, Epic.class));
                    sendResponse.send(exchange, 201, gson.toJson("создан Эпик, его id: " + id));
                } catch (TimeIntersectionException | NullPointerException e) {
                    sendResponse.send(exchange, 406, e.getMessage());
                }
                break;
            case DELETE_EPIC_BY_ID:
                try {
                    manager.getEpicById(Integer.parseInt(pathParts[2]));
                    manager.removeEpicById(Integer.parseInt(pathParts[2]));
                    sendResponse.send(exchange, 200, "Эпик с id: " + Integer.parseInt(pathParts[2]) + " удалена");
                } catch (RuntimeException e) {
                    sendResponse.send(exchange, 404, "Задача не найдена");
                }
                break;
            case GET_HISTORY:
                sendResponse.send(exchange, 200, gson.toJson(manager.getHistory()));
                break;
            case GET_PRIORITIZED_TASKS:
                sendResponse.send(exchange, 200, gson.toJson(manager.getPrioritizedTasks()));
                break;
            case NOT_FOUND:
                sendResponse.send(exchange, 500, "команда не распознана");
                break;
        }
    }
}

package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int taskId;
    private String nameTask;
    private String descriptionTask;
    private TaskStatus statusTask;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String nameTask, String descriptionTask) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.statusTask = TaskStatus.NEW;
    }

    public Task(String nameTask, String descriptionTask, String startTime, String duration) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.statusTask = TaskStatus.NEW;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.startTime = LocalDateTime.parse(startTime,formatter);
        this.duration = Duration.ofMinutes(Long.parseLong(duration));
    }
    public Task(String nameTask, String descriptionTask, String duration) {
        this.nameTask = nameTask;
        this.descriptionTask = descriptionTask;
        this.statusTask = TaskStatus.NEW;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ofMinutes(Long.parseLong(duration));
    }

    public int getId() {
        return taskId;
    }

    public void setId(int id) {
        this.taskId = id;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getDescriptionTask() {
        return descriptionTask;
    }

    public void setDescriptionTask(String descriptionTask) {
        this.descriptionTask = descriptionTask;
    }

    public TaskStatus getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(TaskStatus statusTask) {
        this.statusTask = statusTask;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(nameTask, task.nameTask) &&
                Objects.equals(descriptionTask, task.descriptionTask) && Objects.equals(statusTask, task.statusTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, nameTask, descriptionTask, statusTask);
    }

    @Override
    public String toString() {
        if(getStartTime() == null){
            return "\nTask.Task{" +
                    "id=" + taskId +
                    ", Task.TaskType = '" + TaskType.TASK + '\'' +
                    ", nameTask='" + nameTask + '\'' +
                    ", descriptionTask='" + descriptionTask + '\'' +
                    ", statusTask='" + statusTask + '\'' +
                    ", startTime=" + "n/a" +
                    ", duration=" + "n/a" +
                    ", endTime=" + "n/a" +
                    '}';
        } else {
            return "\nTask.Task{" +
                    "id=" + taskId +
                    ", Task.TaskType = '" + TaskType.TASK + '\'' +
                    ", nameTask='" + nameTask + '\'' +
                    ", descriptionTask='" + descriptionTask + '\'' +
                    ", statusTask='" + statusTask + '\'' +
                    ", startTime=" + startTime +
                    ", duration=" + duration +
                    ", endTime=" + getEndTime() +
                    '}';
        }
    }
}

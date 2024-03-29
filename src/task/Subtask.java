package task;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String nameTask, String descriptionTask, int epicId, String startTime, String duration) {
        super(nameTask, descriptionTask, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String nameTask, String descriptionTask, int epicId) {
        super(nameTask, descriptionTask);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        if(getStartTime() == null){
            return "\nTask.Subtask{" +
                    "id = '" + getId() + '\'' +
                    ", Task.TaskType = '" + TaskType.SUBTASK + '\'' +
                    ", NameTask = '" + getNameTask() + '\'' +
                    ", StatusTask = '" + getStatusTask() + '\'' +
                    ", DescriptionTask = '" + getDescriptionTask() + '\'' +
                    ", epicId = " + epicId +
                    ", startTime=" + "n/a" +
                    ", duration=" + "n/a" +
                    ", endTime=" + "n/a" +
                    "}";
        } else {
            return "\nTask.Subtask{" +
                    "id = '" + getId() + '\'' +
                    ", Task.TaskType = '" + TaskType.SUBTASK + '\'' +
                    ", NameTask = '" + getNameTask() + '\'' +
                    ", StatusTask = '" + getStatusTask() + '\'' +
                    ", DescriptionTask = '" + getDescriptionTask() + '\'' +
                    ", epicId = " + epicId +
                    ", startTime=" + getStartTime() +
                    ", duration=" + getDuration() +
                    ", endTime=" + getEndTime() +
                    "}";
        }

    }
}

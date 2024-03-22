package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String nameTask, String descriptionTask) {
        super(nameTask, descriptionTask);
        this.subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
   public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        if(getStartTime() == null){
            return "\nTask.Epic{" +
                    "id = '" + getId() + '\'' +
                    ", Task.TaskType = '" + TaskType.EPIC + '\'' +
                    ", Name = '" + getNameTask() + '\'' +
                    ", Status = '" + getStatusTask() + '\'' +
                    ", Description = '" + getDescriptionTask() + '\'' +
                    ", subtaskIds= " + subtaskIds +
                    ", startTime=" + "n/a" +
                    ", duration=" + "n/a" +
                    ", endTime=" + "n/a" +
                    '}';
        } else {
            return "\nTask.Epic{" +
                    "id = '" + getId() + '\'' +
                    ", Task.TaskType = '" + TaskType.EPIC + '\'' +
                    ", Name = '" + getNameTask() + '\'' +
                    ", Status = '" + getStatusTask() + '\'' +
                    ", Description = '" + getDescriptionTask() + '\'' +
                    ", subtaskIds= " + subtaskIds +
                    ", startTime=" + getStartTime() +
                    ", duration=" + getDuration() +
                    ", endTime=" + getEndTime() +
                    '}';
        }
    }
}

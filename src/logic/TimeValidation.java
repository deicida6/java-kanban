package logic;

import task.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TimeValidation {
    public Task validate(Task task, Collection<? extends Task> tasksTreeSet) {
        if (task.getStartTime() != null) {
            LocalDateTime taskStartTime = task.getStartTime();
            LocalDateTime taskEndTime = task.getEndTime();
            List<? extends Task> correctionList1 = tasksTreeSet.stream()
                    .filter(taskFromTaskSet ->
                            (       (taskFromTaskSet.getStartTime() == null)
                                    ||
                                    (taskFromTaskSet.getStartTime().isAfter(taskEndTime) &&
                                            taskFromTaskSet.getEndTime().isAfter(taskEndTime))
                                    ||
                                    taskFromTaskSet.getStartTime().isBefore(taskStartTime) &&
                                            taskFromTaskSet.getEndTime().isBefore(taskStartTime))
                                    ||
                                    (taskFromTaskSet.getStartTime().isEqual(taskEndTime))
                                    ||
                                    (taskFromTaskSet.getEndTime().isEqual(taskStartTime)))
                    .collect(Collectors.toList());

            if (correctionList1.size() == tasksTreeSet.size()) {
                return task;
            } else return null;
        }
        return task;
    }
}
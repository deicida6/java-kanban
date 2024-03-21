package logic;

import task.*;

import java.util.Collection;
import java.util.Map;

public class EpicStatusUpdater {

    public void verifyStatus(Epic epic, Map<Integer,Subtask> subtaskHashMap) {

        Collection<Integer> listOfSubTasks = epic.getSubtaskIds();
        int counterSameStatus = 0;
        TaskStatus firstSubtaskStatus = null;

        for (Integer idSubtask : listOfSubTasks) {
            Subtask subtask = subtaskHashMap.get(idSubtask);
            firstSubtaskStatus = subtask.getStatusTask();

            for (Integer idSecondSubtask : listOfSubTasks) {
                subtask = subtaskHashMap.get(idSecondSubtask);

                if (firstSubtaskStatus != subtask.getStatusTask()) {
                    epic.setStatusTask(TaskStatus.IN_PROGRESS);
                    break;
                } else {
                    counterSameStatus++;
                }
            }
            break;
        }
        if (counterSameStatus == listOfSubTasks.size()) {
            epic.setStatusTask(firstSubtaskStatus);
        }
    }
}
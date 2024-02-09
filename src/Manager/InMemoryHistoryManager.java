package Manager;

import Task.*;
import java.util.*;


public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList historyTasksList = new CustomLinkedList();


    @Override
    public void add(Task task) {
        historyTasksList.linkLast(task);
    }

    @Override
    public void remove(int id) {
        historyTasksList.removeNode(historyTasksList.getNode(id));
    }

    @Override
    public List<Task> getHistory() {
        return historyTasksList.getTasks();
    }

    private class CustomLinkedList {
        private final Map<Integer, Node> historyTasksMap = new HashMap<>();
        private Node head;
        private Node tail;

        private void linkLast(Task task) {
            Node element = new Node();
            element.setTask(task);
            if (historyTasksMap.containsKey(task.getId())) {
                removeNode(historyTasksMap.get(task.getId()));
            }
            if (head == null) {
                tail = element;
                head = element;
                element.setNext(null);
                element.setPrev(null);
            } else {
                element.setPrev(tail);
                element.setNext(null);
                tail.setNext(element);
                tail = element;
            }
            historyTasksMap.put(task.getId(), element);
        }

        private List<Task> getTasks() {
            List<Task> taskList = new ArrayList<>();
            Node element = head;
            while (element != null) {
                taskList.add(element.getTask());
                element = element.getNext();
            }
            return taskList;
        }

        private void removeNode(Node node) {
            if (node != null) {
                historyTasksMap.remove(node.getTask().getId());
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }
                if (prev != null) {
                    prev.setNext(next);
                }
                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }


        private Node getNode(int id) {
            return historyTasksMap.get(id);
        }
    }
    public static class Node {
        private Task task;
        private Node next;
        private Node prev;

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
}
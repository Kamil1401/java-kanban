package tracker.managers.historymanager;

import tracker.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private Map<Integer, Node> nodes;

    public InMemoryHistoryManager() {
        this.head = null;
        this.tail = null;
        nodes = new HashMap<>();
    }

    @Override
    public void add(Task instance) {
        if (nodes.containsKey(instance.getId())) {
            remove(instance.getId());
        }
        Task copyInstance = instance.copy();
        linkLast(copyInstance);
        nodes.put(copyInstance.getId(), tail);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(Integer id) {
        removeNode(nodes.get(id));
        nodes.remove(id);
    }

    @Override
    public Task getLast() {
        return tail.value;
    }

    private void linkLast(Task task) {
        Node node = new Node(task, tail, null);
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        for (Node node : nodes.values()) {
            tasks.add(node.value);
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.prev == null) {
            head = node.next;
        } else if (node.next == null) {
            tail = node.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private static class Node {
        private Task value;
        private Node prev;
        private Node next;

        public Node(Task value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
}
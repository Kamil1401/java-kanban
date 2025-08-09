package tracker;

import tracker.managers.Managers;
import tracker.managers.historymanager.InMemoryHistoryManager;
import tracker.managers.taskmanager.TaskManager;
import tracker.model.Epic;
import tracker.model.Subtask;
import tracker.model.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task dinner = new Task("Ужин", "Приготовить ужин");
        taskManager.addTask(dinner);
        System.out.println(dinner.getId());
        Epic repair = new Epic("Ремонт", "Начать ремонт в квартире");
        taskManager.addEpic(repair);
        Subtask hiring = new Subtask("Нанять бригаду", "Позвонить мастерам", repair.getId());
        Subtask materials = new Subtask("Съездить в Добрострой", "Купить материалы", repair.getId());
        taskManager.addSubtask(hiring);
        taskManager.addSubtask(materials);
        Epic sale = new Epic("Продажа", "Продать машины");
        taskManager.addEpic(sale);
        Subtask advertisement = new Subtask("Реклама", "Разместить на авито", sale.getId());
        Subtask service = new Subtask("Сервис", "Отвезти машину в сервис", sale.getId());
        taskManager.addSubtask(advertisement);
        taskManager.addSubtask(service);

        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        taskManager.getTask(dinner.getId());
        dinner.setId(4125748);
        taskManager.getEpic(repair.getId());
        repair.setId(94715);
        taskManager.getSubtask(hiring.getId());
        hiring.setId(64758);
        System.out.println(taskManager.getHistory());

        Epic epicCopy = sale.copy();
        for (Integer sI : epicCopy.getSubtaskIds()) {
            System.out.println(taskManager.getSubtask(sI));
        }
    }
}
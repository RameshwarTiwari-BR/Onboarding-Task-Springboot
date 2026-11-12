package com.learning.springbootonboarding.day12.service;

import com.learning.springbootonboarding.day12.model.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    // Thread-safe in-memory map to store tasks instead of a physical DB - Day 12
    private final Map<Long, Task> taskStorage = new ConcurrentHashMap<>();

    // Thread-safe ID auto-incrementer, Using this because if 2 user request cant create same id, This Provide getAndIncrement
    private final AtomicLong idCounter = new AtomicLong(1);

    // 1. Create a task
    public Task createTask(String name) {
        Long id = idCounter.getAndIncrement();
        Task newTask = new Task(id, name, false, LocalDateTime.now());
        taskStorage.put(id, newTask);
        return newTask;
    }

    // 2. List all tasks
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskStorage.values());
    }

    // 3. Get task by ID
    public Optional<Task> getTaskById(Long id) {
        return Optional.ofNullable(taskStorage.get(id));
    }

//    4. Update task status by ID
    public Optional<Task> updateTaskStatus(Long id, boolean newStatus){
        Task task = taskStorage.get(id);
        if(task!=null){
            task.setStatus(newStatus);
            task.setTimestamp(LocalDateTime.now());
            return Optional.of(task);
        }
        return Optional.empty();
    }

//    Delete Task By ID
    public Optional<Task> deleteTask(Long id){
        Task task = taskStorage.get(id);
        if(task!=null){
            taskStorage.remove(id);
            return Optional.of(task);
        }
        return  Optional.empty();
    }

}

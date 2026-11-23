package com.learning.springbootonboarding.day12.service;

import com.learning.springbootonboarding.day12.dto.TaskRequest;
import com.learning.springbootonboarding.day12.dto.TaskResponse;
import com.learning.springbootonboarding.day12.model.Project;
import com.learning.springbootonboarding.day12.model.Task;
import com.learning.springbootonboarding.day12.model.TaskStatus;
import com.learning.springbootonboarding.day12.model.User;
import com.learning.springbootonboarding.day12.repository.ProjectRepository;
import com.learning.springbootonboarding.day12.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserService currentUserService;


    @Transactional
    public TaskResponse createTask(UUID projectId, TaskRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

        Task task = new Task(
                request.title(),
                request.description(),
                request.status(),
                request.priority(),
                project,
                request.dueDate()
        );
        task.setCreatedBy(currentUser.getId());

        if(request.assignedTo()==null){
            task.setAssignedTo(currentUser.getId());
        }

        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public List<TaskResponse> getTasksByProject(UUID projectId, TaskStatus status) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found with id: " + projectId);
        }

        List<Task> tasks = (status != null)
                ? taskRepository.findByProjectIdAndStatus(projectId, status)
                : taskRepository.findByProjectId(projectId);

        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TaskResponse updateTask(UUID taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + taskId));

        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        task.setDueDate(request.dueDate());

        // No explicit save() needed inside @Transactional; Hibernate auto-flushes dirty changes
        return toResponse(task);
    }

    @Transactional
    public void deleteTask(UUID taskId) {
        User currentUser = currentUserService.getCurrentUser();

        if (!taskRepository.existsById(taskId)) {
            throw new EntityNotFoundException("Task not found with id: " + taskId);
        }
//        find task
        Optional<Task> tasks = taskRepository.findById(taskId);

        UUID currentUserId = currentUser.getId();
        UUID taskCreatorId = tasks.get().getCreatedBy(); // Use .getCreatedBy().getId() if mapped as @ManyToOne User

        if (!Objects.equals(currentUserId, taskCreatorId)) {
            log.warn("Unauthorized delete attempt on task {}: currentUserId={} does not match createdBy={}",
                    tasks.get().getId(), currentUserId, taskCreatorId);
            throw new IllegalArgumentException("You can not delete this task");
        }

        taskRepository.deleteById(taskId);
    }

    public Page<TaskResponse> getAllTask(int page, int size, String sortBy, String sortDir, TaskStatus status) {

        User currentUser = currentUserService.getCurrentUser();


        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(safePage, safeSize, sort);

        Page<Task> taskPage = (status != null)
                ? taskRepository.findAllByStatusAndCreatedBy(status, currentUser.getId(), pageable)
                : taskRepository.findAllByCreatedBy(currentUser.getId(), pageable);


        return taskPage.map(this::toResponse);
    }


    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getProject().getId(),
                task.getCreatedAt(),
                task.getDueDate(),
                task.getAssignedTo(),
                task.getCreatedBy()
        );
    }
}

package com.learning.springbootonboarding.day12.repository;

import com.learning.springbootonboarding.day12.model.Task;
import com.learning.springbootonboarding.day12.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Spring Data JPA auto-generates: SELECT * FROM tasks WHERE project_id = ?
    List<Task> findByProjectId(Long projectId);

    // Spring Data JPA auto-generates: SELECT * FROM tasks WHERE project_id = ? AND status = ?
    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    Page<Task> findALlTaskByStatus(TaskStatus status, Pageable pageable);

}

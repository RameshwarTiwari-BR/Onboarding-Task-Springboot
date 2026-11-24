package com.learning.springbootonboarding.weekThreeFourFive.repository;

import com.learning.springbootonboarding.weekThreeFourFive.model.Task;
import com.learning.springbootonboarding.weekThreeFourFive.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByProjectId(UUID projectId);

    List<Task> findByProjectIdAndStatus(UUID projectId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE (t.createdBy = :userId OR t.assignedTo = :userId)")
    Page<Task> findAllUserTasks(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.status = :status AND (t.createdBy = :userId OR t.assignedTo = :userId)")
    Page<Task> findAllUserTasksByStatus(@Param("userId") UUID userId, @Param("status") TaskStatus status, Pageable pageable);
}

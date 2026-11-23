package com.learning.springbootonboarding.weekThreeFourFive.repository;

import com.learning.springbootonboarding.weekThreeFourFive.model.Task;
import com.learning.springbootonboarding.weekThreeFourFive.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByProjectId(UUID projectId);

    List<Task> findByProjectIdAndStatus(UUID projectId, TaskStatus status);

    Page<Task> findAllByStatusAndCreatedBy(TaskStatus status, UUID createdBy, Pageable pageable);

    Page<Task> findAllByCreatedBy(UUID createdBy, Pageable pageable);
}

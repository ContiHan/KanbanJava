package com.uhk.kanban.repository;

import com.uhk.kanban.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByKanbanId(Long kanbanId);
}

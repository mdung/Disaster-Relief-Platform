package com.relief.repository;

import com.relief.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :assigneeId")
    Page<Task> findByAssigneeId(@Param("assigneeId") UUID assigneeId, Pageable pageable);

    Page<Task> findByStatus(String status, Pageable pageable);
    
    // Admin-specific queries
    long countByStatus(String status);
    
    long countByStatusIn(String... statuses);
    
    @Query("SELECT t.status as status, COUNT(t) as count FROM Task t GROUP BY t.status")
    java.util.Map<String, Long> countByStatus();
    
    // Analytics queries
    long countByCreatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
    long countByStatusAndUpdatedAtBetween(String status, java.time.LocalDateTime start, java.time.LocalDateTime end);
    long countByStatusInAndUpdatedAtBetween(java.util.List<String> statuses, java.time.LocalDateTime start, java.time.LocalDateTime end);
    java.util.Map<String, Long> countByStatusAndUpdatedAtBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
    
    // Smart automation queries
    @Query("SELECT t FROM Task t WHERE t.eta < :now AND t.status IN ('assigned', 'picked_up') ORDER BY t.eta ASC")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);
    
    default List<Task> findOverdueTasks() {
        return findOverdueTasks(LocalDateTime.now());
    }
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee = :assignee AND t.status IN :statuses")
    long countByAssigneeAndStatusIn(@Param("assignee") com.relief.entity.User assignee, @Param("statuses") List<String> statuses);
}



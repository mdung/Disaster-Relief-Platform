package com.relief.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dedupe_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DedupeGroup {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // e.g., NEEDS_REQUEST, TASK, USER

    @Column(name = "status", nullable = false, length = 30)
    private String status; // OPEN, MERGED, DISMISSED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "note", length = 1000)
    private String note;
}






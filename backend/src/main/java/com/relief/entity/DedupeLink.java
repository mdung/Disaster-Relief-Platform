package com.relief.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dedupe_links")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DedupeLink {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private DedupeGroup group;

    @Column(name = "entity_id", nullable = false, columnDefinition = "uuid")
    private UUID entityId; // The duplicate entity id (e.g., NeedsRequest id)

    @Column(name = "score")
    private Double score; // similarity score

    @Column(name = "reason", length = 1000)
    private String reason; // e.g., same phone + bbox proximity

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}






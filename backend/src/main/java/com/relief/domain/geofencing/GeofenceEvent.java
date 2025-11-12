package com.relief.domain.geofencing;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

/**
 * Represents an event that occurred within a geofence
 */
@Entity
@Table(name = "geofence_events", indexes = {
    @Index(name = "idx_geofence_event_geom", columnList = "location"),
    @Index(name = "idx_geofence_event_geofence", columnList = "geofence_id"),
    @Index(name = "idx_geofence_event_type", columnList = "event_type"),
    @Index(name = "idx_geofence_event_occurred_at", columnList = "occurred_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeofenceEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geofence_id", nullable = false)
    private Geofence geofence;
    
    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GeofenceEventType eventType;
    
    @Column(name = "location", nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point location;
    
    @Column(name = "entity_type", nullable = false)
    private String entityType; // Type of entity that triggered the event
    
    @Column(name = "entity_id")
    private Long entityId; // ID of the entity that triggered the event
    
    @Column(name = "entity_name")
    private String entityName; // Name or identifier of the entity
    
    @Column(name = "event_data", columnDefinition = "jsonb")
    private String eventData; // Additional event data as JSON
    
    @Column(name = "severity", nullable = false)
    @Enumerated(EnumType.STRING)
    private GeofenceEventSeverity severity;
    
    @Column(name = "confidence_score")
    private Double confidenceScore; // Confidence in the event detection (0-1)
    
    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
    
    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "is_processed", nullable = false)
    private Boolean isProcessed;
    
    @Column(name = "processing_notes")
    private String processingNotes;
    
    @PrePersist
    protected void onCreate() {
        if (isProcessed == null) {
            isProcessed = false;
        }
        if (detectedAt == null) {
            detectedAt = LocalDateTime.now();
        }
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }
}




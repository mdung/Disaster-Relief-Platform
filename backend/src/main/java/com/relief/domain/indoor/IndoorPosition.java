package com.relief.domain.indoor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

/**
 * Represents a position within an indoor map
 */
@Entity
@Table(name = "indoor_positions", indexes = {
    @Index(name = "idx_indoor_position_map", columnList = "indoor_map_id"),
    @Index(name = "idx_indoor_position_entity", columnList = "entity_type, entity_id"),
    @Index(name = "idx_indoor_position_geom", columnList = "position"),
    @Index(name = "idx_indoor_position_timestamp", columnList = "timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndoorPosition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indoor_map_id", nullable = false)
    private IndoorMap indoorMap;
    
    @Column(name = "entity_type", nullable = false)
    private String entityType; // Type of entity (PERSON, VEHICLE, EQUIPMENT, etc.)
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId; // ID of the entity
    
    @Column(name = "entity_name")
    private String entityName; // Name or identifier of the entity
    
    @Column(name = "position", nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point position;
    
    @Column(name = "local_x", nullable = false)
    private Double localX; // Local coordinate X
    
    @Column(name = "local_y", nullable = false)
    private Double localY; // Local coordinate Y
    
    @Column(name = "floor_level", nullable = false)
    private Integer floorLevel; // Floor level
    
    @Column(name = "heading")
    private Double heading; // Heading in degrees (0-360)
    
    @Column(name = "speed")
    private Double speed; // Speed in m/s
    
    @Column(name = "accuracy", nullable = false)
    private Double accuracy; // Position accuracy in meters
    
    @Column(name = "positioning_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PositioningMethod positioningMethod;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "is_valid", nullable = false)
    private Boolean isValid;
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional position data
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isValid == null) {
            isValid = true;
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}




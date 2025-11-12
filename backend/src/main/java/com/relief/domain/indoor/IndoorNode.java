package com.relief.domain.indoor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a navigation node in an indoor map
 */
@Entity
@Table(name = "indoor_nodes", indexes = {
    @Index(name = "idx_indoor_node_map", columnList = "indoor_map_id"),
    @Index(name = "idx_indoor_node_type", columnList = "node_type"),
    @Index(name = "idx_indoor_node_geom", columnList = "position"),
    @Index(name = "idx_indoor_node_accessible", columnList = "is_accessible")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndoorNode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indoor_map_id", nullable = false)
    private IndoorMap indoorMap;
    
    @Column(name = "node_id", nullable = false)
    private String nodeId; // Unique identifier within the map
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "position", nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point position;
    
    @Column(name = "local_x", nullable = false)
    private Double localX; // Local coordinate X
    
    @Column(name = "local_y", nullable = false)
    private Double localY; // Local coordinate Y
    
    @Column(name = "node_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IndoorNodeType nodeType;
    
    @Column(name = "is_accessible", nullable = false)
    private Boolean isAccessible;
    
    @Column(name = "accessibility_features", columnDefinition = "jsonb")
    private String accessibilityFeatures; // JSON array of accessibility features
    
    @Column(name = "capacity")
    private Integer capacity; // Maximum capacity for the node
    
    @Column(name = "current_occupancy")
    private Integer currentOccupancy; // Current number of people
    
    @Column(name = "is_emergency_exit", nullable = false)
    private Boolean isEmergencyExit;
    
    @Column(name = "is_elevator", nullable = false)
    private Boolean isElevator;
    
    @Column(name = "is_stairs", nullable = false)
    private Boolean isStairs;
    
    @Column(name = "floor_level")
    private Integer floorLevel; // Floor level for multi-story buildings
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional node data
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "fromNode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndoorEdge> outgoingEdges;
    
    @OneToMany(mappedBy = "toNode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndoorEdge> incomingEdges;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isAccessible == null) {
            isAccessible = true;
        }
        if (isEmergencyExit == null) {
            isEmergencyExit = false;
        }
        if (isElevator == null) {
            isElevator = false;
        }
        if (isStairs == null) {
            isStairs = false;
        }
        if (currentOccupancy == null) {
            currentOccupancy = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




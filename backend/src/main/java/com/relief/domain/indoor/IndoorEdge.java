package com.relief.domain.indoor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;

/**
 * Represents a connection between two indoor nodes
 */
@Entity
@Table(name = "indoor_edges", indexes = {
    @Index(name = "idx_indoor_edge_map", columnList = "indoor_map_id"),
    @Index(name = "idx_indoor_edge_from", columnList = "from_node_id"),
    @Index(name = "idx_indoor_edge_to", columnList = "to_node_id"),
    @Index(name = "idx_indoor_edge_type", columnList = "edge_type"),
    @Index(name = "idx_indoor_edge_accessible", columnList = "is_accessible")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndoorEdge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indoor_map_id", nullable = false)
    private IndoorMap indoorMap;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_node_id", nullable = false)
    private IndoorNode fromNode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_node_id", nullable = false)
    private IndoorNode toNode;
    
    @Column(name = "edge_id", nullable = false)
    private String edgeId; // Unique identifier within the map
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "path", columnDefinition = "geometry(LineString, 4326)")
    private LineString path; // Optional path geometry
    
    @Column(name = "edge_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IndoorEdgeType edgeType;
    
    @Column(name = "is_accessible", nullable = false)
    private Boolean isAccessible;
    
    @Column(name = "is_bidirectional", nullable = false)
    private Boolean isBidirectional;
    
    @Column(name = "distance", nullable = false)
    private Double distance; // Distance in meters
    
    @Column(name = "width")
    private Double width; // Width in meters
    
    @Column(name = "height")
    private Double height; // Height in meters
    
    @Column(name = "weight", nullable = false)
    private Double weight; // Navigation weight (lower = preferred)
    
    @Column(name = "max_speed")
    private Double maxSpeed; // Maximum speed in m/s
    
    @Column(name = "accessibility_features", columnDefinition = "jsonb")
    private String accessibilityFeatures; // JSON array of accessibility features
    
    @Column(name = "is_emergency_route", nullable = false)
    private Boolean isEmergencyRoute;
    
    @Column(name = "is_restricted", nullable = false)
    private Boolean isRestricted;
    
    @Column(name = "restriction_type")
    private String restrictionType; // Type of restriction
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional edge data
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isAccessible == null) {
            isAccessible = true;
        }
        if (isBidirectional == null) {
            isBidirectional = true;
        }
        if (weight == null) {
            weight = 1.0;
        }
        if (isEmergencyRoute == null) {
            isEmergencyRoute = false;
        }
        if (isRestricted == null) {
            isRestricted = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




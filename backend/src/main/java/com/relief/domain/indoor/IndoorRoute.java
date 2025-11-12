package com.relief.domain.indoor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a navigation route within an indoor map
 */
@Entity
@Table(name = "indoor_routes", indexes = {
    @Index(name = "idx_indoor_route_map", columnList = "indoor_map_id"),
    @Index(name = "idx_indoor_route_from", columnList = "from_node_id"),
    @Index(name = "idx_indoor_route_to", columnList = "to_node_id"),
    @Index(name = "idx_indoor_route_type", columnList = "route_type"),
    @Index(name = "idx_indoor_route_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndoorRoute {
    
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
    
    @Column(name = "route_id", nullable = false)
    private String routeId; // Unique identifier for the route
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "path", nullable = false, columnDefinition = "geometry(LineString, 4326)")
    private LineString path; // Route path geometry
    
    @Column(name = "route_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IndoorRouteType routeType;
    
    @Column(name = "total_distance", nullable = false)
    private Double totalDistance; // Total distance in meters
    
    @Column(name = "estimated_time", nullable = false)
    private Integer estimatedTime; // Estimated time in seconds
    
    @Column(name = "difficulty_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultyLevel;
    
    @Column(name = "is_accessible", nullable = false)
    private Boolean isAccessible;
    
    @Column(name = "is_emergency_route", nullable = false)
    private Boolean isEmergencyRoute;
    
    @Column(name = "is_restricted", nullable = false)
    private Boolean isRestricted;
    
    @Column(name = "access_level")
    private String accessLevel; // Required access level
    
    @Column(name = "waypoints", columnDefinition = "jsonb")
    private String waypoints; // JSON array of waypoint coordinates
    
    @Column(name = "instructions", columnDefinition = "jsonb")
    private String instructions; // JSON array of turn-by-turn instructions
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional route data
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndoorRouteStep> steps;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isAccessible == null) {
            isAccessible = true;
        }
        if (isEmergencyRoute == null) {
            isEmergencyRoute = false;
        }
        if (isRestricted == null) {
            isRestricted = false;
        }
        if (difficultyLevel == null) {
            difficultyLevel = DifficultyLevel.EASY;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




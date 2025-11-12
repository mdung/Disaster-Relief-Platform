package com.relief.domain.indoor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an indoor map for GPS-denied environments
 */
@Entity
@Table(name = "indoor_maps", indexes = {
    @Index(name = "idx_indoor_map_name", columnList = "name"),
    @Index(name = "idx_indoor_map_facility", columnList = "facility_id"),
    @Index(name = "idx_indoor_map_floor", columnList = "floor_number"),
    @Index(name = "idx_indoor_map_active", columnList = "is_active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndoorMap {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "facility_id", nullable = false)
    private Long facilityId;
    
    @Column(name = "facility_name", nullable = false)
    private String facilityName;
    
    @Column(name = "floor_number", nullable = false)
    private Integer floorNumber;
    
    @Column(name = "floor_name")
    private String floorName;
    
    @Column(name = "map_bounds", nullable = false, columnDefinition = "geometry(Geometry, 4326)")
    private Geometry mapBounds;
    
    @Column(name = "map_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private IndoorMapType mapType;
    
    @Column(name = "coordinate_system", nullable = false)
    private String coordinateSystem; // e.g., "local", "utm", "custom"
    
    @Column(name = "scale_factor", nullable = false)
    private Double scaleFactor; // Pixels per meter
    
    @Column(name = "map_image_url")
    private String mapImageUrl;
    
    @Column(name = "map_data", columnDefinition = "jsonb")
    private String mapData; // JSON data for map features
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "indoorMap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndoorNode> nodes;
    
    @OneToMany(mappedBy = "indoorMap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndoorEdge> edges;
    
    @OneToMany(mappedBy = "indoorMap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndoorZone> zones;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (scaleFactor == null) {
            scaleFactor = 1.0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




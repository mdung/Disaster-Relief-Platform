package com.relief.domain.terrain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

/**
 * Represents elevation data points for terrain analysis
 */
@Entity
@Table(name = "elevation_points", indexes = {
    @Index(name = "idx_elevation_geom", columnList = "location"),
    @Index(name = "idx_elevation_source", columnList = "source"),
    @Index(name = "idx_elevation_accuracy", columnList = "accuracy")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElevationPoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "location", nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point location;
    
    @Column(name = "elevation", nullable = false)
    private Double elevation; // in meters
    
    @Column(name = "source", nullable = false)
    @Enumerated(EnumType.STRING)
    private ElevationSource source;
    
    @Column(name = "accuracy")
    private Double accuracy; // in meters
    
    @Column(name = "resolution")
    private Double resolution; // in meters per pixel
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




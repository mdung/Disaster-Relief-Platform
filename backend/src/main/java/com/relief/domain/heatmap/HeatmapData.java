package com.relief.domain.heatmap;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

/**
 * Represents heatmap data points for visualization
 */
@Entity
@Table(name = "heatmap_data", indexes = {
    @Index(name = "idx_heatmap_geom", columnList = "location"),
    @Index(name = "idx_heatmap_type", columnList = "heatmap_type"),
    @Index(name = "idx_heatmap_created_at", columnList = "created_at"),
    @Index(name = "idx_heatmap_intensity", columnList = "intensity")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "location", nullable = false, columnDefinition = "geometry(Point, 4326)")
    private Point location;
    
    @Column(name = "heatmap_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private HeatmapType heatmapType;
    
    @Column(name = "intensity", nullable = false)
    private Double intensity; // 0.0 to 1.0, normalized intensity value
    
    @Column(name = "weight", nullable = false)
    private Double weight; // Weight of the data point
    
    @Column(name = "radius", nullable = false)
    private Double radius; // Influence radius in meters
    
    @Column(name = "category")
    private String category; // Optional category for grouping
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional data as JSON
    
    @Column(name = "source_id")
    private Long sourceId; // ID of the source record (need, task, etc.)
    
    @Column(name = "source_type")
    private String sourceType; // Type of source record
    
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




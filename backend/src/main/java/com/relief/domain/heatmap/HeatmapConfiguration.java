package com.relief.domain.heatmap;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Configuration for heatmap generation and visualization
 */
@Entity
@Table(name = "heatmap_configurations", indexes = {
    @Index(name = "idx_heatmap_config_type", columnList = "heatmap_type"),
    @Index(name = "idx_heatmap_config_active", columnList = "is_active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "heatmap_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private HeatmapType heatmapType;
    
    @Column(name = "color_scheme", nullable = false)
    private String colorScheme; // JSON configuration for colors
    
    @Column(name = "intensity_range_min", nullable = false)
    private Double intensityRangeMin;
    
    @Column(name = "intensity_range_max", nullable = false)
    private Double intensityRangeMax;
    
    @Column(name = "radius_multiplier", nullable = false)
    private Double radiusMultiplier; // Multiplier for point radius
    
    @Column(name = "opacity", nullable = false)
    private Double opacity; // 0.0 to 1.0
    
    @Column(name = "blur_radius", nullable = false)
    private Double blurRadius; // Blur radius in pixels
    
    @Column(name = "gradient_stops", columnDefinition = "jsonb")
    private String gradientStops; // JSON array of gradient stops
    
    @Column(name = "aggregation_method", nullable = false)
    private String aggregationMethod; // SUM, AVG, MAX, MIN, COUNT
    
    @Column(name = "time_window_hours")
    private Integer timeWindowHours; // Time window for data aggregation
    
    @Column(name = "spatial_resolution_meters")
    private Double spatialResolutionMeters; // Spatial resolution for aggregation
    
    @Column(name = "filter_criteria", columnDefinition = "jsonb")
    private String filterCriteria; // JSON filter criteria
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




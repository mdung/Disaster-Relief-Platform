package com.relief.domain.heatmap;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDateTime;

/**
 * Represents a generated heatmap layer for visualization
 */
@Entity
@Table(name = "heatmap_layers", indexes = {
    @Index(name = "idx_heatmap_layer_geom", columnList = "bounds"),
    @Index(name = "idx_heatmap_layer_type", columnList = "heatmap_type"),
    @Index(name = "idx_heatmap_layer_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeatmapLayer {
    
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
    
    @Column(name = "bounds", nullable = false, columnDefinition = "geometry(Polygon, 4326)")
    private Polygon bounds;
    
    @Column(name = "tile_url_template", nullable = false)
    private String tileUrlTemplate; // URL template for map tiles
    
    @Column(name = "min_zoom", nullable = false)
    private Integer minZoom;
    
    @Column(name = "max_zoom", nullable = false)
    private Integer maxZoom;
    
    @Column(name = "tile_size", nullable = false)
    private Integer tileSize; // Tile size in pixels
    
    @Column(name = "data_points_count", nullable = false)
    private Long dataPointsCount;
    
    @Column(name = "intensity_min", nullable = false)
    private Double intensityMin;
    
    @Column(name = "intensity_max", nullable = false)
    private Double intensityMax;
    
    @Column(name = "intensity_avg", nullable = false)
    private Double intensityAvg;
    
    @Column(name = "configuration_id")
    private Long configurationId; // Reference to heatmap configuration
    
    @Column(name = "generation_parameters", columnDefinition = "jsonb")
    private String generationParameters; // Parameters used for generation
    
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;
    
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // Optional expiration time
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isPublic == null) {
            isPublic = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




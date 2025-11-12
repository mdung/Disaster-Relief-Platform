package com.relief.domain.satellite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents satellite imagery data for damage assessment
 */
@Entity
@Table(name = "satellite_images", indexes = {
    @Index(name = "idx_satellite_geom", columnList = "coverage_area"),
    @Index(name = "idx_satellite_provider", columnList = "provider"),
    @Index(name = "idx_satellite_captured_at", columnList = "captured_at"),
    @Index(name = "idx_satellite_resolution", columnList = "resolution_meters")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SatelliteImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "coverage_area", nullable = false, columnDefinition = "geometry(Polygon, 4326)")
    private Polygon coverageArea;
    
    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private SatelliteProvider provider;
    
    @Column(name = "satellite_name")
    private String satelliteName;
    
    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;
    
    @Column(name = "resolution_meters", nullable = false)
    private Double resolutionMeters;
    
    @Column(name = "cloud_cover_percentage")
    private Double cloudCoverPercentage;
    
    @Column(name = "sun_elevation_angle")
    private Double sunElevationAngle;
    
    @Column(name = "sun_azimuth_angle")
    private Double sunAzimuthAngle;
    
    @Column(name = "image_bands", columnDefinition = "jsonb")
    private String imageBands; // JSON array of available bands
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional satellite metadata
    
    @Column(name = "processing_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus;
    
    @Column(name = "quality_score")
    private Double qualityScore; // 0-1, higher is better
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "satelliteImage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DamageAssessment> damageAssessments;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (processingStatus == null) {
            processingStatus = ProcessingStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




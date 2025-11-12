package com.relief.domain.satellite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDateTime;

/**
 * Represents damage assessment results from satellite imagery analysis
 */
@Entity
@Table(name = "damage_assessments", indexes = {
    @Index(name = "idx_damage_geom", columnList = "damage_area"),
    @Index(name = "idx_damage_type", columnList = "damage_type"),
    @Index(name = "idx_damage_severity", columnList = "severity"),
    @Index(name = "idx_damage_assessed_at", columnList = "assessed_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DamageAssessment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "satellite_image_id", nullable = false)
    private SatelliteImage satelliteImage;
    
    @Column(name = "damage_area", nullable = false, columnDefinition = "geometry(Polygon, 4326)")
    private Polygon damageArea;
    
    @Column(name = "damage_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DamageType damageType;
    
    @Column(name = "severity", nullable = false)
    @Enumerated(EnumType.STRING)
    private DamageSeverity severity;
    
    @Column(name = "confidence_score", nullable = false)
    private Double confidenceScore; // 0-1, higher is more confident
    
    @Column(name = "damage_percentage")
    private Double damagePercentage; // 0-100, percentage of area damaged
    
    @Column(name = "affected_area_sqm")
    private Double affectedAreaSqm; // Area in square meters
    
    @Column(name = "pre_disaster_image_id")
    private Long preDisasterImageId; // Reference to pre-disaster image
    
    @Column(name = "change_detection_score")
    private Double changeDetectionScore; // 0-1, change detection confidence
    
    @Column(name = "analysis_algorithm")
    private String analysisAlgorithm; // Algorithm used for analysis
    
    @Column(name = "analysis_parameters", columnDefinition = "jsonb")
    private String analysisParameters; // JSON of algorithm parameters
    
    @Column(name = "assessed_at", nullable = false)
    private LocalDateTime assessedAt;
    
    @Column(name = "assessed_by")
    private String assessedBy; // User or system that performed assessment
    
    @Column(name = "notes")
    private String notes; // Additional notes about the assessment
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (assessedAt == null) {
            assessedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




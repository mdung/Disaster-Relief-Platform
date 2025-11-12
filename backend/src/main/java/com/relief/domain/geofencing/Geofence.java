package com.relief.domain.geofencing;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a geofence boundary for automated monitoring and alerts
 */
@Entity
@Table(name = "geofences", indexes = {
    @Index(name = "idx_geofence_geom", columnList = "boundary"),
    @Index(name = "idx_geofence_name", columnList = "name"),
    @Index(name = "idx_geofence_active", columnList = "is_active"),
    @Index(name = "idx_geofence_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Geofence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "boundary", nullable = false, columnDefinition = "geometry(Geometry, 4326)")
    private Geometry boundary;
    
    @Column(name = "geofence_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GeofenceType geofenceType;
    
    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private GeofencePriority priority;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "buffer_distance_meters")
    private Double bufferDistanceMeters;
    
    @Column(name = "check_interval_seconds")
    private Integer checkIntervalSeconds;
    
    @Column(name = "alert_threshold")
    private Integer alertThreshold; // Number of events before alert
    
    @Column(name = "cooldown_period_seconds")
    private Integer cooldownPeriodSeconds;
    
    @Column(name = "notification_channels", columnDefinition = "jsonb")
    private String notificationChannels; // JSON array of notification channels
    
    @Column(name = "auto_actions", columnDefinition = "jsonb")
    private String autoActions; // JSON array of automated actions
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional configuration data
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;
    
    @Column(name = "last_alert_at")
    private LocalDateTime lastAlertAt;
    
    @OneToMany(mappedBy = "geofence", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GeofenceEvent> events;
    
    @OneToMany(mappedBy = "geofence", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GeofenceAlert> alerts;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (checkIntervalSeconds == null) {
            checkIntervalSeconds = 300; // Default 5 minutes
        }
        if (alertThreshold == null) {
            alertThreshold = 1;
        }
        if (cooldownPeriodSeconds == null) {
            cooldownPeriodSeconds = 3600; // Default 1 hour
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




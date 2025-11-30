package com.relief.controller.location;

import com.relief.domain.location.*;
import com.relief.service.location.LocationAnalyticsService;
import com.relief.service.location.LocationAnalyticsService.LocationHistoryRequest;
import com.relief.repository.location.LocationHistoryRepository;
import com.relief.repository.location.LocationPatternRepository;
import com.relief.repository.location.LocationOptimizationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for location analytics
 */
@RestController
@RequestMapping("/location-analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Location Analytics", description = "Location history analytics and pattern detection APIs")
public class LocationAnalyticsController {
    
    private final LocationAnalyticsService locationAnalyticsService;
    private final LocationHistoryRepository locationHistoryRepository;
    private final LocationPatternRepository locationPatternRepository;
    private final LocationOptimizationRepository locationOptimizationRepository;
    
    @PostMapping("/history")
    @Operation(summary = "Record location history", description = "Record a location history entry for analytics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER') or hasRole('HELPER') or hasRole('RESIDENT')")
    public ResponseEntity<LocationHistory> recordLocationHistory(@Valid @RequestBody LocationHistoryRequest request) {
        log.info("Recording location history for entity: {} {}", request.entityType(), request.entityId());
        LocationHistory locationHistory = locationAnalyticsService.recordLocationHistory(request);
        return ResponseEntity.ok(locationHistory);
    }
    
    @GetMapping("/history")
    @Operation(summary = "Get location history", description = "Get location history with optional filtering")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER') or hasRole('HELPER') or hasRole('RESIDENT')")
    public ResponseEntity<List<LocationHistory>> getLocationHistory(
            @Parameter(description = "Filter by entity type") @RequestParam(required = false) String entityType,
            @Parameter(description = "Filter by entity ID") @RequestParam(required = false) Long entityId,
            @Parameter(description = "Filter by activity type") @RequestParam(required = false) ActivityType activityType,
            @Parameter(description = "Filter by start time") @RequestParam(required = false) LocalDateTime startTime,
            @Parameter(description = "Filter by end time") @RequestParam(required = false) LocalDateTime endTime,
            @Parameter(description = "Filter stationary locations only") @RequestParam(defaultValue = "false") boolean stationaryOnly,
            @Parameter(description = "Filter significant locations only") @RequestParam(defaultValue = "false") boolean significantOnly,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "100") int size) {
        log.info("Getting location history - entityType: {}, entityId: {}, activityType: {}", entityType, entityId, activityType);
        
        List<LocationHistory> history;
        if (entityType != null && entityId != null) {
            history = locationHistoryRepository.findByEntityTypeAndEntityId(entityType, entityId);
        } else if (entityType != null) {
            history = locationHistoryRepository.findByEntityType(entityType);
        } else if (activityType != null) {
            history = locationHistoryRepository.findByActivityType(activityType);
        } else if (startTime != null && endTime != null) {
            history = locationHistoryRepository.findByTimestampBetween(startTime, endTime);
        } else {
            history = locationHistoryRepository.findAll();
        }
        
        // Apply additional filters
        if (stationaryOnly) {
            history = history.stream().filter(LocationHistory::getIsStationary).toList();
        }
        if (significantOnly) {
            history = history.stream().filter(LocationHistory::getIsSignificant).toList();
        }
        
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/history/within-bounds")
    @Operation(summary = "Get location history within bounds", description = "Get location history within geographic bounds")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER') or hasRole('HELPER') or hasRole('RESIDENT')")
    public ResponseEntity<List<LocationHistory>> getLocationHistoryWithinBounds(
            @Parameter(description = "Minimum longitude") @RequestParam double minLon,
            @Parameter(description = "Minimum latitude") @RequestParam double minLat,
            @Parameter(description = "Maximum longitude") @RequestParam double maxLon,
            @Parameter(description = "Maximum latitude") @RequestParam double maxLat,
            @Parameter(description = "Start time") @RequestParam(required = false) LocalDateTime startTime,
            @Parameter(description = "End time") @RequestParam(required = false) LocalDateTime endTime) {
        log.info("Getting location history within bounds: ({}, {}) to ({}, {})", minLon, minLat, maxLon, maxLat);
        
        LocalDateTime start = startTime != null ? startTime : LocalDateTime.now().minusDays(7);
        LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
        
        List<LocationHistory> history = locationHistoryRepository.findWithinBounds(
            minLon, minLat, maxLon, maxLat, start, end
        );
        
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/history/near-point")
    @Operation(summary = "Get location history near point", description = "Get location history near a specific point")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER') or hasRole('HELPER') or hasRole('RESIDENT')")
    public ResponseEntity<List<LocationHistory>> getLocationHistoryNearPoint(
            @Parameter(description = "Longitude") @RequestParam double longitude,
            @Parameter(description = "Latitude") @RequestParam double latitude,
            @Parameter(description = "Radius in meters") @RequestParam(defaultValue = "1000") double radius,
            @Parameter(description = "Start time") @RequestParam(required = false) LocalDateTime startTime,
            @Parameter(description = "End time") @RequestParam(required = false) LocalDateTime endTime) {
        log.info("Getting location history near point: ({}, {}) within {}m", longitude, latitude, radius);
        
        LocalDateTime start = startTime != null ? startTime : LocalDateTime.now().minusDays(7);
        LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
        
        List<LocationHistory> history = locationHistoryRepository.findNearPoint(
            longitude, latitude, radius, start, end
        );
        
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/patterns")
    @Operation(summary = "Get location patterns", description = "Get detected location patterns")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER') or hasRole('HELPER') or hasRole('RESIDENT')")
    public ResponseEntity<List<LocationPattern>> getLocationPatterns(
            @Parameter(description = "Filter by entity type") @RequestParam(required = false) String entityType,
            @Parameter(description = "Filter by entity ID") @RequestParam(required = false) Long entityId,
            @Parameter(description = "Filter by pattern type") @RequestParam(required = false) PatternType patternType,
            @Parameter(description = "Filter recurring patterns only") @RequestParam(defaultValue = "false") boolean recurringOnly,
            @Parameter(description = "Filter optimal patterns only") @RequestParam(defaultValue = "false") boolean optimalOnly,
            @Parameter(description = "Filter by confidence score") @RequestParam(required = false) Double minConfidence) {
        log.info("Getting location patterns - entityType: {}, entityId: {}, patternType: {}", entityType, entityId, patternType);
        
        List<LocationPattern> patterns;
        if (entityType != null && entityId != null) {
            patterns = locationPatternRepository.findByEntityTypeAndEntityId(entityType, entityId);
        } else if (entityType != null) {
            patterns = locationPatternRepository.findByEntityType(entityType);
        } else if (patternType != null) {
            patterns = locationPatternRepository.findByPatternType(patternType);
        } else {
            patterns = locationPatternRepository.findAll();
        }
        
        // Apply additional filters
        if (recurringOnly) {
            patterns = patterns.stream().filter(LocationPattern::getIsRecurring).toList();
        }
        if (optimalOnly) {
            patterns = patterns.stream().filter(LocationPattern::getIsOptimal).toList();
        }
        if (minConfidence != null) {
            patterns = patterns.stream()
                .filter(p -> p.getConfidenceScore() >= minConfidence)
                .toList();
        }
        
        return ResponseEntity.ok(patterns);
    }
    
    @GetMapping("/optimizations")
    @Operation(summary = "Get location optimizations", description = "Get optimization suggestions based on patterns")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER') or hasRole('HELPER') or hasRole('RESIDENT')")
    public ResponseEntity<List<LocationOptimization>> getLocationOptimizations(
            @Parameter(description = "Filter by pattern ID") @RequestParam(required = false) Long patternId,
            @Parameter(description = "Filter by optimization type") @RequestParam(required = false) OptimizationType optimizationType,
            @Parameter(description = "Filter by priority") @RequestParam(required = false) OptimizationPriority priority,
            @Parameter(description = "Filter by status") @RequestParam(required = false) OptimizationStatus status,
            @Parameter(description = "Filter implemented optimizations only") @RequestParam(defaultValue = "false") boolean implementedOnly,
            @Parameter(description = "Filter high priority only") @RequestParam(defaultValue = "false") boolean highPriorityOnly) {
        log.info("Getting location optimizations - patternId: {}, optimizationType: {}, priority: {}", patternId, optimizationType, priority);
        
        List<LocationOptimization> optimizations;
        if (patternId != null) {
            optimizations = locationOptimizationRepository.findByLocationPatternId(patternId);
        } else if (optimizationType != null) {
            optimizations = locationOptimizationRepository.findByOptimizationType(optimizationType);
        } else if (priority != null) {
            optimizations = locationOptimizationRepository.findByPriority(priority);
        } else if (status != null) {
            optimizations = locationOptimizationRepository.findByStatus(status);
        } else {
            optimizations = locationOptimizationRepository.findAll();
        }
        
        // Apply additional filters
        if (implementedOnly) {
            optimizations = optimizations.stream().filter(LocationOptimization::getIsImplemented).toList();
        }
        if (highPriorityOnly) {
            optimizations = locationOptimizationRepository.findHighPriorityPending();
        }
        
        return ResponseEntity.ok(optimizations);
    }
    
    @PostMapping("/patterns/{patternId}/analyze")
    @Operation(summary = "Analyze patterns for entity", description = "Trigger pattern analysis for a specific entity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER')")
    public ResponseEntity<Void> analyzePatternsForEntity(
            @Parameter(description = "Entity type") @RequestParam String entityType,
            @Parameter(description = "Entity ID") @RequestParam Long entityId) {
        log.info("Analyzing patterns for entity: {} {}", entityType, entityId);
        locationAnalyticsService.analyzePatternsForEntity(entityType, entityId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/optimizations/{optimizationId}/implement")
    @Operation(summary = "Implement optimization", description = "Mark an optimization as implemented")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER')")
    public ResponseEntity<Void> implementOptimization(
            @PathVariable Long optimizationId,
            @Parameter(description = "Implementation notes") @RequestParam(required = false) String notes,
            @Parameter(description = "Actual efficiency gain") @RequestParam(required = false) Double actualEfficiencyGain) {
        log.info("Implementing optimization: {}", optimizationId);
        
        LocationOptimization optimization = locationOptimizationRepository.findById(optimizationId)
            .orElseThrow(() -> new IllegalArgumentException("Optimization not found"));
        
        optimization.setIsImplemented(true);
        optimization.setImplementationDate(LocalDateTime.now());
        optimization.setStatus(OptimizationStatus.IMPLEMENTED);
        if (notes != null) {
            optimization.setImplementationNotes(notes);
        }
        if (actualEfficiencyGain != null) {
            optimization.setActualEfficiencyGain(actualEfficiencyGain);
        }
        
        locationOptimizationRepository.save(optimization);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/statistics/history")
    @Operation(summary = "Get location history statistics", description = "Get statistics for location history")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER')")
    public ResponseEntity<Map<String, Object>> getLocationHistoryStatistics(
            @Parameter(description = "Start date") @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) LocalDateTime endDate) {
        log.info("Getting location history statistics");
        
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();
        
        var statistics = locationHistoryRepository.getLocationHistoryStatistics(start, end);
        
        Map<String, Object> result = Map.of(
            "totalLocations", statistics.getTotalLocations(),
            "stationaryLocations", statistics.getStationaryLocations(),
            "significantLocations", statistics.getSignificantLocations(),
            "avgSpeed", statistics.getAvgSpeed(),
            "maxSpeed", statistics.getMaxSpeed(),
            "avgAccuracy", statistics.getAvgAccuracy(),
            "uniqueEntityTypes", statistics.getUniqueEntityTypes(),
            "uniqueEntities", statistics.getUniqueEntities()
        );
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/statistics/patterns")
    @Operation(summary = "Get pattern statistics", description = "Get statistics for detected patterns")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER')")
    public ResponseEntity<Map<String, Object>> getPatternStatistics(
            @Parameter(description = "Start date") @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) LocalDateTime endDate) {
        log.info("Getting pattern statistics");
        
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();
        
        var statistics = locationPatternRepository.getPatternStatistics(start, end);
        
        Map<String, Object> result = Map.ofEntries(
            Map.entry("totalPatterns", statistics.getTotalPatterns()),
            Map.entry("linearPatterns", statistics.getLinearPatterns()),
            Map.entry("circularPatterns", statistics.getCircularPatterns()),
            Map.entry("stationaryPatterns", statistics.getStationaryPatterns()),
            Map.entry("routePatterns", statistics.getRoutePatterns()),
            Map.entry("searchPatterns", statistics.getSearchPatterns()),
            Map.entry("recurringPatterns", statistics.getRecurringPatterns()),
            Map.entry("optimalPatterns", statistics.getOptimalPatterns()),
            Map.entry("avgConfidence", statistics.getAvgConfidence()),
            Map.entry("avgSpeed", statistics.getAvgSpeed()),
            Map.entry("avgDistance", statistics.getAvgDistance())
        );
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/statistics/optimizations")
    @Operation(summary = "Get optimization statistics", description = "Get statistics for optimizations")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER')")
    public ResponseEntity<Map<String, Object>> getOptimizationStatistics(
            @Parameter(description = "Start date") @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) LocalDateTime endDate) {
        log.info("Getting optimization statistics");
        
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();
        
        var statistics = locationOptimizationRepository.getOptimizationStatistics(start, end);
        
        Map<String, Object> result = Map.ofEntries(
            Map.entry("totalOptimizations", statistics.getTotalOptimizations()),
            Map.entry("pendingOptimizations", statistics.getPendingOptimizations()),
            Map.entry("approvedOptimizations", statistics.getApprovedOptimizations()),
            Map.entry("inProgressOptimizations", statistics.getInProgressOptimizations()),
            Map.entry("completedOptimizations", statistics.getCompletedOptimizations()),
            Map.entry("implementedOptimizations", statistics.getImplementedOptimizations()),
            Map.entry("avgCurrentEfficiency", statistics.getAvgCurrentEfficiency()),
            Map.entry("avgProjectedEfficiency", statistics.getAvgProjectedEfficiency()),
            Map.entry("avgActualEfficiencyGain", statistics.getAvgActualEfficiencyGain()),
            Map.entry("totalTimeSavings", statistics.getTotalTimeSavings()),
            Map.entry("totalDistanceSavings", statistics.getTotalDistanceSavings())
        );
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/statistics/activity-types")
    @Operation(summary = "Get activity type statistics", description = "Get statistics by activity type")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER')")
    public ResponseEntity<List<Map<String, Object>>> getActivityTypeStatistics(
            @Parameter(description = "Start date") @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) LocalDateTime endDate) {
        log.info("Getting activity type statistics");
        
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();
        
        var statistics = locationHistoryRepository.getActivityTypeStatistics(start, end);
        
        List<Map<String, Object>> result = statistics.stream()
            .map(stat -> {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("activityType", stat.getActivityType());
                map.put("count", stat.getCount());
                map.put("avgSpeed", stat.getAvgSpeed());
                map.put("avgDuration", stat.getAvgDuration());
                map.put("avgAccuracy", stat.getAvgAccuracy());
                return map;
            })
            .toList();
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/statistics/entity-movement")
    @Operation(summary = "Get entity movement statistics", description = "Get movement statistics by entity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER')")
    public ResponseEntity<List<Map<String, Object>>> getEntityMovementStatistics(
            @Parameter(description = "Start date") @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam(required = false) LocalDateTime endDate) {
        log.info("Getting entity movement statistics");
        
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();
        
        var statistics = locationHistoryRepository.getEntityMovementStatistics(start, end);
        
        List<Map<String, Object>> result = statistics.stream()
            .map(stat -> {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("entityType", stat.getEntityType());
                map.put("entityId", stat.getEntityId());
                map.put("entityName", stat.getEntityName());
                map.put("locationCount", stat.getLocationCount());
                map.put("avgSpeed", stat.getAvgSpeed());
                map.put("maxSpeed", stat.getMaxSpeed());
                map.put("totalDistance", stat.getTotalDistance());
                map.put("avgAccuracy", stat.getAvgAccuracy());
                map.put("firstSeen", stat.getFirstSeen());
                map.put("lastSeen", stat.getLastSeen());
                return map;
            })
            .toList();
        
        return ResponseEntity.ok(result);
    }
}




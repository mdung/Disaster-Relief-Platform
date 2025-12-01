package com.relief.controller;

import com.relief.repository.NeedsRequestRepository;
import com.relief.repository.TaskRepository;
import com.relief.repository.UserRepository;
import com.relief.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard controller for main dashboard page
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "Dashboard data endpoints")
public class DashboardController {

    private final NeedsRequestRepository needsRequestRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Get real-time statistics for the main dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            // Calculate today's date range
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
            
            // Active requests (needs with status "OPEN" or "IN_PROGRESS")
            long activeRequests = needsRequestRepository.countByStatus("OPEN") 
                + needsRequestRepository.countByStatus("IN_PROGRESS");
            
            // Completed tasks today
            long completedToday = taskRepository.countByStatusAndUpdatedAtBetween("delivered", todayStart, todayEnd);
            
            // Active helpers (users with HELPER role who are not disabled)
            long activeHelpers = userRepository.countByRole()
                .getOrDefault("HELPER", 0L);
            // Also count active users with HELPER role (not disabled)
            long activeHelperUsers = userRepository.findAll().stream()
                .filter(u -> "HELPER".equals(u.getRole()) && (u.getDisabled() == null || !u.getDisabled()))
                .count();
            activeHelpers = Math.max(activeHelpers, activeHelperUsers);
            
            // Average response time (in hours)
            Map<String, Object> responseTimes = analyticsService.getResponseTimes(
                LocalDateTime.now().minusDays(7), 
                LocalDateTime.now()
            );
            double avgResponseTimeHours = responseTimes.containsKey("average") 
                ? (Double) responseTimes.get("average") 
                : 2.3;
            String responseTimeStr = String.format("%.1fh", avgResponseTimeHours);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("activeRequests", activeRequests);
            stats.put("completedToday", completedToday);
            stats.put("activeHelpers", activeHelpers);
            stats.put("responseTime", responseTimeStr);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching dashboard stats", e);
            // Return default values on error
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("activeRequests", 0L);
            defaultStats.put("completedToday", 0L);
            defaultStats.put("activeHelpers", 0L);
            defaultStats.put("responseTime", "0h");
            return ResponseEntity.ok(defaultStats);
        }
    }

    @GetMapping("/activity")
    @Operation(summary = "Get activity chart data", description = "Get activity data for the dashboard chart")
    public ResponseEntity<List<Map<String, Object>>> getActivityData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusDays(7);
            LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();
            
            // Get needs trends for activity chart
            List<com.relief.dto.TimeSeriesData> trends = analyticsService.getNeedsTrends(start, end, "day");
            
            // Convert to chart-friendly format
            List<Map<String, Object>> activityData = trends.stream()
                .map(trend -> {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("date", trend.getTimestamp().toString());
                    dataPoint.put("value", trend.getValue());
                    dataPoint.put("label", trend.getLabel());
                    return dataPoint;
                })
                .toList();
            
            return ResponseEntity.ok(activityData);
        } catch (Exception e) {
            log.error("Error fetching activity data", e);
            return ResponseEntity.ok(List.of());
        }
    }
}


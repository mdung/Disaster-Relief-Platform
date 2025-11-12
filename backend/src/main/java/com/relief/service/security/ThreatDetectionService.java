package com.relief.service.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThreatDetectionService {

    private final Map<String, SecurityEvent> events = new ConcurrentHashMap<>();
    private final Map<String, ThreatAlert> alerts = new ConcurrentHashMap<>();

    public SecurityEvent recordEvent(String userId, String ip, String action, Map<String, Object> context) {
        SecurityEvent e = new SecurityEvent();
        e.setId(UUID.randomUUID().toString());
        e.setUserId(userId);
        e.setIp(ip);
        e.setAction(action);
        e.setContext(context != null ? context : new HashMap<>());
        e.setTimestamp(LocalDateTime.now());
        e.setAnomalyScore(calculateAnomalyScore(e));
        events.put(e.getId(), e);

        if (e.getAnomalyScore() >= 0.8) {
            createThreatAlert(e);
        }
        return e;
    }

    public List<SecurityEvent> getRecentEvents(int limit) {
        return events.values().stream()
                .sorted(Comparator.comparing(SecurityEvent::getTimestamp).reversed())
                .limit(limit)
                .toList();
    }

    public List<ThreatAlert> getActiveAlerts() {
        return alerts.values().stream()
                .filter(a -> !a.isAcknowledged())
                .sorted(Comparator.comparing(ThreatAlert::getCreatedAt).reversed())
                .toList();
    }

    public void acknowledgeAlert(String alertId, String userId) {
        ThreatAlert a = alerts.get(alertId);
        if (a != null) {
            a.setAcknowledged(true);
            a.setAcknowledgedBy(userId);
            a.setAcknowledgedAt(LocalDateTime.now());
        }
    }

    private void createThreatAlert(SecurityEvent e) {
        ThreatAlert a = new ThreatAlert();
        a.setId(UUID.randomUUID().toString());
        a.setEventId(e.getId());
        a.setSeverity(e.getAnomalyScore() > 0.95 ? "CRITICAL" : e.getAnomalyScore() > 0.9 ? "HIGH" : "MEDIUM");
        a.setTitle("Anomalous security activity detected");
        a.setMessage("Anomalous event from IP %s with action %s".formatted(e.getIp(), e.getAction()));
        a.setCreatedAt(LocalDateTime.now());
        a.setAcknowledged(false);
        alerts.put(a.getId(), a);
        log.warn("Threat alert created: {} severity={} score={}", a.getId(), a.getSeverity(), e.getAnomalyScore());
    }

    private double calculateAnomalyScore(SecurityEvent e) {
        double score = 0.0;
        if (e.getAction() != null && e.getAction().toLowerCase().contains("admin")) score += 0.3;
        if (e.getIp() != null && (e.getIp().startsWith("192.168.") || e.getIp().startsWith("10."))) score += 0.1;
        Object failed = e.getContext().get("failedAttempts");
        if (failed instanceof Number n) score += Math.min(0.6, n.doubleValue() * 0.1);
        return Math.min(1.0, score);
    }

    @lombok.Data
    public static class SecurityEvent {
        private String id;
        private String userId;
        private String ip;
        private String action;
        private Map<String, Object> context;
        private LocalDateTime timestamp;
        private double anomalyScore;
    }

    @lombok.Data
    public static class ThreatAlert {
        private String id;
        private String eventId;
        private String title;
        private String message;
        private String severity;
        private boolean acknowledged;
        private String acknowledgedBy;
        private LocalDateTime acknowledgedAt;
        private LocalDateTime createdAt;
    }
}





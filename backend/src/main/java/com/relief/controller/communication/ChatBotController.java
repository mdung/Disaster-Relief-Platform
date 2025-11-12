package com.relief.controller.communication;

import com.relief.service.communication.ChatBotService;
import com.relief.service.communication.ChatBotService.ChatBotResponse;
import com.relief.service.communication.ChatBotService.ChatSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Chat bot controller
 */
@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat Bot", description = "AI-powered chat bot APIs")
public class ChatBotController {

    private final ChatBotService chatBotService;

    @PostMapping("/message")
    @Operation(summary = "Send message to chat bot")
    public ResponseEntity<ChatBotResponse> sendMessage(
            @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        
        UUID userId = UUID.fromString(principal.getUsername());
        
        ChatBotResponse response = chatBotService.processMessage(
            request.getSessionId(),
            request.getMessage(),
            userId
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions")
    @Operation(summary = "Get user's chat sessions")
    public ResponseEntity<List<ChatSession>> getUserSessions(
            @AuthenticationPrincipal UserDetails principal) {
        
        UUID userId = UUID.fromString(principal.getUsername());
        
        List<ChatSession> sessions = chatBotService.getUserSessions(userId);
        
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/sessions/{sessionId}")
    @Operation(summary = "Get chat session details")
    public ResponseEntity<ChatSession> getChatSession(
            @PathVariable String sessionId) {
        
        ChatSession session = chatBotService.getChatSession(sessionId);
        
        return ResponseEntity.ok(session);
    }

    @PostMapping("/sessions/{sessionId}/end")
    @Operation(summary = "End chat session")
    public ResponseEntity<Map<String, String>> endSession(
            @PathVariable String sessionId) {
        
        chatBotService.endSession(sessionId);
        
        return ResponseEntity.ok(Map.of("status", "success", "message", "Session ended successfully"));
    }

    // Request DTOs
    public static class SendMessageRequest {
        private String sessionId;
        private String message;

        // Getters and setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

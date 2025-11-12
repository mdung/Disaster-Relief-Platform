package com.relief.controller;

import com.relief.realtime.RealtimeBroadcaster;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/requests/stream")
@RequiredArgsConstructor
@Tag(name = "Realtime", description = "SSE stream for realtime updates")
public class RealtimeController {

    private final RealtimeBroadcaster broadcaster;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to Server-Sent Events")
    public SseEmitter stream() {
        return broadcaster.register(0L);
    }
}






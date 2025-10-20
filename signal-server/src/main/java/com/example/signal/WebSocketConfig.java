package com.example.signal;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket configuration class. Registers a single handler endpoint that
 * accepts all incoming WebSocket connections at the path "/ws". A wildcard
 * origin policy is configured here for simplicity; in production scenarios
 * this should be restricted to the domains that serve your front‑end
 * application to mitigate cross‑site WebSocket hijacking attacks.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final SignalWebSocketHandler signalWebSocketHandler;

    public WebSocketConfig(SignalWebSocketHandler signalWebSocketHandler) {
        this.signalWebSocketHandler = signalWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalWebSocketHandler, "/ws")
                .setAllowedOrigins("*");
    }
}
package com.pointing.poker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 🔥 1. RAW WebSocket endpoint (Postman, native clients)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        // 🔥 2. SockJS fallback endpoint (Angular browsers)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // for broadcasts
        registry.setApplicationDestinationPrefixes("/app"); // for @MessageMapping
    }
}

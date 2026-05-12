package com.feasymenu.server.config;

import com.feasymenu.server.repository.OrderRepository;
import com.feasymenu.server.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final OrderRepository orderRepository;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/ws/raw").setAllowedOriginPatterns("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        if (request instanceof ServletServerHttpRequest) {
                            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request)
                                    .getServletRequest();
                            Cookie[] cookies = servletRequest.getCookies();
                            if (cookies != null) {
                                for (Cookie cookie : cookies) {
                                    if ("qm_token".equals(cookie.getName())) {
                                        attributes.put("qm_token", cookie.getValue());
                                    }
                                }
                            }
                        }
                        return super.beforeHandshake(request, response, wsHandler, attributes);
                    }
                });
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null)
                    return message;

                log.info("WebSocket Frame: Command={}, Destination={}", accessor.getCommand(),
                        accessor.getDestination());

                if (StompCommand.CONNECT.equals(accessor.getCommand())
                        || StompCommand.STOMP.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    String token = null;

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        token = authHeader.substring(7);
                    } else {
                        // 1. Try from handshake attributes (Set by HandshakeInterceptor)
                        var attributes = accessor.getSessionAttributes();
                        if (attributes != null && attributes.containsKey("qm_token")) {
                            token = (String) attributes.get("qm_token");
                        }

                        // 2. Fallback: Check cookies in native headers (Sometimes available)
                        if (token == null) {
                            String cookieHeader = accessor.getFirstNativeHeader("Cookie");
                            if (cookieHeader != null) {
                                token = java.util.Arrays.stream(cookieHeader.split(";")).map(String::trim)
                                        .filter(c -> c.startsWith("qm_token=")).map(c -> c.substring(9)).findFirst()
                                        .orElse(null);
                            }
                        }
                    }

                    if (token != null && jwtUtil.validateToken(token)) {
                        Authentication auth = jwtUtil.getAuthentication(token);
                        accessor.setUser(auth);
                        String tenant = jwtUtil.extractTenant(token);
                        var attributes = accessor.getSessionAttributes();
                        if (attributes == null) {
                            attributes = new HashMap<>();
                            accessor.setSessionAttributes(attributes);
                        }
                        attributes.put("tenant", tenant);
                        log.info("WebSocket CONNECT: User {} connected with tenant {}", auth.getName(), tenant);
                    }
                }

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    if (destination == null)
                        return message;

                    Authentication auth = (Authentication) accessor.getUser();
                    var sessionAttributes = accessor.getSessionAttributes();

                    // Example: /topic/session/{sid}
                    if (destination.startsWith("/topic/session/")) {
                        // Anonymous or Admin can view their own session.
                        // For anonymous, we trust the sid is hard to guess (UUID).
                        // For authenticated admins, we check tenant match.
                        if (auth != null && auth.isAuthenticated()) {
                            String userTenant = null;
                            if (auth.getDetails() instanceof Map) {
                                userTenant = (String) ((Map<?, ?>) auth.getDetails()).get("tenant");
                            }

                            if (userTenant == null) {
                                log.debug("Native Headers (Session): {}", accessor.toNativeHeaderMap());
                                String authHeader = accessor.getFirstNativeHeader("Authorization");
                                if (authHeader == null)
                                    authHeader = accessor.getFirstNativeHeader("authorization");

                                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                    userTenant = jwtUtil.extractTenant(authHeader.substring(7));
                                }
                            }

                            String sid = destination.substring("/topic/session/".length());

                            // Real check: Does this order session belong to userTenant?
                            // This prevents Admin of Tenant A from spying on Tenant B's session.
                            if (userTenant != null) {
                                boolean belongs = orderRepository.existsBySessionIdAndTenant_Code(sid, userTenant);
                                if (!belongs) {
                                    log.warn("Admin {} tried to subscribe to unauthorized session {}", auth.getName(),
                                            sid);
                                    throw new RuntimeException("Unauthorized subscription");
                                }
                            }
                        }
                    }

                    // Example: /topic/orders/{tenantCode}
                    if (destination.startsWith("/topic/orders/")) {
                        String tenantCode = destination.substring("/topic/orders/".length());
                        if (auth == null || !auth.isAuthenticated()) {
                            log.warn("Unauthorized anonymous subscription attempt to {}", destination);
                            throw new RuntimeException("Unauthorized");
                        }
                        String userTenant = null;
                        if (auth.getDetails() instanceof Map) {
                            userTenant = (String) ((Map<?, ?>) auth.getDetails()).get("tenant");
                        }

                        // FALLBACK: If userTenant is still null, it might be because Spring Security
                        // authenticated the session via Cookies. We should extract it from the
                        // native header in this frame if possible.
                        // DEBUG: Log all native headers to see what's actually coming
                        log.debug("Native Headers: {}", accessor.toNativeHeaderMap());

                        if (userTenant == null) {
                            String authHeader = accessor.getFirstNativeHeader("Authorization");
                            if (authHeader == null)
                                authHeader = accessor.getFirstNativeHeader("authorization");

                            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                                String token = authHeader.substring(7);
                                userTenant = jwtUtil.extractTenant(token);
                                log.debug("WebSocket Sub: Extracted tenant from header fallback ({}): {}", authHeader,
                                        userTenant);
                            }
                        }

                        log.debug("WebSocket Sub Check: userTenant={}, targetTenant={}", userTenant, tenantCode);
                        if (userTenant == null || !userTenant.equals(tenantCode)) {
                            log.warn("Unauthorized! Admin {} (tenant: {}) tried to subscribe to: {}", auth.getName(),
                                    userTenant, tenantCode);
                            throw new RuntimeException("Unauthorized");
                        }
                    }
                }
                return message;
            }
        });
    }
}

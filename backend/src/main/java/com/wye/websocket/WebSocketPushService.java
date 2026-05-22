package com.wye.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketPushService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    private final Set<WebSocketSession> adminSessions = ConcurrentHashMap.newKeySet();

    public void register(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        Integer role = (Integer) session.getAttributes().get("role");
        if (userId == null) {
            return;
        }

        userSessions.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(session);
        if (role != null && role == 0) {
            adminSessions.add(session);
        }

        send(session, buildEvent("connected", mapOf("userId", userId, "role", role)));
    }

    public void unregister(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    userSessions.remove(userId);
                }
            }
        }
        adminSessions.remove(session);
    }

    public void pushToUser(Long userId, String event, Object payload) {
        if (userId == null) {
            return;
        }
        broadcast(userSessions.get(userId), buildEvent(event, payload));
    }

    public void pushToAdmins(String event, Object payload) {
        broadcast(adminSessions, buildEvent(event, payload));
    }

    public void pushChatMessage(Long sessionId, Object payload) {
        pushToUser(sessionId, "chat.message", payload);
        pushToAdmins("chat.message", payload);
    }

    private Map<String, Object> buildEvent(String event, Object payload) {
        Map<String, Object> message = new HashMap<>();
        message.put("event", event);
        message.put("payload", payload);
        message.put("timestamp", System.currentTimeMillis());
        return message;
    }

    private Map<String, Object> mapOf(String key1, Object value1, String key2, Object value2) {
        Map<String, Object> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    private void broadcast(Set<WebSocketSession> sessions, Map<String, Object> message) {
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        for (WebSocketSession session : new HashSet<>(sessions)) {
            send(session, message);
        }
    }

    private void send(WebSocketSession session, Map<String, Object> message) {
        if (session == null || !session.isOpen()) {
            unregister(session);
            return;
        }
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            unregister(session);
        }
    }
}

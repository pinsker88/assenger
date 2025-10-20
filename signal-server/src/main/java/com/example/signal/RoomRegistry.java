package com.example.signal;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry that tracks which WebSocket sessions belong to which rooms.
 * Each room is identified by a string key (room ID) and maintains a
 * thread‑safe set of sessions. This class provides methods to join a room,
 * leave a room, and retrieve all peers currently connected to a room. When
 * the last session leaves a room, the room entry is removed to keep the
 * registry map tidy.
 */
@Component
public class RoomRegistry {
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    /**
     * Adds a session to the specified room. If the room does not yet exist,
     * it will be created.
     *
     * @param roomId the identifier of the room
     * @param session the WebSocket session to add
     */
    public void join(String roomId, WebSocketSession session) {
        rooms.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    /**
     * Removes a session from the specified room. If the room becomes empty
     * after removal, it is cleaned up from the registry.
     *
     * @param roomId the identifier of the room
     * @param session the WebSocket session to remove
     */
    public void leave(String roomId, WebSocketSession session) {
        Set<WebSocketSession> set = rooms.get(roomId);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) {
                rooms.remove(roomId);
            }
        }
    }

    /**
     * Returns an immutable view of all WebSocket sessions currently
     * associated with the specified room. If the room does not exist, an
     * empty set is returned.
     *
     * @param roomId the identifier of the room
     * @return a set of WebSocket sessions currently in the room
     */
    public Set<WebSocketSession> peers(String roomId) {
        return rooms.getOrDefault(roomId, Collections.emptySet());
    }
}
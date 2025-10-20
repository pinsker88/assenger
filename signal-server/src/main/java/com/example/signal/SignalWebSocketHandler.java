package com.example.signal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

/**
 * A simple WebSocket handler that relays signaling messages between peers
 * connected to the same room. Each client identifies which room it belongs to
 * via the query parameter "room" in the WebSocket handshake URI. When a
 * client sends a text message, the handler forwards the payload to every
 * other client in the room. It also sends a lightweight notification to
 * peers when a new participant joins.
 */
@Component
public class SignalWebSocketHandler extends TextWebSocketHandler {

    private static final String ATTR_ROOM = "roomId";

    private final RoomRegistry roomRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SignalWebSocketHandler(RoomRegistry roomRegistry) {
        this.roomRegistry = roomRegistry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extract the room identifier from the WebSocket handshake URI.
        String roomId = parseRoomId(session.getUri());
        if (roomId == null || roomId.isBlank()) {
            // Close with BAD_DATA status if the room parameter is missing.
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        session.getAttributes().put(ATTR_ROOM, roomId);
        roomRegistry.join(roomId, session);

        // Notify other peers in the room that a new client has joined.
        broadcast(roomId, session, json(Map.of("type", "peer-join")));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = (String) session.getAttributes().get(ATTR_ROOM);
        if (roomId == null) {
            return;
        }
        // Relay the received payload to all other peers in the same room.
        broadcast(roomId, session, message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String roomId = (String) session.getAttributes().get(ATTR_ROOM);
        if (roomId != null) {
            roomRegistry.leave(roomId, session);
        }
    }

    /**
     * Broadcasts a text payload to all sessions in the specified room except
     * the origin. Handles any I/O exceptions by silently ignoring failed
     * sends.
     *
     * @param roomId the target room
     * @param origin the session that should not receive the message
     * @param payload the raw JSON payload to send
     */
    private void broadcast(String roomId, WebSocketSession origin, String payload) {
        Set<WebSocketSession> peers = roomRegistry.peers(roomId);
        for (WebSocketSession peer : peers) {
            if (peer != origin && peer.isOpen()) {
                try {
                    peer.sendMessage(new TextMessage(payload));
                } catch (IOException e) {
                    // Ignore and continue. Logging could be added here if desired.
                }
            }
        }
    }

    /**
     * Parses the room identifier from the query portion of the WebSocket URI.
     * The expected format is ?room=ROOMID (and optionally other parameters).
     *
     * @param uri the WebSocket handshake URI
     * @return the room ID if present, otherwise null
     */
    private String parseRoomId(URI uri) {
        if (uri == null || uri.getQuery() == null) {
            return null;
        }
        String[] pairs = uri.getQuery().split("&");
        for (String kv : pairs) {
            String[] parts = kv.split("=", 2);
            if (parts.length == 2 && parts[0].equals("room")) {
                return parts[1];
            }
        }
        return null;
    }

    /**
     * Serializes an arbitrary map into a JSON string using a configured
     * {@link ObjectMapper}. This method centralizes JSON creation to avoid
     * repeating exception handling every time a message needs to be generated.
     *
     * @param data the map to serialize
     * @return a JSON string representation of the map
     * @throws IOException if serialization fails
     */
    private String json(Map<String, String> data) throws IOException {
        return objectMapper.writeValueAsString(data);
    }
}
package com.poker.game.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poker.common.constant.GameEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 游戏 WebSocket 处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** userId -> WebSocketSession */
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /** userId -> roomId */
    private final Map<String, String> userRoomMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = getUserId(session);
        if (userId != null) {
            sessions.put(userId, session);
            log.info("玩家 {} 已连接 WebSocket", userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = getUserId(session);
        if (userId == null) return;

        Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");
        Map<String, Object> data = (Map<String, Object>) payload.get("data");

        log.debug("收到玩家 {} 的消息: type={}", userId, type);

        switch (type) {
            case GameEvent.JOIN_ROOM -> handleJoinRoom(userId, data);
            case GameEvent.LEAVE_ROOM -> handleLeaveRoom(userId, data);
            case GameEvent.PLAYER_ACTION -> handlePlayerAction(userId, data);
            case GameEvent.CHAT_MESSAGE -> handleChatMessage(userId, data);
            case GameEvent.PING -> sendToUser(userId, GameEvent.PONG, Map.of());
            case GameEvent.RECONNECT -> handleReconnect(userId, data);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = getUserId(session);
        if (userId != null) {
            sessions.remove(userId);
            log.info("玩家 {} 断开 WebSocket 连接", userId);
            // TODO: 通知游戏引擎玩家断线
        }
    }

    // ---- 消息处理方法 ----

    private void handleJoinRoom(String userId, Map<String, Object> data) {
        String roomId = (String) data.get("roomId");
        userRoomMap.put(userId, roomId);
        // TODO: 调用 GameService 处理加入房间逻辑
        log.info("玩家 {} 加入房间 {}", userId, roomId);
    }

    private void handleLeaveRoom(String userId, Map<String, Object> data) {
        String roomId = userRoomMap.remove(userId);
        // TODO: 调用 GameService 处理离开房间逻辑
        log.info("玩家 {} 离开房间 {}", userId, roomId);
    }

    private void handlePlayerAction(String userId, Map<String, Object> data) {
        String roomId = userRoomMap.get(userId);
        String action = (String) data.get("action");
        int amount = data.containsKey("amount") ? ((Number) data.get("amount")).intValue() : 0;
        // TODO: 调用 GameEngine 处理玩家操作
        log.info("玩家 {} 在房间 {} 执行操作: {} ({})", userId, roomId, action, amount);
    }

    private void handleChatMessage(String userId, Map<String, Object> data) {
        String roomId = userRoomMap.get(userId);
        // TODO: 广播聊天消息给房间内所有玩家
    }

    private void handleReconnect(String userId, Map<String, Object> data) {
        String roomId = (String) data.get("roomId");
        userRoomMap.put(userId, roomId);
        // TODO: 发送当前游戏状态给重连玩家
        log.info("玩家 {} 重连房间 {}", userId, roomId);
    }

    // ---- 消息发送方法 ----

    /**
     * 发送消息给指定用户
     */
    public void sendToUser(String userId, String type, Object data) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> msg = Map.of("type", type, "data", data);
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            } catch (Exception e) {
                log.error("发送消息给用户 {} 失败", userId, e);
            }
        }
    }

    /**
     * 广播消息给房间内所有玩家
     */
    public void broadcastToRoom(String roomId, String type, Object data) {
        userRoomMap.entrySet().stream()
                .filter(e -> roomId.equals(e.getValue()))
                .forEach(e -> sendToUser(e.getKey(), type, data));
    }

    private String getUserId(WebSocketSession session) {
        return (String) session.getAttributes().get("userId");
    }
}

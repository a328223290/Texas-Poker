package com.poker.common.constant;

/**
 * WebSocket 游戏事件类型常量
 */
public final class GameEvent {

    private GameEvent() {}

    // --- 房间事件 ---
    public static final String JOIN_ROOM = "JOIN_ROOM";
    public static final String LEAVE_ROOM = "LEAVE_ROOM";
    public static final String PLAYER_JOINED = "PLAYER_JOINED";
    public static final String PLAYER_LEFT = "PLAYER_LEFT";

    // --- 游戏流程事件 ---
    public static final String GAME_START = "GAME_START";
    public static final String DEAL_CARDS = "DEAL_CARDS";
    public static final String COMMUNITY_CARDS = "COMMUNITY_CARDS";
    public static final String YOUR_TURN = "YOUR_TURN";
    public static final String GAME_END = "GAME_END";

    // --- 玩家操作事件 ---
    public static final String PLAYER_ACTION = "PLAYER_ACTION";
    public static final String PLAYER_ACTED = "PLAYER_ACTED";

    // --- 游戏状态同步 ---
    public static final String GAME_STATE = "GAME_STATE";

    // --- 聊天 ---
    public static final String CHAT_MESSAGE = "CHAT_MESSAGE";

    // --- 系统 ---
    public static final String ERROR = "ERROR";
    public static final String RECONNECT = "RECONNECT";
    public static final String PING = "PING";
    public static final String PONG = "PONG";
}

package com.poker.common.enums;

/**
 * 玩家状态
 */
public enum PlayerStatus {
    WAITING,        // 等待中
    PLAYING,        // 游戏中
    FOLDED,         // 已弃牌
    ALL_IN,         // 已全押
    DISCONNECTED    // 已断线
}

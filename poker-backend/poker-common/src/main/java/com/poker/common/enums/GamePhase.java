package com.poker.common.enums;

/**
 * 游戏阶段状态
 */
public enum GamePhase {
    WAITING,      // 等待玩家
    PRE_FLOP,     // 翻牌前
    FLOP,         // 翻牌
    TURN,         // 转牌
    RIVER,        // 河牌
    SHOWDOWN,     // 摊牌
    SETTLEMENT    // 结算
}

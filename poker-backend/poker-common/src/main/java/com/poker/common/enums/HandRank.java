package com.poker.common.enums;

/**
 * 牌型等级（从高到低）
 */
public enum HandRank {
    ROYAL_FLUSH(10, "皇家同花顺"),
    STRAIGHT_FLUSH(9, "同花顺"),
    FOUR_OF_A_KIND(8, "四条"),
    FULL_HOUSE(7, "葫芦"),
    FLUSH(6, "同花"),
    STRAIGHT(5, "顺子"),
    THREE_OF_A_KIND(4, "三条"),
    TWO_PAIR(3, "两对"),
    ONE_PAIR(2, "一对"),
    HIGH_CARD(1, "高牌");

    private final int level;
    private final String name;

    HandRank(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() { return level; }
    public String getName() { return name; }
}

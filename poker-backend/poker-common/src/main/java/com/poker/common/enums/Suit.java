package com.poker.common.enums;

/**
 * 扑克牌花色
 */
public enum Suit {
    SPADES("♠", "黑桃"),
    HEARTS("♥", "红心"),
    DIAMONDS("♦", "方块"),
    CLUBS("♣", "梅花");

    private final String symbol;
    private final String name;

    Suit(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
}

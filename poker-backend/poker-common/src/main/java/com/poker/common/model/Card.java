package com.poker.common.model;

import com.poker.common.enums.Rank;
import com.poker.common.enums.Suit;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 扑克牌
 */
@Data
@AllArgsConstructor
public class Card implements Comparable<Card> {

    private Suit suit;
    private Rank rank;

    @Override
    public int compareTo(Card other) {
        return Integer.compare(this.rank.getValue(), other.rank.getValue());
    }

    @Override
    public String toString() {
        return suit.getSymbol() + rank.getDisplay();
    }
}

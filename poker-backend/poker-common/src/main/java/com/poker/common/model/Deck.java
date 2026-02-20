package com.poker.common.model;

import com.poker.common.enums.Rank;
import com.poker.common.enums.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 牌组（一副52张牌）
 */
public class Deck {

    private final List<Card> cards;
    private int dealIndex;

    public Deck() {
        cards = new ArrayList<>(52);
        dealIndex = 0;
        init();
    }

    private void init() {
        cards.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        dealIndex = 0;
    }

    /**
     * 洗牌（Fisher-Yates 算法）
     */
    public void shuffle() {
        Collections.shuffle(cards);
        dealIndex = 0;
    }

    /**
     * 发一张牌
     */
    public Card deal() {
        if (dealIndex >= cards.size()) {
            throw new IllegalStateException("牌已发完");
        }
        return cards.get(dealIndex++);
    }

    /**
     * 发指定数量的牌
     */
    public List<Card> deal(int count) {
        List<Card> dealt = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            dealt.add(deal());
        }
        return dealt;
    }

    /**
     * 重置牌组
     */
    public void reset() {
        init();
    }

    public int remaining() {
        return cards.size() - dealIndex;
    }
}

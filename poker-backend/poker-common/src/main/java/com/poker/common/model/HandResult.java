package com.poker.common.model;

import com.poker.common.enums.HandRank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 牌型评估结果
 */
@Data
@AllArgsConstructor
public class HandResult implements Comparable<HandResult> {

    /** 牌型等级 */
    private HandRank handRank;

    /** 组成该牌型的5张牌（排序后） */
    private List<Card> bestHand;

    /** 用于比较的关键牌值（从高到低） */
    private List<Integer> kickers;

    @Override
    public int compareTo(HandResult other) {
        // 先比较牌型等级
        int cmp = Integer.compare(this.handRank.getLevel(), other.handRank.getLevel());
        if (cmp != 0) return cmp;

        // 牌型相同，比较 kicker
        for (int i = 0; i < Math.min(kickers.size(), other.kickers.size()); i++) {
            cmp = Integer.compare(kickers.get(i), other.kickers.get(i));
            if (cmp != 0) return cmp;
        }
        return 0;
    }
}

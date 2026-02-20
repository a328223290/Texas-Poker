package com.poker.game.engine;

import com.poker.common.enums.HandRank;
import com.poker.common.enums.Rank;
import com.poker.common.model.Card;
import com.poker.common.model.HandResult;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 牌型评估器 - 从7张牌中选出最佳5张牌组合
 */
public class HandEvaluator {

    /**
     * 评估最佳牌型（从手牌+公共牌中选出最佳5张）
     */
    public static HandResult evaluate(List<Card> holeCards, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(holeCards);
        allCards.addAll(communityCards);

        // 生成所有5张牌的组合
        List<List<Card>> combinations = getCombinations(allCards, 5);

        HandResult best = null;
        for (List<Card> combo : combinations) {
            HandResult result = evaluateFive(combo);
            if (best == null || result.compareTo(best) > 0) {
                best = result;
            }
        }
        return best;
    }

    /**
     * 评估5张牌的牌型
     */
    private static HandResult evaluateFive(List<Card> cards) {
        cards.sort(Comparator.comparingInt(c -> -c.getRank().getValue()));

        boolean isFlush = isFlush(cards);
        boolean isStraight = isStraight(cards);

        List<Integer> values = cards.stream()
                .map(c -> c.getRank().getValue())
                .collect(Collectors.toList());

        // 统计每个点数出现的次数
        Map<Integer, Long> counts = values.stream()
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()));

        List<Map.Entry<Integer, Long>> sorted = counts.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = Long.compare(b.getValue(), a.getValue());
                    return cmp != 0 ? cmp : Integer.compare(b.getKey(), a.getKey());
                })
                .collect(Collectors.toList());

        List<Integer> kickers = sorted.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 皇家同花顺
        if (isFlush && isStraight && values.get(0) == Rank.ACE.getValue()) {
            return new HandResult(HandRank.ROYAL_FLUSH, cards, kickers);
        }

        // 同花顺
        if (isFlush && isStraight) {
            return new HandResult(HandRank.STRAIGHT_FLUSH, cards, kickers);
        }

        // 四条
        if (sorted.get(0).getValue() == 4) {
            return new HandResult(HandRank.FOUR_OF_A_KIND, cards, kickers);
        }

        // 葫芦
        if (sorted.get(0).getValue() == 3 && sorted.get(1).getValue() == 2) {
            return new HandResult(HandRank.FULL_HOUSE, cards, kickers);
        }

        // 同花
        if (isFlush) {
            return new HandResult(HandRank.FLUSH, cards, values);
        }

        // 顺子
        if (isStraight) {
            return new HandResult(HandRank.STRAIGHT, cards, kickers);
        }

        // 三条
        if (sorted.get(0).getValue() == 3) {
            return new HandResult(HandRank.THREE_OF_A_KIND, cards, kickers);
        }

        // 两对
        if (sorted.get(0).getValue() == 2 && sorted.get(1).getValue() == 2) {
            return new HandResult(HandRank.TWO_PAIR, cards, kickers);
        }

        // 一对
        if (sorted.get(0).getValue() == 2) {
            return new HandResult(HandRank.ONE_PAIR, cards, kickers);
        }

        // 高牌
        return new HandResult(HandRank.HIGH_CARD, cards, values);
    }

    private static boolean isFlush(List<Card> cards) {
        return cards.stream().map(Card::getSuit).distinct().count() == 1;
    }

    private static boolean isStraight(List<Card> cards) {
        List<Integer> values = cards.stream()
                .map(c -> c.getRank().getValue())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // 普通顺子
        boolean normal = true;
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i - 1) - values.get(i) != 1) {
                normal = false;
                break;
            }
        }
        if (normal) return true;

        // A-2-3-4-5 特殊顺子
        if (values.equals(List.of(14, 5, 4, 3, 2))) {
            return true;
        }

        return false;
    }

    /**
     * 从 n 张牌中选 k 张的所有组合
     */
    private static List<List<Card>> getCombinations(List<Card> cards, int k) {
        List<List<Card>> result = new ArrayList<>();
        combine(cards, k, 0, new ArrayList<>(), result);
        return result;
    }

    private static void combine(List<Card> cards, int k, int start,
                                List<Card> current, List<List<Card>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            combine(cards, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}

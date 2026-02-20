package com.poker.game.engine;

import com.poker.common.enums.HandRank;
import com.poker.common.enums.Rank;
import com.poker.common.enums.Suit;
import com.poker.common.model.Card;
import com.poker.common.model.HandResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 牌型评估器测试
 */
class HandEvaluatorTest {

    @Test
    void testRoyalFlush() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.SPADES, Rank.ACE),
            new Card(Suit.SPADES, Rank.KING)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.SPADES, Rank.QUEEN),
            new Card(Suit.SPADES, Rank.JACK),
            new Card(Suit.SPADES, Rank.TEN),
            new Card(Suit.HEARTS, Rank.TWO),
            new Card(Suit.CLUBS, Rank.THREE)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.ROYAL_FLUSH, result.getHandRank());
    }

    @Test
    void testStraightFlush() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.HEARTS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.EIGHT)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.HEARTS, Rank.SEVEN),
            new Card(Suit.HEARTS, Rank.SIX),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.CLUBS, Rank.THREE)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.STRAIGHT_FLUSH, result.getHandRank());
    }

    @Test
    void testFourOfAKind() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.SPADES, Rank.ACE),
            new Card(Suit.HEARTS, Rank.ACE)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.DIAMONDS, Rank.ACE),
            new Card(Suit.CLUBS, Rank.ACE),
            new Card(Suit.HEARTS, Rank.KING),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.CLUBS, Rank.THREE)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.FOUR_OF_A_KIND, result.getHandRank());
    }

    @Test
    void testFullHouse() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.SPADES, Rank.KING),
            new Card(Suit.HEARTS, Rank.KING)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.DIAMONDS, Rank.KING),
            new Card(Suit.CLUBS, Rank.QUEEN),
            new Card(Suit.HEARTS, Rank.QUEEN),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.CLUBS, Rank.THREE)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.FULL_HOUSE, result.getHandRank());
    }

    @Test
    void testFlush() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.HEARTS, Rank.ACE),
            new Card(Suit.HEARTS, Rank.KING)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.HEARTS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.HEARTS, Rank.TWO),
            new Card(Suit.SPADES, Rank.JACK),
            new Card(Suit.CLUBS, Rank.THREE)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.FLUSH, result.getHandRank());
    }

    @Test
    void testStraight() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.SPADES, Rank.TEN),
            new Card(Suit.HEARTS, Rank.NINE)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.DIAMONDS, Rank.EIGHT),
            new Card(Suit.CLUBS, Rank.SEVEN),
            new Card(Suit.HEARTS, Rank.SIX),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.CLUBS, Rank.ACE)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.STRAIGHT, result.getHandRank());
    }

    @Test
    void testThreeOfAKind() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.SPADES, Rank.JACK),
            new Card(Suit.HEARTS, Rank.JACK)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.DIAMONDS, Rank.JACK),
            new Card(Suit.CLUBS, Rank.NINE),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.CLUBS, Rank.ACE)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.THREE_OF_A_KIND, result.getHandRank());
    }

    @Test
    void testTwoPair() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.SPADES, Rank.ACE),
            new Card(Suit.HEARTS, Rank.ACE)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.DIAMONDS, Rank.KING),
            new Card(Suit.CLUBS, Rank.KING),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.CLUBS, Rank.THREE)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.TWO_PAIR, result.getHandRank());
    }

    @Test
    void testOnePair() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.SPADES, Rank.ACE),
            new Card(Suit.HEARTS, Rank.ACE)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.DIAMONDS, Rank.KING),
            new Card(Suit.CLUBS, Rank.QUEEN),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.SPADES, Rank.TWO),
            new Card(Suit.CLUBS, Rank.THREE)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.ONE_PAIR, result.getHandRank());
    }

    @Test
    void testHighCard() {
        List<Card> holeCards = Arrays.asList(
            new Card(Suit.SPADES, Rank.ACE),
            new Card(Suit.HEARTS, Rank.KING)
        );
        List<Card> communityCards = Arrays.asList(
            new Card(Suit.DIAMONDS, Rank.NINE),
            new Card(Suit.CLUBS, Rank.SEVEN),
            new Card(Suit.HEARTS, Rank.FIVE),
            new Card(Suit.SPADES, Rank.THREE),
            new Card(Suit.CLUBS, Rank.TWO)
        );

        HandResult result = HandEvaluator.evaluate(holeCards, communityCards);
        assertEquals(HandRank.HIGH_CARD, result.getHandRank());
    }
}

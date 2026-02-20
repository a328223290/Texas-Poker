package com.poker.game.engine;

import com.poker.common.enums.*;
import com.poker.common.model.Card;
import com.poker.common.model.Deck;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 德州扑克游戏引擎 - 控制单局游戏的完整流程
 */
@Slf4j
@Data
public class GameEngine {

    private final String roomId;
    private final int smallBlind;
    private final int bigBlind;

    private Deck deck;
    private GamePhase phase;
    private List<Card> communityCards;
    private List<PlayerSeat> seats;
    private int dealerIndex;
    private int currentPlayerIndex;
    private int pot;
    private int currentBet;
    private Map<String, List<Card>> playerHoleCards;

    public GameEngine(String roomId, int smallBlind, int bigBlind) {
        this.roomId = roomId;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.deck = new Deck();
        this.phase = GamePhase.WAITING;
        this.communityCards = new ArrayList<>();
        this.seats = new ArrayList<>();
        this.playerHoleCards = new ConcurrentHashMap<>();
        this.pot = 0;
        this.currentBet = 0;
    }

    /**
     * 开始新一局
     */
    public void startNewHand() {
        // 重置牌组
        deck.reset();
        deck.shuffle();

        // 重置公共牌和底池
        communityCards.clear();
        pot = 0;
        currentBet = bigBlind;
        playerHoleCards.clear();

        // 重置玩家状态
        for (PlayerSeat seat : seats) {
            if (seat.isActive()) {
                seat.setStatus(PlayerStatus.PLAYING);
                seat.setCurrentBet(0);
                seat.setTotalBetInHand(0);
            }
        }

        // 移动庄家位置
        dealerIndex = nextActiveIndex(dealerIndex);

        // 收取盲注
        collectBlinds();

        // 发手牌
        dealHoleCards();

        // 进入翻牌前阶段
        phase = GamePhase.PRE_FLOP;

        // 设置当前行动玩家（大盲左侧）
        int bbIndex = nextActiveIndex(nextActiveIndex(dealerIndex));
        currentPlayerIndex = nextActiveIndex(bbIndex);

        log.info("房间 {} 新一局开始，庄家位: {}", roomId, dealerIndex);
    }

    /**
     * 处理玩家操作
     */
    public boolean handleAction(String playerId, PlayerAction action, int amount) {
        PlayerSeat seat = findSeat(playerId);
        if (seat == null || !isCurrentPlayer(playerId)) {
            return false;
        }

        switch (action) {
            case FOLD -> {
                seat.setStatus(PlayerStatus.FOLDED);
            }
            case CHECK -> {
                if (currentBet > seat.getCurrentBet()) {
                    return false; // 不能过牌，需要跟注
                }
            }
            case CALL -> {
                int callAmount = currentBet - seat.getCurrentBet();
                callAmount = Math.min(callAmount, (int) seat.getChips());
                seat.setChips(seat.getChips() - callAmount);
                seat.setCurrentBet(seat.getCurrentBet() + callAmount);
                seat.setTotalBetInHand(seat.getTotalBetInHand() + callAmount);
                pot += callAmount;
                if (seat.getChips() == 0) {
                    seat.setStatus(PlayerStatus.ALL_IN);
                }
            }
            case RAISE -> {
                if (amount < currentBet * 2 && seat.getChips() > amount) {
                    return false; // 加注不够
                }
                int raiseAmount = amount - seat.getCurrentBet();
                raiseAmount = Math.min(raiseAmount, (int) seat.getChips());
                seat.setChips(seat.getChips() - raiseAmount);
                seat.setCurrentBet(seat.getCurrentBet() + raiseAmount);
                seat.setTotalBetInHand(seat.getTotalBetInHand() + raiseAmount);
                pot += raiseAmount;
                currentBet = seat.getCurrentBet();
                if (seat.getChips() == 0) {
                    seat.setStatus(PlayerStatus.ALL_IN);
                }
            }
            case ALL_IN -> {
                int allInAmount = (int) seat.getChips();
                pot += allInAmount;
                seat.setCurrentBet(seat.getCurrentBet() + allInAmount);
                seat.setTotalBetInHand(seat.getTotalBetInHand() + allInAmount);
                seat.setChips(0);
                seat.setStatus(PlayerStatus.ALL_IN);
                if (seat.getCurrentBet() > currentBet) {
                    currentBet = seat.getCurrentBet();
                }
            }
        }

        // 检查是否进入下一阶段
        if (shouldAdvancePhase()) {
            advancePhase();
        } else {
            currentPlayerIndex = nextActiveIndex(currentPlayerIndex);
        }

        return true;
    }

    /**
     * 推进到下一阶段
     */
    private void advancePhase() {
        // 重置当前轮下注
        for (PlayerSeat seat : seats) {
            seat.setCurrentBet(0);
        }
        currentBet = 0;

        switch (phase) {
            case PRE_FLOP -> {
                phase = GamePhase.FLOP;
                communityCards.addAll(deck.deal(3));
            }
            case FLOP -> {
                phase = GamePhase.TURN;
                communityCards.add(deck.deal());
            }
            case TURN -> {
                phase = GamePhase.RIVER;
                communityCards.add(deck.deal());
            }
            case RIVER -> {
                phase = GamePhase.SHOWDOWN;
                return;
            }
            default -> { return; }
        }

        // 从庄家左侧开始行动
        currentPlayerIndex = nextActiveIndex(dealerIndex);
    }

    /**
     * 判断是否所有活跃玩家都已行动且下注一致
     */
    private boolean shouldAdvancePhase() {
        long activePlayers = seats.stream()
                .filter(s -> s.getStatus() == PlayerStatus.PLAYING)
                .count();

        // 只剩一个人，直接结算
        if (activePlayers <= 1) {
            phase = GamePhase.SHOWDOWN;
            return false;
        }

        // 所有 PLAYING 状态的玩家下注是否一致
        return seats.stream()
                .filter(s -> s.getStatus() == PlayerStatus.PLAYING)
                .allMatch(s -> s.getCurrentBet() == currentBet);
    }

    /**
     * 收取盲注
     */
    private void collectBlinds() {
        int sbIndex = nextActiveIndex(dealerIndex);
        int bbIndex = nextActiveIndex(sbIndex);

        PlayerSeat sb = seats.get(sbIndex);
        PlayerSeat bb = seats.get(bbIndex);

        int sbAmount = (int) Math.min(smallBlind, sb.getChips());
        sb.setChips(sb.getChips() - sbAmount);
        sb.setCurrentBet(sbAmount);
        sb.setTotalBetInHand(sbAmount);
        pot += sbAmount;

        int bbAmount = (int) Math.min(bigBlind, bb.getChips());
        bb.setChips(bb.getChips() - bbAmount);
        bb.setCurrentBet(bbAmount);
        bb.setTotalBetInHand(bbAmount);
        pot += bbAmount;
    }

    /**
     * 发手牌（每人2张）
     */
    private void dealHoleCards() {
        for (PlayerSeat seat : seats) {
            if (seat.isActive()) {
                List<Card> holeCards = deck.deal(2);
                playerHoleCards.put(seat.getPlayerId(), holeCards);
            }
        }
    }

    /**
     * 查找下一个活跃玩家
     */
    private int nextActiveIndex(int fromIndex) {
        int idx = (fromIndex + 1) % seats.size();
        while (idx != fromIndex) {
            PlayerSeat seat = seats.get(idx);
            if (seat.isActive() && seat.getStatus() == PlayerStatus.PLAYING) {
                return idx;
            }
            idx = (idx + 1) % seats.size();
        }
        return fromIndex;
    }

    private PlayerSeat findSeat(String playerId) {
        return seats.stream()
                .filter(s -> s.getPlayerId().equals(playerId))
                .findFirst()
                .orElse(null);
    }

    private boolean isCurrentPlayer(String playerId) {
        if (currentPlayerIndex < 0 || currentPlayerIndex >= seats.size()) return false;
        return seats.get(currentPlayerIndex).getPlayerId().equals(playerId);
    }

    /**
     * 获取存活玩家数量
     */
    public long getActivePlayerCount() {
        return seats.stream()
                .filter(s -> s.getStatus() == PlayerStatus.PLAYING || s.getStatus() == PlayerStatus.ALL_IN)
                .count();
    }
}

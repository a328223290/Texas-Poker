package com.poker.game.engine;

import com.poker.common.enums.PlayerStatus;
import lombok.Data;

/**
 * 玩家座位信息
 */
@Data
public class PlayerSeat {

    private String playerId;
    private String nickname;
    private int seatIndex;
    private long chips;
    private int currentBet;
    private int totalBetInHand;
    private PlayerStatus status;

    public PlayerSeat(String playerId, String nickname, int seatIndex, long chips) {
        this.playerId = playerId;
        this.nickname = nickname;
        this.seatIndex = seatIndex;
        this.chips = chips;
        this.currentBet = 0;
        this.totalBetInHand = 0;
        this.status = PlayerStatus.WAITING;
    }

    public boolean isActive() {
        return status != PlayerStatus.DISCONNECTED
                && status != PlayerStatus.FOLDED
                && chips > 0;
    }
}

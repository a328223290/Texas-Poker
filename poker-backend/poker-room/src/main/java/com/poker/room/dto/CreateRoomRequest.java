package com.poker.room.dto;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String name;
    private Integer blindSmall;
    private Integer blindBig;
    private Integer maxPlayers;
    private Integer minBuyIn;
    private Integer maxBuyIn;
}

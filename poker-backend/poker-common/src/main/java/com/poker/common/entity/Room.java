package com.poker.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 房间实体
 */
@Data
@TableName("room")
public class Room {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 房间类型: NORMAL, TOURNAMENT */
    private String type;

    private Integer blindSmall;

    private Integer blindBig;

    private Integer minBuyIn;

    private Integer maxBuyIn;

    private Integer maxPlayers;

    private Integer currentPlayers;

    /** 房间状态: 0-关闭, 1-等待中, 2-游戏中 */
    private Integer status;

    private Long ownerId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

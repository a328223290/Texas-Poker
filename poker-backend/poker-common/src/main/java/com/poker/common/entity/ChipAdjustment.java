package com.poker.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 筹码调整记录
 */
@Data
@TableName("chip_adjustment")
public class ChipAdjustment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long adminId;

    /** 调整金额（正数增加，负数扣减） */
    private Long amount;

    private Long balanceBefore;

    private Long balanceAfter;

    private String reason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

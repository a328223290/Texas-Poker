package com.poker.admin.dto;

import lombok.Data;

@Data
public class ChipAdjustRequest {
    private Long targetUserId;
    private Long adminUserId;
    /** 调整金额（正数增加，负数扣减） */
    private Long amount;
    /** 调整原因 */
    private String reason;
}

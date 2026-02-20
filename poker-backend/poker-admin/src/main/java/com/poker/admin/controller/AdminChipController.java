package com.poker.admin.controller;

import com.poker.common.model.ApiResult;
import com.poker.admin.dto.ChipAdjustRequest;
import com.poker.admin.service.AdminChipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台 - 筹码管理接口
 */
@RestController
@RequestMapping("/api/admin/chips")
@RequiredArgsConstructor
public class AdminChipController {

    private final AdminChipService adminChipService;

    /** 查看用户筹码余额 */
    @GetMapping("/{userId}")
    public ApiResult<?> getBalance(@PathVariable Long userId) {
        return adminChipService.getBalance(userId);
    }

    /** 调整用户筹码 */
    @PostMapping("/adjust")
    public ApiResult<?> adjustChips(@RequestBody ChipAdjustRequest request) {
        return adminChipService.adjustChips(request);
    }

    /** 筹码变动流水 */
    @GetMapping("/transactions")
    public ApiResult<?> getTransactions(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return adminChipService.getTransactions(userId, page, size);
    }
}

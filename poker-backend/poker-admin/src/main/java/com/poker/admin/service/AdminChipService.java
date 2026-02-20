package com.poker.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poker.admin.dto.ChipAdjustRequest;
import com.poker.admin.mapper.AdminUserMapper;
import com.poker.admin.mapper.ChipAdjustmentMapper;
import com.poker.common.entity.ChipAdjustment;
import com.poker.common.entity.User;
import com.poker.common.model.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminChipService {

    private final AdminUserMapper adminUserMapper;
    private final ChipAdjustmentMapper chipAdjustmentMapper;

    public ApiResult<?> getBalance(Long userId) {
        User user = adminUserMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }
        return ApiResult.success(Map.of(
                "userId", user.getId(),
                "username", user.getUsername(),
                "chips", user.getChips()
        ));
    }

    @Transactional
    public ApiResult<?> adjustChips(ChipAdjustRequest request) {
        User user = adminUserMapper.selectById(request.getTargetUserId());
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }

        long balanceBefore = user.getChips();
        long balanceAfter = balanceBefore + request.getAmount();
        if (balanceAfter < 0) {
            return ApiResult.error(400, "筹码不足，无法扣减");
        }

        // 更新用户余额
        user.setChips(balanceAfter);
        adminUserMapper.updateById(user);

        // 记录调整日志
        ChipAdjustment adjustment = new ChipAdjustment();
        adjustment.setUserId(request.getTargetUserId());
        adjustment.setAdminId(request.getAdminUserId());
        adjustment.setAmount(request.getAmount());
        adjustment.setBalanceBefore(balanceBefore);
        adjustment.setBalanceAfter(balanceAfter);
        adjustment.setReason(request.getReason());
        chipAdjustmentMapper.insert(adjustment);

        return ApiResult.success(Map.of(
                "balanceBefore", balanceBefore,
                "balanceAfter", balanceAfter
        ));
    }

    public ApiResult<?> getTransactions(Long userId, int page, int size) {
        LambdaQueryWrapper<ChipAdjustment> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ChipAdjustment::getUserId, userId);
        }
        wrapper.orderByDesc(ChipAdjustment::getCreatedAt);

        Page<ChipAdjustment> result = chipAdjustmentMapper.selectPage(new Page<>(page, size), wrapper);
        return ApiResult.success(result);
    }
}

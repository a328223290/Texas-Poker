package com.poker.user.controller;

import com.poker.common.entity.User;
import com.poker.common.model.ApiResult;
import com.poker.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final UserMapper userMapper;

    /** 查询余额 */
    @GetMapping("/balance")
    public ApiResult<?> getBalance(@RequestHeader("X-User-Id") Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }
        return ApiResult.success(Map.of("balance", user.getChips()));
    }
}

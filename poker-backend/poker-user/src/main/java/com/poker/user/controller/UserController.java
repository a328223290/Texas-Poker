package com.poker.user.controller;

import com.poker.common.entity.User;
import com.poker.common.model.ApiResult;
import com.poker.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    /** 获取用户信息 */
    @GetMapping("/{userId}")
    public ApiResult<?> getUserProfile(@PathVariable Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", user.getId());
        profile.put("username", user.getUsername());
        profile.put("nickname", user.getNickname());
        profile.put("avatar", user.getAvatar());
        profile.put("chips", user.getChips());
        profile.put("totalGames", user.getTotalGames());
        profile.put("totalWins", user.getTotalWins());

        return ApiResult.success(profile);
    }

    /** 更新用户资料 */
    @PutMapping("/{userId}")
    public ApiResult<?> updateProfile(@PathVariable Long userId,
                                      @RequestBody Map<String, String> body) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }

        if (body.containsKey("nickname")) {
            user.setNickname(body.get("nickname"));
        }
        if (body.containsKey("avatarUrl")) {
            user.setAvatar(body.get("avatarUrl"));
        }

        userMapper.updateById(user);
        return ApiResult.success();
    }
}

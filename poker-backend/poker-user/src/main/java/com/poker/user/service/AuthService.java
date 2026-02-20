package com.poker.user.service;

import com.poker.common.entity.User;
import com.poker.common.model.ApiResult;
import com.poker.user.dto.LoginRequest;
import com.poker.user.dto.RegisterRequest;
import com.poker.user.mapper.UserMapper;
import com.poker.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ApiResult<?> register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(request.getUsername());
        if (existing != null) {
            return ApiResult.error(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setChips(10000L); // 初始筹码
        user.setStatus("ACTIVE");
        user.setTotalGames(0);
        user.setTotalWins(0);

        userMapper.insert(user);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("token", jwtUtil.generateToken(user.getId(), user.getUsername()));
        return ApiResult.success(data);
    }

    public ApiResult<Map<String, Object>> login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            return ApiResult.error(401, "用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ApiResult.error(401, "用户名或密码错误");
        }

        if ("BANNED".equals(user.getStatus())) {
            return ApiResult.error(403, "账号已被封禁");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("token", jwtUtil.generateToken(user.getId(), user.getUsername()));

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("nickname", user.getNickname());
        userInfo.put("chips", user.getChips());
        userInfo.put("avatar", user.getAvatar());
        data.put("userInfo", userInfo);

        return ApiResult.success(data);
    }
}

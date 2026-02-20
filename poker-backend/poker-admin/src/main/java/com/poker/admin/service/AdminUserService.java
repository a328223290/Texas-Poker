package com.poker.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poker.common.entity.User;
import com.poker.common.model.ApiResult;
import com.poker.admin.mapper.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    public ApiResult<?> listUsers(int page, int size, String username, String status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(User::getUsername, username);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> result = adminUserMapper.selectPage(new Page<>(page, size), wrapper);
        return ApiResult.success(result);
    }

    public ApiResult<?> getUserDetail(Long id) {
        User user = adminUserMapper.selectById(id);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }
        user.setPasswordHash(null); // 不返回密码
        return ApiResult.success(user);
    }

    public ApiResult<?> updateStatus(Long id, String status) {
        User user = adminUserMapper.selectById(id);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }
        user.setStatus(status);
        adminUserMapper.updateById(user);
        return ApiResult.success();
    }

    public ApiResult<?> resetPassword(Long id) {
        User user = adminUserMapper.selectById(id);
        if (user == null) {
            return ApiResult.error(404, "用户不存在");
        }
        user.setPasswordHash(passwordEncoder.encode("123456"));
        adminUserMapper.updateById(user);
        return ApiResult.success();
    }

    public ApiResult<?> setUserRole(Long id, Long roleId) {
        // TODO: 更新 user_role 关联表
        return ApiResult.success();
    }
}

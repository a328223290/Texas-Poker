package com.poker.admin.controller;

import com.poker.common.model.ApiResult;
import com.poker.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台 - 用户管理接口
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /** 用户列表（分页） */
    @GetMapping
    public ApiResult<?> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {
        return adminUserService.listUsers(page, size, username, status);
    }

    /** 用户详情 */
    @GetMapping("/{id}")
    public ApiResult<?> getUserDetail(@PathVariable Long id) {
        return adminUserService.getUserDetail(id);
    }

    /** 修改用户状态（启用/禁用/封禁） */
    @PutMapping("/{id}/status")
    public ApiResult<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return adminUserService.updateStatus(id, body.get("status"));
    }

    /** 重置用户密码 */
    @PutMapping("/{id}/password")
    public ApiResult<?> resetPassword(@PathVariable Long id) {
        return adminUserService.resetPassword(id);
    }

    /** 设置用户角色 */
    @PutMapping("/{id}/role")
    public ApiResult<?> setUserRole(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        return adminUserService.setUserRole(id, body.get("roleId"));
    }
}

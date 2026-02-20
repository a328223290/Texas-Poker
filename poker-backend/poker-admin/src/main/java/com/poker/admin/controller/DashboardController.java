package com.poker.admin.controller;

import com.poker.common.model.ApiResult;
import com.poker.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台 - 仪表盘数据接口
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /** 实时概览（在线人数、活跃房间数、筹码流通量） */
    @GetMapping("/overview")
    public ApiResult<?> getOverview() {
        return dashboardService.getOverview();
    }

    /** 用户统计趋势 */
    @GetMapping("/user-stats")
    public ApiResult<?> getUserStats(@RequestParam(defaultValue = "7") int days) {
        return dashboardService.getUserStats(days);
    }

    /** 游戏统计趋势 */
    @GetMapping("/game-stats")
    public ApiResult<?> getGameStats(@RequestParam(defaultValue = "7") int days) {
        return dashboardService.getGameStats(days);
    }
}

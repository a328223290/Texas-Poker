package com.poker.admin.service;

import com.poker.common.model.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StringRedisTemplate redisTemplate;

    public ApiResult<?> getOverview() {
        Map<String, Object> overview = new HashMap<>();
        // TODO: 从 Redis 获取实时在线人数
        Long onlineCount = redisTemplate.opsForSet().size("online_users");
        overview.put("onlineUsers", onlineCount != null ? onlineCount : 0);
        overview.put("activeRooms", 0);   // TODO: 从 Redis 获取
        overview.put("totalChips", 0);    // TODO: 从数据库汇总
        return ApiResult.success(overview);
    }

    public ApiResult<?> getUserStats(int days) {
        // TODO: 按天统计新增用户数和活跃用户数
        return ApiResult.success(Map.of());
    }

    public ApiResult<?> getGameStats(int days) {
        // TODO: 按天统计牌局数和平均时长
        return ApiResult.success(Map.of());
    }
}

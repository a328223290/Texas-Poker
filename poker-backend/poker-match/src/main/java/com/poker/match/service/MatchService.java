package com.poker.match.service;

import com.poker.common.model.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchService {

    private final StringRedisTemplate redisTemplate;

    private static final String MATCH_QUEUE_KEY = "match:queue";

    public ApiResult<?> quickMatch(Long userId) {
        // 将用户加入匹配队列
        redisTemplate.opsForList().rightPush(MATCH_QUEUE_KEY, userId.toString());
        log.info("用户 {} 加入匹配队列", userId);

        // TODO: 实现匹配逻辑，检查队列中是否有足够的玩家组成一桌
        // 暂时返回等待状态
        return ApiResult.success(Map.of("status", "MATCHING"));
    }

    public ApiResult<?> cancelMatch(Long userId) {
        // 从匹配队列中移除用户
        redisTemplate.opsForList().remove(MATCH_QUEUE_KEY, 1, userId.toString());
        log.info("用户 {} 取消匹配", userId);

        return ApiResult.success(Map.of("status", "CANCELLED"));
    }
}

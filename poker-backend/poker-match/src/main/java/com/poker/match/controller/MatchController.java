package com.poker.match.controller;

import com.poker.common.model.ApiResult;
import com.poker.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /** 快速匹配 */
    @PostMapping("/quick")
    public ApiResult<?> quickMatch(@RequestHeader("X-User-Id") Long userId) {
        return matchService.quickMatch(userId);
    }

    /** 取消匹配 */
    @PostMapping("/cancel")
    public ApiResult<?> cancelMatch(@RequestHeader("X-User-Id") Long userId) {
        return matchService.cancelMatch(userId);
    }
}

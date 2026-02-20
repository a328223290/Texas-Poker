package com.poker.room.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.poker.common.entity.Room;
import com.poker.common.model.ApiResult;
import com.poker.room.dto.CreateRoomRequest;
import com.poker.room.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomMapper roomMapper;
    private final StringRedisTemplate redisTemplate;

    public ApiResult<?> createRoom(CreateRoomRequest request, Long userId) {
        Room room = new Room();
        room.setName(request.getName());
        room.setType("NORMAL");
        room.setBlindSmall(request.getBlindSmall() != null ? request.getBlindSmall() : 10);
        room.setBlindBig(request.getBlindBig() != null ? request.getBlindBig() : 20);
        room.setMaxPlayers(request.getMaxPlayers() != null ? request.getMaxPlayers() : 6);
        room.setMinBuyIn(request.getMinBuyIn());
        room.setMaxBuyIn(request.getMaxBuyIn());
        room.setCurrentPlayers(0);
        room.setStatus(1); // 1-等待中
        room.setOwnerId(userId);

        roomMapper.insert(room);

        return ApiResult.success(Map.of("roomId", room.getId()));
    }

    public ApiResult<?> getRooms(int page, int size) {
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(Room::getStatus, 0) // 0-关闭
               .orderByDesc(Room::getCreatedAt);

        Page<Room> result = roomMapper.selectPage(new Page<>(page, size), wrapper);

        List<Map<String, Object>> rooms = result.getRecords().stream().map(room -> {
            Map<String, Object> map = new HashMap<>();
            map.put("roomId", room.getId());
            map.put("name", room.getName());
            map.put("blindSmall", room.getBlindSmall());
            map.put("blindBig", room.getBlindBig());
            map.put("maxPlayers", room.getMaxPlayers());
            map.put("status", room.getStatus());
            map.put("currentPlayers", room.getCurrentPlayers() != null ? room.getCurrentPlayers() : 0);
            return map;
        }).toList();

        return ApiResult.success(Map.of(
                "total", result.getTotal(),
                "rooms", rooms
        ));
    }

    public ApiResult<?> getRoomDetail(Long roomId) {
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            return ApiResult.error(404, "房间不存在");
        }
        return ApiResult.success(room);
    }
}

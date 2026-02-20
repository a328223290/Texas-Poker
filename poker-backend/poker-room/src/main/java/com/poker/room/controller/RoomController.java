package com.poker.room.controller;

import com.poker.common.model.ApiResult;
import com.poker.room.dto.CreateRoomRequest;
import com.poker.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /** 创建房间 */
    @PostMapping("/room/create")
    public ApiResult<?> createRoom(@RequestBody CreateRoomRequest request,
                                   @RequestHeader("X-User-Id") Long userId) {
        return roomService.createRoom(request, userId);
    }

    /** 房间列表 */
    @GetMapping("/lobby/rooms")
    public ApiResult<?> getRooms(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return roomService.getRooms(page, size);
    }

    /** 房间详情 */
    @GetMapping("/room/{roomId}")
    public ApiResult<?> getRoomDetail(@PathVariable Long roomId) {
        return roomService.getRoomDetail(roomId);
    }
}

package com.poker.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限实体
 */
@Data
@TableName("admin_permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 权限标识: user:view, user:edit, chip:adjust 等 */
    private String permissionKey;

    private String permissionName;

    private String description;

    /** 所属模块: user, room, chip, game, announcement */
    private String module;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

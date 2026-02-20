-- =============================================
-- 德州扑克 - 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS poker DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE poker;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL,
    `username` VARCHAR(50) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `nickname` VARCHAR(50) DEFAULT NULL,
    `avatar_url` VARCHAR(255) DEFAULT NULL,
    `chips` BIGINT NOT NULL DEFAULT 10000,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `total_games` INT NOT NULL DEFAULT 0,
    `total_wins` INT NOT NULL DEFAULT 0,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 房间表
CREATE TABLE IF NOT EXISTS `room` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `type` VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    `blind_small` INT NOT NULL,
    `blind_big` INT NOT NULL,
    `max_players` INT NOT NULL DEFAULT 9,
    `min_buy_in` INT DEFAULT NULL,
    `max_buy_in` INT DEFAULT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    `created_by` BIGINT DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 牌局记录表
CREATE TABLE IF NOT EXISTS `game_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `room_id` BIGINT NOT NULL,
    `game_number` INT NOT NULL,
    `players_data` JSON DEFAULT NULL,
    `community_cards` VARCHAR(50) DEFAULT NULL,
    `pot_size` BIGINT DEFAULT 0,
    `winner_ids` JSON DEFAULT NULL,
    `result` JSON DEFAULT NULL,
    `started_at` DATETIME DEFAULT NULL,
    `ended_at` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_room_game` (`room_id`, `game_number`),
    KEY `idx_started_at` (`started_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 筹码交易流水表
CREATE TABLE IF NOT EXISTS `chip_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    `amount` BIGINT NOT NULL,
    `balance_after` BIGINT NOT NULL,
    `reference_id` BIGINT DEFAULT NULL,
    `description` VARCHAR(255) DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_time` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 后台管理相关表
-- =============================================

-- 角色表
CREATE TABLE IF NOT EXISTS `admin_role` (
    `id` BIGINT NOT NULL,
    `role_name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 权限表
CREATE TABLE IF NOT EXISTS `admin_permission` (
    `id` BIGINT NOT NULL,
    `permission_key` VARCHAR(100) NOT NULL,
    `description` VARCHAR(255) DEFAULT NULL,
    `module` VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_key` (`permission_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 角色-权限关联表
CREATE TABLE IF NOT EXISTS `admin_role_permission` (
    `role_id` BIGINT NOT NULL,
    `permission_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 筹码调整记录表
CREATE TABLE IF NOT EXISTS `chip_adjustment` (
    `id` BIGINT NOT NULL,
    `target_user_id` BIGINT NOT NULL,
    `admin_user_id` BIGINT NOT NULL,
    `amount` BIGINT NOT NULL,
    `balance_before` BIGINT NOT NULL,
    `balance_after` BIGINT NOT NULL,
    `reason` VARCHAR(255) DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_target_user` (`target_user_id`),
    KEY `idx_admin_user` (`admin_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 公告表
CREATE TABLE IF NOT EXISTS `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT NOT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    `created_by` BIGINT DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 管理员操作日志表
CREATE TABLE IF NOT EXISTS `admin_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `admin_user_id` BIGINT NOT NULL,
    `operation` VARCHAR(100) NOT NULL,
    `target_type` VARCHAR(50) DEFAULT NULL,
    `target_id` BIGINT DEFAULT NULL,
    `detail` JSON DEFAULT NULL,
    `ip_address` VARCHAR(50) DEFAULT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_admin_time` (`admin_user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 初始化数据
-- =============================================

-- 预设角色
INSERT INTO `admin_role` (`id`, `role_name`, `description`) VALUES
(1, 'SUPER_ADMIN', '超级管理员'),
(2, 'ADMIN', '普通管理员'),
(3, 'OPERATOR', '运营人员'),
(4, 'PLAYER', '普通玩家')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- 预设权限
INSERT INTO `admin_permission` (`id`, `permission_key`, `description`, `module`) VALUES
(1, 'user:view', '查看用户', 'user'),
(2, 'user:edit', '编辑用户', 'user'),
(3, 'user:ban', '封禁用户', 'user'),
(4, 'role:view', '查看角色', 'role'),
(5, 'role:edit', '编辑角色', 'role'),
(6, 'chip:view', '查看筹码', 'chip'),
(7, 'chip:adjust', '调整筹码', 'chip'),
(8, 'room:view', '查看房间', 'room'),
(9, 'room:manage', '管理房间', 'room'),
(10, 'game:view', '查看牌局', 'game'),
(11, 'announcement:view', '查看公告', 'announcement'),
(12, 'announcement:edit', '编辑公告', 'announcement'),
(13, 'dashboard:view', '查看仪表盘', 'dashboard')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- 超级管理员拥有全部权限
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`)
SELECT 1, `id` FROM `admin_permission`
ON DUPLICATE KEY UPDATE `role_id` = `role_id`;

-- 默认管理员账号（密码: admin123）
INSERT INTO `user` (`id`, `username`, `password_hash`, `nickname`, `chips`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 0, 'ACTIVE')
ON DUPLICATE KEY UPDATE `username` = VALUES(`username`);

INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1)
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);

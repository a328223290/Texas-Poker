-- 德州扑克数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS poker DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE poker;

-- ========== 用户相关表 ==========

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `chips` BIGINT NOT NULL DEFAULT 10000 COMMENT '筹码余额',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `total_games` INT NOT NULL DEFAULT 0 COMMENT '总场次',
    `total_wins` INT NOT NULL DEFAULT 0 COMMENT '胜利场次',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ========== 房间相关表 ==========

-- 房间表
CREATE TABLE IF NOT EXISTS `room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '房间名称',
    `type` VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '房间类型: NORMAL-普通, TOURNAMENT-锦标赛',
    `blind_small` BIGINT NOT NULL DEFAULT 10 COMMENT '小盲注',
    `blind_big` BIGINT NOT NULL DEFAULT 20 COMMENT '大盲注',
    `min_buy_in` BIGINT NOT NULL DEFAULT 200 COMMENT '最小带入',
    `max_buy_in` BIGINT NOT NULL DEFAULT 2000 COMMENT '最大带入',
    `max_players` INT NOT NULL DEFAULT 9 COMMENT '最大玩家数',
    `current_players` INT NOT NULL DEFAULT 0 COMMENT '当前玩家数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-关闭, 1-等待中, 2-游戏中',
    `owner_id` BIGINT COMMENT '房主ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

-- ========== 游戏记录表 ==========

-- 牌局记录表
CREATE TABLE IF NOT EXISTS `game_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `room_id` BIGINT NOT NULL COMMENT '房间ID',
    `hand_number` INT NOT NULL COMMENT '本房间第几手牌',
    `community_cards` VARCHAR(50) COMMENT '公共牌 (格式: Ah,Kd,Qc,Js,Th)',
    `pot_total` BIGINT NOT NULL DEFAULT 0 COMMENT '底池总额',
    `winner_ids` VARCHAR(255) COMMENT '赢家ID列表',
    `game_data` JSON COMMENT '完整牌局数据',
    `started_at` DATETIME NOT NULL COMMENT '开始时间',
    `ended_at` DATETIME COMMENT '结束时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_room_id` (`room_id`),
    INDEX `idx_started_at` (`started_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='牌局记录表';

-- 玩家牌局详情表
CREATE TABLE IF NOT EXISTS `game_player_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `game_record_id` BIGINT NOT NULL COMMENT '牌局记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `seat_number` INT NOT NULL COMMENT '座位号',
    `hole_cards` VARCHAR(20) COMMENT '手牌 (格式: Ah,Kd)',
    `final_hand` VARCHAR(50) COMMENT '最终牌型',
    `hand_rank` VARCHAR(30) COMMENT '牌型名称',
    `bet_total` BIGINT NOT NULL DEFAULT 0 COMMENT '总下注额',
    `win_amount` BIGINT NOT NULL DEFAULT 0 COMMENT '赢得金额',
    `actions` JSON COMMENT '操作记录',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_game_record_id` (`game_record_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家牌局详情表';

-- ========== 筹码相关表 ==========

-- 筹码交易记录表
CREATE TABLE IF NOT EXISTS `chip_transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` VARCHAR(30) NOT NULL COMMENT '类型: WIN-赢得, LOSE-输掉, BUY_IN-买入, CASH_OUT-兑出, ADMIN_ADJUST-管理员调整',
    `amount` BIGINT NOT NULL COMMENT '金额(正负)',
    `balance_before` BIGINT NOT NULL COMMENT '交易前余额',
    `balance_after` BIGINT NOT NULL COMMENT '交易后余额',
    `reference_id` BIGINT COMMENT '关联ID (牌局ID/调整ID等)',
    `remark` VARCHAR(255) COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='筹码交易记录表';

-- 筹码调整记录表 (管理员操作)
CREATE TABLE IF NOT EXISTS `chip_adjustment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '被调整用户ID',
    `admin_id` BIGINT NOT NULL COMMENT '操作管理员ID',
    `amount` BIGINT NOT NULL COMMENT '调整金额(正负)',
    `balance_before` BIGINT NOT NULL COMMENT '调整前余额',
    `balance_after` BIGINT NOT NULL COMMENT '调整后余额',
    `reason` VARCHAR(255) NOT NULL COMMENT '调整原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_admin_id` (`admin_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='筹码调整记录表';

-- ========== 管理后台相关表 ==========

-- 角色表
CREATE TABLE IF NOT EXISTS `admin_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_name` VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    `description` VARCHAR(255) COMMENT '角色描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `admin_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `permission_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '权限标识',
    `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `module` VARCHAR(50) NOT NULL COMMENT '所属模块',
    `description` VARCHAR(255) COMMENT '权限描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `admin_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL,
    `permission_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 公告表
CREATE TABLE IF NOT EXISTS `announcement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `type` VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '类型: NORMAL-普通, IMPORTANT-重要, URGENT-紧急',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下线, 1-上线',
    `start_time` DATETIME COMMENT '生效开始时间',
    `end_time` DATETIME COMMENT '生效结束时间',
    `created_by` BIGINT NOT NULL COMMENT '创建人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 管理员操作日志表
CREATE TABLE IF NOT EXISTS `admin_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `admin_id` BIGINT NOT NULL COMMENT '管理员ID',
    `module` VARCHAR(50) NOT NULL COMMENT '操作模块',
    `action` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `target_type` VARCHAR(50) COMMENT '操作对象类型',
    `target_id` BIGINT COMMENT '操作对象ID',
    `detail` JSON COMMENT '操作详情',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_admin_id` (`admin_id`),
    INDEX `idx_module` (`module`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作日志表';

-- ========== 初始化数据 ==========

-- 初始化超级管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO `user` (`username`, `password_hash`, `nickname`, `chips`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsNxiS4yjAF.', '超级管理员', 999999999, 1)
ON DUPLICATE KEY UPDATE `nickname` = '超级管理员';

-- 初始化角色
INSERT INTO `admin_role` (`id`, `role_name`, `description`) VALUES
(1, 'SUPER_ADMIN', '超级管理员'),
(2, 'ADMIN', '普通管理员'),
(3, 'OPERATOR', '运营人员'),
(4, 'VIEWER', '只读用户')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- 初始化权限
INSERT INTO `admin_permission` (`permission_key`, `permission_name`, `module`, `description`) VALUES
('user:view', '查看用户', 'user', '查看用户列表和详情'),
('user:edit', '编辑用户', 'user', '修改用户信息'),
('user:ban', '封禁用户', 'user', '封禁/解封用户'),
('chip:view', '查看筹码', 'chip', '查看筹码余额和流水'),
('chip:adjust', '调整筹码', 'chip', '调整用户筹码余额'),
('room:view', '查看房间', 'room', '查看房间列表'),
('room:manage', '管理房间', 'room', '创建/关闭房间'),
('game:view', '查看牌局', 'game', '查看牌局记录'),
('announcement:view', '查看公告', 'announcement', '查看公告列表'),
('announcement:manage', '管理公告', 'announcement', '发布/编辑/删除公告'),
('log:view', '查看日志', 'log', '查看操作日志'),
('role:manage', '管理角色', 'role', '管理角色和权限')
ON DUPLICATE KEY UPDATE `permission_name` = VALUES(`permission_name`);

-- 为超级管理员分配角色
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
(1, 1)
ON DUPLICATE KEY UPDATE `role_id` = 1;

-- 为超级管理员角色分配所有权限
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`)
SELECT 1, `id` FROM `admin_permission`
ON DUPLICATE KEY UPDATE `role_id` = 1;

-- 创建测试用户
INSERT INTO `user` (`username`, `password_hash`, `nickname`, `chips`, `status`) VALUES
('player1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsNxiS4yjAF.', '玩家一', 10000, 1),
('player2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsNxiS4yjAF.', '玩家二', 10000, 1),
('player3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsNxiS4yjAF.', '玩家三', 10000, 1)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`);

-- 创建测试房间
INSERT INTO `room` (`name`, `type`, `blind_small`, `blind_big`, `min_buy_in`, `max_buy_in`, `max_players`, `status`) VALUES
('新手练习场', 'NORMAL', 10, 20, 200, 2000, 9, 1),
('中级对战房', 'NORMAL', 50, 100, 1000, 10000, 9, 1),
('高手竞技场', 'NORMAL', 100, 200, 2000, 20000, 6, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

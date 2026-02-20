# Texas Poker - 德州扑克

一个完整的德州扑克联网对战游戏，包含游戏前端、管理后台和后端微服务。

## 技术栈

### 前端
- **游戏前端**: React 18 + TypeScript + Vite + PixiJS + GSAP
- **管理后台**: React 18 + TypeScript + Vite + Ant Design

### 后端
- **框架**: Spring Boot 3.x + Spring Cloud Alibaba
- **数据库**: MySQL 8.0 + Redis 7
- **服务注册**: Nacos

## 项目结构

```
texas-poker/
├── poker-game-web/        # 游戏前端（端口 3000）
├── poker-admin-web/       # 管理后台（端口 3001）
├── poker-backend/         # 后端微服务
│   ├── poker-common/      #   公共模块
│   ├── poker-gateway/     #   API 网关 (8080)
│   ├── poker-user/        #   用户服务 (8081)
│   ├── poker-game/        #   游戏服务 (8082)
│   ├── poker-room/        #   房间服务 (8083)
│   ├── poker-match/       #   匹配服务 (8084)
│   └── poker-admin/       #   管理服务 (8085)
└── docker/                # Docker 配置
```

## 快速启动

### 1. 启动基础设施

```bash
cd docker
docker-compose up -d
```

这会启动：
- MySQL 8.0（端口 3306，用户 root，密码 root123）
- Redis 7（端口 6379）
- Nacos 2.3（端口 8848）

### 2. 启动后端服务

```bash
cd poker-backend

# Windows
gradlew.bat :poker-gateway:bootRun
gradlew.bat :poker-user:bootRun
gradlew.bat :poker-game:bootRun
gradlew.bat :poker-room:bootRun
gradlew.bat :poker-admin:bootRun

# Linux/Mac
./gradlew :poker-gateway:bootRun
./gradlew :poker-user:bootRun
./gradlew :poker-game:bootRun
./gradlew :poker-room:bootRun
./gradlew :poker-admin:bootRun
```

### 3. 启动游戏前端

```bash
cd poker-game-web
npm install
npm run dev
```

访问 http://localhost:3000

### 4. 启动管理后台

```bash
cd poker-admin-web
npm install
npm run dev
```

访问 http://localhost:3001

## 功能说明

### 游戏功能
- 用户注册、登录
- 房间创建、加入、退出
- 快速匹配
- 德州扑克核心玩法（发牌、下注、比牌）
- 实时 WebSocket 通信

### 管理后台功能
- 仪表盘（在线人数、活跃房间、筹码流通量）
- 用户管理（列表、封禁、重置密码）
- 筹码管理（余额调整、流水查询）
- 房间管理
- 牌局记录
- 公告管理

## API 文档

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

### 用户接口
- `GET /api/user/{userId}` - 获取用户信息
- `PUT /api/user/{userId}` - 更新用户资料

### 大厅接口
- `GET /api/lobby/rooms` - 获取房间列表
- `POST /api/room/create` - 创建房间

### WebSocket 事件
- `JOIN_ROOM` - 加入房间
- `LEAVE_ROOM` - 离开房间
- `GAME_START` - 游戏开始
- `DEAL_CARDS` - 发牌
- `PLAYER_ACTION` - 玩家操作
- `GAME_END` - 游戏结束

## 默认账号

管理员账号：admin / admin123

## 开发说明

### 运行测试

```bash
cd poker-backend
./gradlew test
```

### 构建项目

```bash
# 后端
cd poker-backend
./gradlew build

# 前端
cd poker-game-web
npm run build

cd poker-admin-web
npm run build
```

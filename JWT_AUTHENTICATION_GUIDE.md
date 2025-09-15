# JWT认证系统使用指南

## 重新设计的系统架构

本系统采用**API网关统一认证**的架构设计，具有以下特点：

1. **API Gateway作为统一入口** → **处理所有认证逻辑**
2. **BCrypt密码校验** + **JWT生成** + **Redis缓存** → **网关完成**
3. **用户服务专注数据管理** → **不处理认证逻辑**
4. **统一的用户信息透传** → **ThreadLocal机制**
5. **OpenFeign自动透传token** → **服务间调用保持认证状态**

### 新架构优势

✅ **统一入口**：所有认证在网关层面处理，避免绕过认证  
✅ **职责分离**：网关负责认证，用户服务专注数据管理  
✅ **安全性提升**：认证逻辑集中，更容易管控和审计  
✅ **扩展性**：后续添加OAuth2、SSO等认证方式更容易  

## API使用示例

### 1. 用户注册（网关处理）

```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
    "username": "testuser",
    "password": "123456",
    "email": "test@example.com",
    "account": "testuser"
}
```

### 2. 用户登录（网关处理）

```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "123456"
}
```

**响应示例：**
```json
{
    "code": 200,
    "message": "登录成功",
    "data": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIi..."
}
```

### 3. 用户登出（网关处理）

```bash
POST http://localhost:8080/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIi...
```

### 4. 访问用户数据（用户服务）

**获取当前用户信息：**
```bash
GET http://localhost:8080/user/current
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIi...
```

**获取用户基本信息：**
```bash
GET http://localhost:8080/user/baseInfo/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIi...
```

### 5. 访问其他服务

**课程信息：**
```bash
GET http://localhost:8080/course/info/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoidGVzdHVzZXIi...
```

## 系统组件说明

### 1. API网关 (api-gateway) - 认证中心

**关键类：**
- `AuthController` - 提供登录、注册、登出API
- `AuthService` - 认证业务逻辑（BCrypt、JWT、Redis）
- `AuthFilter` - 全局JWT认证过滤器
- `UserRepository` - 响应式用户数据访问

**功能：**
- ✅ **统一认证入口**：处理所有登录、注册、登出请求
- ✅ **BCrypt密码加密**：安全的密码存储和校验
- ✅ **JWT生成与校验**：无状态认证token管理
- ✅ **Redis缓存管理**：`login:user:{userId} → token`
- ✅ **用户信息透传**：提取用户信息并添加到请求头
- ✅ **白名单管理**：免认证路径配置

**数据库配置：**
```yaml
spring:
  r2dbc:
    url: r2dbc:mysql://localhost:3306/db_user
    username: root
    password: 1234
```

### 2. 用户服务 (user-service) - 数据管理

**关键类：**
- `UserController` - 用户数据相关API
- `UserServiceImpl` - 用户数据业务逻辑

**功能：**
- ✅ **用户数据管理**：基本信息、收藏信息等
- ✅ **权限检查**：基于ThreadLocal的用户信息验证
- ✅ **数据更新**：用户信息修改等操作

**不再处理：**
- ❌ 用户登录
- ❌ 用户注册  
- ❌ Token生成
- ❌ 密码校验

### 3. 业务服务（课程、学习、评论）

**关键机制：**
- `UserContextInterceptor` - 将Header中的用户信息存入ThreadLocal
- `UserContextHolder` - ThreadLocal工具类

**使用方式：**
```java
// 在任何业务方法中获取当前用户信息
UserBaseInfoDTO currentUser = UserContextHolder.getUser();
if (currentUser != null) {
    log.info("当前用户: {}", currentUser.getUsername());
    // 执行权限检查、审计日志等
}
```

### 4. OpenFeign配置

**自动token透传：**
```java
@Bean
public RequestInterceptor feignRequestInterceptor() {
    return template -> {
        // 自动从请求上下文获取token和用户信息
        // 透传到下游服务
    };
}
```

## 认证流程图

```
前端请求
    ↓
[API Gateway]
    ↓
1. /auth/** → AuthController (网关处理)
   - 登录：验证密码 → 生成JWT → 存Redis → 返回token
   - 注册：BCrypt加密 → 存数据库
   - 登出：删除Redis中的token

2. 其他请求 → AuthFilter
   - 提取token → 校验JWT → 验证Redis → 解析用户信息
   - 添加用户信息到Header → 转发给业务服务
    ↓
[业务服务]
    ↓
UserContextInterceptor
    ↓
从Header提取用户信息 → 存入ThreadLocal
    ↓
业务逻辑获取用户：UserContextHolder.getUser()
```

## 路由配置

```yaml
spring:
  cloud:
    gateway:
      routes:
        # 认证相关请求由网关直接处理（不转发）
        # /auth/** → AuthController

        # 用户数据请求转发到用户服务
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**

        # 其他业务服务
        - id: course-service
          uri: lb://course-service
          predicates:
            - Path=/course/**
```

## 白名单配置

```java
private static final List<String> WHITE_LIST = Arrays.asList(
    "/auth/login",      // 登录
    "/auth/register",   // 注册  
    "/auth/validate",   // token验证（内部）
    "/user/test",       // 测试接口
    "/actuator/health"  // 健康检查
);
```

## 安全特性

### 1. 集中式认证
- 所有认证逻辑在网关层面处理
- 避免业务服务绕过认证的风险
- 便于统一的安全策略管理

### 2. 密码安全
- BCrypt进行密码哈希，防止明文存储
- 密码加盐防止彩虹表攻击

### 3. Token安全
- JWT使用HMAC-SHA256签名，防止篡改
- Redis存储token映射，支持主动失效
- Token过期时间24小时，可配置

### 4. 传输安全
- 所有认证信息通过HTTP Header传输
- 支持HTTPS部署（推荐生产环境）

## 部署架构

```
Internet
    ↓
Load Balancer
    ↓
[API Gateway Cluster]  ← Redis Cluster
    ↓                 ← MySQL (User DB)
[Microservices]
├── user-service
├── course-service  
├── learning-service
└── comment-service
```

## 扩展功能

### 1. 多种认证方式
网关架构便于集成：
- OAuth2认证
- LDAP认证
- 第三方登录（微信、QQ等）

### 2. 高级安全特性
- IP白名单/黑名单
- 请求频率限制
- 异常登录检测
- 设备指纹识别

### 3. 监控和审计
- 统一的认证日志
- 登录行为分析
- 安全事件告警

## 常见问题

### Q: 为什么要将认证逻辑从用户服务移到网关？
A: 
1. **统一入口**：所有请求都经过网关，避免绕过认证
2. **职责分离**：网关负责认证，业务服务专注业务逻辑
3. **安全性**：认证逻辑集中，更容易管控
4. **扩展性**：后续添加其他认证方式更容易

### Q: 网关如何访问用户数据库？
A: 网关使用R2DBC响应式数据库访问，配置独立的数据库连接。

### Q: 用户服务还需要密码校验功能吗？
A: 不需要。用户服务现在专注于用户数据的CRUD操作，认证由网关统一处理。

### Q: 如何保证网关的高可用？
A: 
1. 网关集群部署
2. Redis集群保证缓存高可用
3. 数据库主从复制
4. 负载均衡器

## 迁移指南

从旧架构迁移到新架构：

1. **更新前端调用**：
   - 登录：`POST /user/login` → `POST /auth/login`
   - 注册：`POST /user/register` → `POST /auth/register`  
   - 登出：`POST /user/logout` → `POST /auth/logout`

2. **用户服务简化**：
   - 移除登录注册相关代码
   - 专注于用户数据管理

3. **网关配置**：
   - 添加数据库连接配置
   - 配置认证相关Bean

4. **测试验证**：
   - 认证流程测试
   - 用户信息透传测试
   - 权限控制测试 
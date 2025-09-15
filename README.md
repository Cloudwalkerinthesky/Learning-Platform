# CoursePlatform2

基于Spring Cloud Alibaba的在线课程平台

## 项目介绍

这是一个基于微服务架构的在线课程平台，采用Spring Cloud Alibaba技术栈开发。

### 技术栈

- 微服务框架：Spring Cloud Alibaba
- 服务注册与发现：Nacos
- 网关：Spring Cloud Gateway
- 数据库：MySQL
- 缓存：Redis
- 消息队列：RabbitMQ
- 服务间通信：OpenFeign
- 负载均衡：Spring Cloud LoadBalancer

### 项目结构

- api-gateway：API网关服务
- user-service：用户服务
- course-service：课程服务
- learning-service：学习服务
- comment-service：评论服务
- common：公共模块

### 主要功能

- 用户管理：注册、登录、权限控制
- **JWT认证系统：基于JWT + Redis的分布式认证方案**
- 课程管理：课程发布、编辑、分类
- 学习管理：学习进度跟踪、课程收藏
- 评论系统：课程评论、互动交流
- 实时通知：学习提醒、系统通知

## JWT认证系统

本项目实现了**API网关统一认证**的架构设计，支持：

### 核心特性
- ✅ **网关统一认证** - 所有认证逻辑在API Gateway处理
- ✅ **BCrypt密码加密** - 安全的密码存储
- ✅ **JWT Token生成与校验** - 无状态认证
- ✅ **Redis Token缓存** - 支持主动失效和防伪
- ✅ **用户信息透传** - 业务服务无感知获取用户信息
- ✅ **OpenFeign自动透传** - 服务间调用保持认证状态

### 新架构优势
- 🎯 **统一入口** - 避免绕过认证的安全风险
- 🔧 **职责分离** - 网关负责认证，业务服务专注数据管理
- 🛡️ **安全性提升** - 认证逻辑集中，更容易管控和审计
- 🚀 **扩展性强** - 后续添加OAuth2、SSO等认证方式更容易

### 认证流程
```
前端请求 → API Gateway
    ↓
1. /auth/** → 网关直接处理（登录/注册/登出）
2. 其他请求 → AuthFilter校验 → 用户信息透传 → 业务服务
    ↓
业务服务通过ThreadLocal获取用户信息：UserContextHolder.getUser()
```

### 使用示例

**登录获取token（网关处理）：**
```bash
POST /auth/login
{
    "username": "testuser",
    "password": "123456"
}
```

**访问用户数据（转发到用户服务）：**
```bash
GET /user/current
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**访问其他业务服务：**
```bash
GET /course/info/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**业务代码中获取当前用户：**
```java
UserBaseInfoDTO currentUser = UserContextHolder.getUser();
log.info("当前用户: {}", currentUser.getUsername());
```

详细文档请参考：[JWT认证系统使用指南](JWT_AUTHENTICATION_GUIDE.md)

## 快速开始

### 环境要求

- JDK 17
- Maven 3.8+
- MySQL 8.0
- Redis
- RabbitMQ
- Nacos

### 本地运行

1. 克隆项目
```bash
git clone https://github.com/你的用户名/CoursePlatform2.git
```

2. 启动Nacos服务

3. 启动Redis服务

4. 启动RabbitMQ服务

5. 启动各个微服务
```bash
# 启动用户服务
cd user-service
mvn spring-boot:run

# 启动课程服务
cd course-service
mvn spring-boot:run

# 启动学习服务
cd learning-service
mvn spring-boot:run

# 启动评论服务
cd comment-service
mvn spring-boot:run

# 启动API网关
cd api-gateway
mvn spring-boot:run
```

## 项目特点

- 微服务架构设计
- 高并发处理
- 安全性设计
- 性能优化
- 代码质量保证
- 可扩展性设计

## 贡献指南

欢迎提交Issue和Pull Request

## 许可证

[MIT License](LICENSE) 
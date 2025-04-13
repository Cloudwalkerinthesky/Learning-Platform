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
- 课程管理：课程发布、编辑、分类
- 学习管理：学习进度跟踪、课程收藏
- 评论系统：课程评论、互动交流
- 实时通知：学习提醒、系统通知

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
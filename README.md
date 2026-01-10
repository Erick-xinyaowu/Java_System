# 🎯 智能职业规划系统 (Career Planner)

一个基于 **Vue 3 + Spring Boot** 的全栈 Web 应用，集成 **AI 大模型**能力，为用户提供智能简历分析、职业规划建议等服务。

## ✨ 功能特性

### 核心功能
- 🔐 **用户认证** - JWT Token 安全认证，支持注册、登录、权限管理
- 📊 **数据仪表盘** - 可视化展示简历评分、技能分布、成长趋势等数据
- 📝 **智能简历分析** - AI 自动解析简历，提取技能标签，给出优化建议
- 🤖 **AI 职业顾问** - 对话式智能问答，提供个性化职业发展建议
- 👤 **个人中心** - 用户信息管理，支持头像上传、资料编辑
- ⚙️ **管理员控制台** - 系统监控、用户管理（仅管理员可见）

### 技术亮点
- 🎨 现代化 UI 设计：毛玻璃效果、渐变色彩、流畅动画
- 📈 ECharts 数据可视化：交互式图表，响应式布局
- 🌊 SSE 流式响应：AI 回答实时推送，打字机效果
- 🔒 安全机制：密码加密存储、Token 鉴权、CORS 跨域配置

## 🛠️ 技术栈

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.5 | 渐进式 JavaScript 框架 |
| TypeScript | 5.6 | 类型安全的 JavaScript |
| Vite | 6.0 | 下一代前端构建工具 |
| Element Plus | 2.9 | Vue 3 组件库 |
| Vue Router | 4.5 | 官方路由管理器 |
| Pinia | 2.3 | 状态管理库 |
| ECharts | 5.6 | 数据可视化图表库 |
| Axios | 1.7 | HTTP 请求库 |
| Sass | - | CSS 预处理器 |

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.5 | 应用开发框架 |
| MyBatis | 3.0 | ORM 持久层框架 |
| MySQL | 8.0 | 关系型数据库 |
| JWT | - | 身份认证 |
| 通义千问 API | - | AI 大模型服务 |

## 📁 项目结构

```
Java_System/
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/               # API 接口封装
│   │   ├── components/        # 公共组件
│   │   │   ├── layout/        # 布局组件 (Header, Sidebar)
│   │   │   └── ui/            # UI 组件 (BaseCard 等)
│   │   ├── pages/             # 页面组件
│   │   ├── views/             # 视图组件
│   │   ├── router/            # 路由配置
│   │   ├── stores/            # Pinia 状态管理
│   │   ├── styles/            # 全局样式
│   │   └── App.vue            # 根组件
│   ├── package.json
│   └── vite.config.ts
│
├── backend/                    # 后端项目
│   ├── src/main/java/com/erickwu/backend/
│   │   ├── config/            # 配置类 (JWT, CORS, Security)
│   │   ├── controller/        # 控制器层
│   │   ├── service/           # 业务逻辑层
│   │   │   └── impl/          # 服务实现
│   │   ├── mapper/            # MyBatis 数据访问层
│   │   ├── entity/            # 实体类
│   │   ├── dto/               # 数据传输对象
│   │   ├── vo/                # 视图对象
│   │   └── util/              # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml    # 应用配置
│   │   ├── mapper/            # MyBatis XML 映射文件
│   │   └── db/                # 数据库脚本
│   └── pom.xml
│
└── README.md
```

## 🚀 快速开始

### 环境要求

- **Node.js** >= 18.x
- **Java** >= 17
- **MySQL** >= 8.0
- **Maven** >= 3.8 (或使用项目自带的 mvnw)

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/Java_System.git
cd Java_System
```

### 2. 数据库配置

```bash
# 登录 MySQL 创建数据库
mysql -u root -p

# 执行以下 SQL
CREATE DATABASE career_planner DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE career_planner;

# 导入数据表结构（在项目目录下执行）
source backend/src/main/resources/db/schema.sql
source backend/src/main/resources/db/data.sql
source backend/src/main/resources/db/chat_tables.sql
```

### 3. 启动后端服务

```bash
cd backend

# Windows
.\mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

后端服务将运行在 `http://localhost:8080`

### 4. 启动前端服务

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将运行在 `http://localhost:3000`

### 5. 访问系统

打开浏览器访问 `http://localhost:3000`

**测试账号：**
| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin |
| 普通用户 | user | 123456 |

## ⚙️ 配置说明

### 后端配置 (application.yml)

```yaml
# 数据库连接
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/career_planner
    username: root
    password: root

# JWT 配置
jwt:
  secret: your-secret-key
  expiration: 86400000  # 24小时

# AI 大模型配置
llm:
  provider: qwen
  qwen:
    api-key: your-api-key
    model: qwen-turbo
```

### 前端配置 (vite.config.ts)

```typescript
// API 代理配置
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 📸 功能截图

> 登录页面、仪表盘、简历分析、AI 对话等功能截图（待补充）

## 📝 API 接口

### 用户相关
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/update` - 更新用户信息

### 仪表盘
- `GET /api/dashboard/overview` - 获取概览数据
- `GET /api/dashboard/skills` - 获取技能分布
- `GET /api/dashboard/trend` - 获取成长趋势

### 简历分析
- `POST /api/resume/analyze` - 分析简历
- `GET /api/resume/history` - 获取分析历史

### AI 对话
- `POST /api/chat/send` - 发送消息（SSE 流式响应）
- `GET /api/chat/history` - 获取对话历史

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 开源协议

本项目采用 MIT 协议开源，详情请参阅 [LICENSE](LICENSE) 文件。

## 👨‍💻 作者

**ErickWu**

---

⭐ 如果这个项目对你有帮助，请给一个 Star 支持一下！

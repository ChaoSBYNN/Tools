 ---
  CLAUDE.md 是什么？

  放在项目根目录的一个 Markdown 文件，Claude Code 每次在该项目中工作时会自动读取它。作用是告诉 Claude
  你的项目背景、约定和常用操作，减少每次重复说明的成本。

  ---
  核心原则

  - 只写无法从代码本身推导的信息 — 不需要说"使用有意义的变量名"，但需要说"测试在 test/ 目录下，用 pytest 运行"
  - 简洁精确，不要写长篇大论或模板化的开发建议
  - 高价值内容优先：构建/测试命令 → 项目架构 → 代码规范约定 → 特殊注意事项

  ---
  标准模板

  # CLAUDE.md

  This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

  ## Build/Test/Lint 命令
  - 构建: `npm run build`
  - 运行单个测试: `npx jest tests/foo.test.ts --watch`
  - Lint: `npx eslint src/ --fix`
  - Type check: `npx tsc --noEmit`

  ## 项目架构
  - 前端: src/components/ (React), src/pages/ (路由页面)
  - 后端: api/ (Next.js API routes)
  - 数据库: prisma/schema.prisma (PostgreSQL)
  - 数据流: pages → API routes → Prisma → PostgreSQL

  ## 代码规范
  - React 组件使用函数组件 + hooks，不使用类组件
  - API 路由放在 api/ 下按模块分目录
  - 所有数据库查询通过 Prisma，不写原生 SQL
  - 环境变量在 .env.local 中，参考 .env.example

  ---
  各模块应该写什么

  1. 构建/测试/运维命令（最重要）

  Claude 需要知道如何运行你的项目。写刚需命令即可：

  ## Commands
  - 安装: `pip install -r requirements.txt`
  - 本地开发: `uvicorn app.main:app --reload`
  - 测试: `pytest tests/ -v`
  - 单个测试: `pytest tests/test_auth.py::test_login -v`
  - Lint: `ruff check src/`
  - 类型检查: `mypy src/`

  如果是多模块项目，按模块分：

  ## Commands
  ### 后端
  - 运行: `cd backend && poetry run uvicorn ...`
  - 测试: `cd backend && poetry run pytest`
  ### 前端
  - 运行: `cd frontend && pnpm dev`
  - 测试: `cd frontend && pnpm test`

  2. 项目架构（"大图"）

  只写需要读多个文件才能理解的架构设计，而不是列举目录结构：

  ## Architecture
  - 认证流程: middleware (middleware/auth.ts) → JWT 校验 → request.user 注入
  - 支付: frontend → /api/checkout → PaymentService → Stripe API → Webhook 回调
  - 数据同步: 定时任务 (cron/sync.ts) → 拉取第三方 API → 写入本地 DB → 发事件通知

  3. 代码规范与约定

  Claude 无法从代码中读到的"不成文规则"：

  ## Conventions
  - 错误码格式: ERR_MODULE_NAME_001，错误码在 errors/ 下统一管理
  - Git 分支命名: feature/xxx, bugfix/xxx, hotfix/xxx
  - 组件文件命名: PascalCase，工具函数文件: camelCase

  4. 特殊注意事项

  ## Notes
  - 本地开发需要启动 Docker Compose（含 Redis 和 PostgreSQL）
  - 生产环境日志通过 CloudWatch，不写本地文件
  - 此项目依赖另一个私有包（@company/shared），需要先 npm link

  ---
  避免写的内容

  ┌──────────────────────────────────────┬──────────────────────────────────────┐
  │                不要写                │                 原因                 │
  ├──────────────────────────────────────┼──────────────────────────────────────┤
  │ "写干净代码"、"做错误处理"           │ 这是通用开发规范，非项目特有         │
  ├──────────────────────────────────────┼──────────────────────────────────────┤
  │ 完整的目录树（用 tree 命令输出）     │ Claude 可以直接用 Glob/Grep 工具     │
  ├──────────────────────────────────────┼──────────────────────────────────────┤
  │ 每个文件的注释式说明                 │ 文件内部代码就是最好的文档           │
  ---
  避免写的内容

  ┌──────────────────────────────────────┬──────────────────────────────────────┐
  │                不要写                │                 原因                 │
  ├──────────────────────────────────────┼──────────────────────────────────────┤
  │ "写干净代码"、"做错误处理"           │ 这是通用开发规范，非项目特有         │
  ├──────────────────────────────────────┼──────────────────────────────────────┤
  │ 完整的目录树（用 tree 命令输出）     │ Claude 可以直接用 Glob/Grep 工具     │
  ├──────────────────────────────────────┼──────────────────────────────────────┤
  │ 每个文件的注释式说明                 │ 文件内部代码就是最好的文档           │
  ├──────────────────────────────────────┼──────────────────────────────────────┤
  │ API 的完整接口文档                   │ 应该单独放在 docs/ 或 OpenAPI 规范中 │
  ├──────────────────────────────────────┼──────────────────────────────────────┤
  │ 明显的框架用法（"React 组件用 JSX"） │ 从文件后缀和导入就能判断             │
  └──────────────────────────────────────┴──────────────────────────────────────┘

  ---
  自动生成 vs 手动编写

  如果你已经在一个现有项目中，可以直接运行 /init 命令（），Claude 会自动分析项目结构并为当前工作目录生成一份 CLAUDE.md。之后你可以手动调整。

  你也可以先用 /init 生成草稿，再按照上面的原则修剪和补充。

  ---
  已有的 CLAUDE.md 回答了哪三个问题？

  写好之后，检查你的 CLAUDE.md 是否能让一个全新的 Claude 实例快速回答：

  1. 怎么运行和测试代码？ — 构建/测试命令
  2. 代码怎么组织的？ — 架构和数据流
  3. 有什么特殊约定？ — 命名规范、错误处理模式、分支策略

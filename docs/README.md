# 文档开发指南

## 📦 安装依赖

在开始开发文档之前，需要先安装 Node.js 依赖：

```bash
cd docs
npm install
```

## 🚀 开发命令

所有文档相关的命令都需要在 `docs` 目录下执行：

### 启动开发服务器

```bash
cd docs
npm run docs:dev
```

开发服务器将在 `http://localhost:5173` 启动（端口可能不同）。

### 构建文档

```bash
cd docs
npm run docs:build
```

构建产物将输出到 `docs/.vitepress/dist` 目录。

### 预览构建结果

```bash
cd docs
npm run docs:preview
```

> 💡 **提示**: `docs:` 前缀只是 npm script 的命名约定，实际执行的命令是 `vitepress dev/build/preview`，因为 `package.json` 已在 `docs/` 目录，所以不需要额外的路径参数。

## 📁 目录结构

```
docs/
├── package.json              # Node.js 依赖配置
├── package-lock.json         # 依赖锁定文件
├── node_modules/             # 安装的依赖（已加入 .gitignore）
├── .vitepress/               # VitePress 配置
│   ├── config.js             # 文档配置文件
│   ├── dist/                 # 构建产物（已加入 .gitignore）
│   └── cache/                # 缓存文件（已加入 .gitignore）
├── guide/                    # 用户指南
├── tester/                   # 测试人员文档
├── developer/                # 开发者文档
├── faq/                      # 常见问题
└── index.md                  # 文档首页
```

## 🔧 依赖说明

### 开发依赖

- **vitepress** (^1.6.4): 文档框架
- **mermaid** (^11.12.0): 图表渲染库
- **vitepress-plugin-mermaid** (^2.0.17): VitePress 的 Mermaid 插件

## ⚠️ 注意事项

1. **工作目录**: 所有 npm 命令必须在 `docs` 目录下执行
2. **Node.js 版本**: 建议使用 Node.js 14 或更高版本
3. **依赖安装**: 如果 `package.json` 有更新，需要重新运行 `npm install`
4. **缓存清理**: 如果遇到构建问题，可以删除 `docs/.vitepress/cache` 目录后重新构建

## 📝 文档规范

- 使用 Markdown 语法编写
- 支持 Mermaid 图表语法
- 文件扩展名必须为 `.md`
- 使用相对路径链接其他文档

## 🎨 主题定制

VitePress 配置位于 `docs/.vitepress/config.js`，可以自定义：
- 导航栏
- 侧边栏
- 主题颜色
- 插件配置

## 🐛 常见问题

### 1. 找不到 npm 命令

确保已安装 Node.js，可以从 https://nodejs.org 下载安装。

### 2. 依赖安装失败

尝试清除缓存后重新安装：
```bash
cd docs
rm -rf node_modules package-lock.json
npm install
```

### 3. 开发服务器启动失败

检查端口是否被占用，或者尝试：
```bash
cd docs
npm run docs:dev -- --port 3000
```

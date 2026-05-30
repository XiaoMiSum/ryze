# GitHub Actions 更新说明

## 📋 更新概述

由于 `package.json` 和 `package-lock.json` 已从根目录迁移到 `docs/` 目录，GitHub Actions 脚本已相应更新。

## ✅ 已更新的 Workflows

### 1. deploy.yml - 文档部署工作流

**文件路径**: `.github/workflows/deploy.yml`

**主要变更**:

#### 添加了默认工作目录
```yaml
defaults:
  run:
    working-directory: docs
```

这使得所有 `run` 命令默认在 `docs/` 目录下执行。

#### 更新了依赖缓存路径
```yaml
- name: Setup Node
  uses: actions/setup-node@v4
  with:
    node-version: 22
    cache: npm
    cache-dependency-path: docs/package-lock.json  # 新增
```

明确指定了 `package-lock.json` 的路径，确保缓存正确工作。

**工作流程**:
1. ✅ 检出代码
2. ✅ 设置 Node.js 环境（版本 22）
3. ✅ 配置依赖缓存（使用 `docs/package-lock.json`）
4. ✅ 安装依赖（在 `docs/` 目录执行 `npm ci`）
5. ✅ 构建文档（在 `docs/` 目录执行 `npm run docs:build`）
6. ✅ 上传构建产物（`docs/.vitepress/dist`）
7. ✅ 部署到 GitHub Pages

### 2. publish-maven-central.yml - Maven 发布工作流

**文件路径**: `.github/workflows/publish-maven-central.yml`

**状态**: ✅ 无需更新

该工作流仅处理 Java/Maven 相关的发布任务，不受 Node.js 文件迁移影响。

## 🔄 触发条件

### deploy.yml 触发方式

1. **自动触发**: 当向 `master` 分支推送代码时
2. **手动触发**: 通过 GitHub Actions 页面手动运行

### publish-maven-central.yml 触发方式

1. **Release 触发**: 当创建新的 GitHub Release 时
2. **手动触发**: 通过 GitHub Actions 页面手动运行，可指定版本号

## 📊 构建产物

### 文档部署

- **输入**: `docs/` 目录下的 Markdown 文件
- **输出**: `docs/.vitepress/dist/` 目录
- **部署目标**: GitHub Pages
- **访问地址**: https://xiaomisum.github.io/ryze

### Maven 发布

- **输入**: 所有 Java 模块源代码
- **输出**: JAR 文件（包含 sources 和 javadoc）
- **部署目标**: Maven Central Repository
- **访问地址**: https://repo.maven.apache.org/maven2/io/github/xiaomisum/

## ⚙️ 环境要求

### 文档部署

- **Node.js**: 22.x
- **包管理器**: npm
- **依赖文件**: `docs/package.json` 和 `docs/package-lock.json`

### Maven 发布

- **JDK**: 21
- **Maven**: 3.8+
- **GPG**: 用于签名
- **Secrets**: 
  - `GPG_PRIVATE_KEY`
  - `GPG_PASSPHRASE`
  - `MAVEN_USERNAME`
  - `MAVEN_PASSWORD`

## 🐛 故障排查

### 文档构建失败

**可能原因**:
1. `package-lock.json` 路径错误
2. Node.js 版本不兼容
3. 依赖安装失败

**解决方案**:
```bash
# 本地测试构建
cd docs
rm -rf node_modules package-lock.json
npm install
npm run docs:build
```

### 缓存未命中

**检查点**:
- 确认 `cache-dependency-path` 设置为 `docs/package-lock.json`
- 检查 `package-lock.json` 是否已提交到仓库

### 部署路径错误

**确认上传路径**:
```yaml
- name: Upload artifact
  uses: actions/upload-pages-artifact@v3
  with:
    path: docs/.vitepress/dist  # 确保路径正确
```

## 📝 注意事项

1. **工作目录**: 所有 npm 相关命令都在 `docs/` 目录下执行
2. **缓存优化**: 使用 `package-lock.json` 进行依赖缓存，加快构建速度
3. **路径一致**: 确保所有路径引用都指向 `docs/` 目录下的文件
4. **权限配置**: GitHub Pages 部署需要正确的权限设置

## 🔗 相关文档

- [GitHub Actions 官方文档](https://docs.github.com/en/actions)
- [VitePress 部署指南](https://vitepress.dev/guide/deploy)
- [GitHub Pages 文档](https://docs.github.com/en/pages)
- [项目文档开发指南](../docs/README.md)

## 📅 更新历史

| 日期 | 更新内容 | 原因 |
|------|---------|------|
| 2026-05-30 | 更新 deploy.yml | package.json 迁移到 docs/ 目录 |
| 2026-05-30 | 更新 ReadMe.md | 更新文档开发说明 |

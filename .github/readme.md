# Maven Central 发布配置指南

## 前提条件

1. 已在 [Sonatype Central Portal](https://central.sonatype.org/) 注册账号
2. 已创建 Namespace (io.github.xiaomisum)
3. 已生成 GPG 密钥

## GitHub Secrets 配置

在 GitHub 仓库的 Settings -> Secrets and variables -> Actions 中添加以下 Secrets:

### 1. GPG_PRIVATE_KEY

你的 GPG 私钥内容。

**获取方法:**

```bash
# 导出 GPG 私钥
gpg --armor --export-secret-keys your-email@example.com
```

将输出的内容完整复制（包括 `-----BEGIN PGP PRIVATE KEY BLOCK-----` 和 `-----END PGP PRIVATE KEY BLOCK-----`）

### 2. GPG_PASSPHRASE

你的 GPG 私钥密码（生成 GPG 密钥时设置的密码）

### 3. MAVEN_USERNAME

Sonatype Central Portal 的用户名（通常是邮箱或用户名）

### 4. MAVEN_PASSWORD

Sonatype Central Portal 的密码（在 Central Portal 中生成的 token）

**获取方法:**

1. 登录 https://central.sonatype.org/
2. 进入 Account -> User Token
3. 点击 "Generate User Token"
4. 复制 Username 和 Password

## 发布流程

### 方式一: 通过 GitHub Release 触发

1. 在 GitHub 上创建新的 Release
2. Workflow 会自动触发并发布

### 方式二: 手动触发

1. 进入 GitHub Actions 页面
2. 选择 "Publish to Maven Central" workflow
3. 点击 "Run workflow"
4. 选择分支并运行

## 验证发布

发布成功后,可以在以下位置查看:

- Maven Central: https://repo.maven.apache.org/maven2/io/github/xiaomisum/
- Sonatype Central Portal: https://central.sonatype.com/publishing/deployments

## 注意事项

1. 发布前请确保版本号正确
2. GPG 密钥必须有效且未过期
3. 所有模块会使用相同的版本号（在父 pom.xml 中定义）
4. 首次发布需要在 Sonatype 审核通过

## 常见问题

### GPG 签名失败

- 检查 GPG_PRIVATE_KEY 是否正确
- 检查 GPG_PASSPHRASE 是否正确
- 确保 GPG 密钥未过期

### 认证失败

- 检查 MAVEN_USERNAME 和 MAVEN_PASSWORD
- 确保在 Sonatype Central Portal 有正确的权限
- 确认 Namespace 配置正确

### 发布后找不到包

- 等待几分钟同步到 Maven Central
- 检查 Sonatype Central Portal 的发布状态
- 确认 autoPublish 已启用

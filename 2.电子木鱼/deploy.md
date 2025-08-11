# 电子木鱼部署指南

本文档提供详细的部署方案和步骤，帮助您将电子木鱼应用部署到各种平台。

## 📋 部署前准备

### 1. 项目文件检查
确保以下文件存在且完整：
- `index.html` - 主页面
- `styles.css` - 样式文件
- `script.js` - 功能脚本
- `sounds/` - 音效文件夹（可选）

### 2. 音效文件准备
- 将木鱼音效文件放入 `sounds/` 文件夹
- 支持格式：MP3、OGG、WAV
- 建议文件大小：< 100KB

## 🚀 部署方案

### 方案一：静态网站托管（推荐新手）

#### 1. GitHub Pages
**优点**：免费、简单、自动部署
**适用**：个人项目、演示

**步骤**：
```bash
# 1. 创建GitHub仓库
git init
git add .
git commit -m "Initial commit"

# 2. 推送到GitHub
git remote add origin https://github.com/yourusername/woodfish.git
git push -u origin main

# 3. 启用GitHub Pages
# 进入仓库设置 -> Pages -> Source选择main分支
```

**访问地址**：`https://yourusername.github.io/woodfish`

#### 2. Netlify
**优点**：免费、自动部署、CDN加速
**适用**：生产环境

**步骤**：
1. 注册 [Netlify](https://netlify.com)
2. 点击 "New site from Git"
3. 连接GitHub仓库
4. 设置构建命令（留空）
5. 设置发布目录（留空，根目录）
6. 点击 "Deploy site"

**自定义域名**：
- 在站点设置中添加自定义域名
- 配置DNS记录

#### 3. Vercel
**优点**：免费、快速、自动部署
**适用**：现代Web应用

**步骤**：
```bash
# 1. 安装Vercel CLI
npm i -g vercel

# 2. 登录Vercel
vercel login

# 3. 部署
vercel

# 4. 生产环境部署
vercel --prod
```

### 方案二：云服务器部署

#### 1. 阿里云/腾讯云服务器

**服务器配置建议**：
- CPU：1核
- 内存：1GB
- 带宽：1Mbps
- 系统：Ubuntu 20.04 LTS

**部署步骤**：

```bash
# 1. 连接服务器
ssh root@your-server-ip

# 2. 更新系统
apt update && apt upgrade -y

# 3. 安装Nginx
apt install nginx -y

# 4. 上传项目文件
# 使用scp或git clone
scp -r ./电子木鱼 root@your-server-ip:/var/www/

# 5. 配置Nginx
cp nginx.conf /etc/nginx/nginx.conf
nginx -t
systemctl reload nginx

# 6. 配置防火墙
ufw allow 80
ufw allow 443
ufw enable
```

#### 2. 使用Docker部署

```bash
# 1. 构建镜像
docker build -t woodfish .

# 2. 运行容器
docker run -d -p 80:80 --name woodfish-app woodfish

# 3. 查看运行状态
docker ps

# 4. 查看日志
docker logs woodfish-app
```

**Docker Compose**：
```yaml
version: '3.8'
services:
  woodfish:
    build: .
    ports:
      - "80:80"
    restart: unless-stopped
    volumes:
      - ./logs:/var/log/nginx
```

### 方案三：CDN加速

#### 1. Cloudflare
**步骤**：
1. 注册 [Cloudflare](https://cloudflare.com)
2. 添加域名
3. 更新DNS记录
4. 启用CDN和缓存
5. 配置SSL证书

**优化设置**：
- 启用Brotli压缩
- 设置缓存规则
- 启用HTTP/3

#### 2. 阿里云CDN
**步骤**：
1. 在阿里云控制台创建CDN加速域名
2. 配置源站信息
3. 设置缓存规则
4. 配置HTTPS证书

### 方案四：小程序部署

#### 微信小程序
**转换步骤**：
1. 创建小程序项目
2. 将HTML转换为WXML
3. 将CSS转换为WXSS
4. 将JavaScript适配小程序API

**关键适配**：
```javascript
// 音频播放适配
const innerAudioContext = wx.createInnerAudioContext()
innerAudioContext.src = '/sounds/woodfish.mp3'
innerAudioContext.play()
```

#### 支付宝小程序
**步骤**：
1. 使用支付宝小程序开发工具
2. 适配支付宝小程序框架
3. 配置音频播放权限

## 🔧 性能优化

### 1. 文件压缩
```bash
# 压缩CSS
npm install -g clean-css-cli
cleancss -o styles.min.css styles.css

# 压缩JS
npm install -g uglify-js
uglifyjs script.js -o script.min.js
```

### 2. 图片优化
- 使用WebP格式
- 压缩图片大小
- 使用CDN加速

### 3. 缓存策略
```nginx
# Nginx缓存配置
location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg|mp3|ogg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

## 🔒 安全配置

### 1. HTTPS配置
```nginx
# Nginx HTTPS配置
server {
    listen 443 ssl http2;
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    # ... 其他配置
}
```

### 2. 安全头设置
```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header X-Content-Type-Options "nosniff" always;
```

### 3. 防火墙配置
```bash
# UFW防火墙
ufw allow 80
ufw allow 443
ufw deny 22
ufw enable
```

## 📊 监控和维护

### 1. 日志监控
```bash
# 查看Nginx访问日志
tail -f /var/log/nginx/access.log

# 查看错误日志
tail -f /var/log/nginx/error.log
```

### 2. 性能监控
- 使用Google Analytics
- 配置错误监控
- 设置性能指标

### 3. 备份策略
```bash
# 自动备份脚本
#!/bin/bash
tar -czf backup-$(date +%Y%m%d).tar.gz /var/www/woodfish
```

## 🚨 故障排除

### 常见问题

1. **音效无法播放**
   - 检查文件路径
   - 确认浏览器支持
   - 检查CORS设置

2. **页面加载缓慢**
   - 启用Gzip压缩
   - 使用CDN加速
   - 优化图片大小

3. **移动端显示异常**
   - 检查响应式设计
   - 测试触摸事件
   - 验证音效播放

### 调试工具
- 浏览器开发者工具
- 网络分析工具
- 性能分析工具

## 📞 技术支持

如果遇到部署问题，可以：
1. 查看项目文档
2. 提交GitHub Issue
3. 联系技术支持

---

**祝您部署顺利！** 🎉 